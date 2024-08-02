package com.pesopes.ascendfruit;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.types.templates.List;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class AscendFruitItem extends Item {
    public AscendFruitItem(Settings settings) {
        super(settings);
    }

    private static final int maxCeilingDistance = 10;
    private static final int maxTerrainDistance = 100;


    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack itemStack = super.finishUsing(stack, world, user);
        if (!world.isClient) {

            Vec3d userPos = user.getPos();
            BlockPos targetPos = findTarget(world, BlockPos.ofFloored(userPos));
            if (targetPos == null) {
                return itemStack;
            }

            if (user.hasVehicle()) {
                user.stopRiding();
            }
            if (user.teleport(userPos.getX(), targetPos.getY(), userPos.getZ(), true)) {
                world.emitGameEvent(GameEvent.TELEPORT, userPos, GameEvent.Emitter.of(user));
                SoundCategory soundCategory;
                SoundEvent soundEvent;
                if (user instanceof FoxEntity) {
                    soundEvent = SoundEvents.ENTITY_FOX_TELEPORT;
                    soundCategory = SoundCategory.NEUTRAL;
                } else {
                    soundEvent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
                    soundCategory = SoundCategory.PLAYERS;
                }

                world.playSound((PlayerEntity) null, user.getX(), user.getY(), user.getZ(), soundEvent, soundCategory);
                user.onLanding();
            }

            if (user instanceof PlayerEntity playerEntity) {
                playerEntity.clearCurrentExplosion();
                playerEntity.getItemCooldownManager().set(this, 60);
            }
        }

        return itemStack;
    }

    private BlockPos findTarget(World world, BlockPos startPos) {
        BlockPos ceilingPos = findCeiling(world, startPos);
        if (ceilingPos != null) {
            return findTop(world, ceilingPos);
        }
        return null;
    }

    private BlockPos findCeiling(World world, BlockPos startPos) {
        // first find the ceiling beginning
        for (int y = 0; y < maxCeilingDistance; y++) {
            BlockPos pos = startPos.up(y);
            if (!world.isAir(pos) && !world.getBlockState(pos).isIn(AscendFruit.NOT_ASCENDABLE)) {

                return pos;
            }
        }

        // no place found
        return null;
    }

    private BlockPos findTop(World world, BlockPos ceilingPos) {
        for (int y = 0; y < maxTerrainDistance; y++) {
            BlockPos tpos = ceilingPos.up(y);
            // Trying to go through a not ascendable block is not possible
            if (world.getBlockState(tpos).isIn(AscendFruit.NOT_ASCENDABLE)) {
                return null;
            }
            if (world.isAir(tpos)) {
                if (world.isAir(tpos.up(1))) {
                    return tpos;
                }
                // Found air gap but player cant fit
                return null;
            }
        }
        return null;
    }
}
