package com.pesopes.ascendfruit;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.ArrayList;


public class AscendFruitItem extends Item {
    public AscendFruitItem(Properties settings) {
        super(settings);
    }

    private static final int maxCeilingDistance = 8;
    private static final int maxTerrainDistance = 200;


    // TODO: what if there's ocean above you (probably teleport but then what about lava)
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {

        if (!world.isClientSide()) {
            Vec3 userPos = user.position();

            // Searches above and around the entity for the target position to teleport to
            Vec3 targetPos = findBestTarget(world, user);
            // If nothing found cancel the teleport

            if (user.isPassenger()) {
                user.stopRiding();
            }
            // "Borrowed" from the chorus fruit implementation
            if (targetPos != null && user.randomTeleport(targetPos.x, targetPos.y, targetPos.z, false)) {
                world.gameEvent(GameEvent.TELEPORT, userPos, GameEvent.Context.of(user));
                SoundSource soundCategory;
                SoundEvent soundEvent = CustomSounds.ASCEND_FRUIT_TELEPORT;
                if (user instanceof Fox) {
                    soundCategory = SoundSource.NEUTRAL;
                } else {
                    soundCategory = SoundSource.PLAYERS;
                }

                // Find players in 32 block area (that's what the /particle commands uses normally)
                // Send a packet to display particles for both the starting and ending positions
                //FIXME: when teleporting a long distance you can't see particles, even the top ones which should be visible always
                for (ServerPlayer player : PlayerLookup.around((ServerLevel) world, userPos, 32.0)) {
                    ServerPlayNetworking.send(player, new CustomPackets.SendParticlePayload(userPos.toVector3f(), Direction.UP, new Vector3f(1.0F, 4.0F, 1.0F), 1));
                }
                for (ServerPlayer player : PlayerLookup.around((ServerLevel) world, targetPos, 32.0)) {
                    ServerPlayNetworking.send(player, new CustomPackets.SendParticlePayload(targetPos.toVector3f(), Direction.UP, new Vector3f(-1.0F, 4.0F, -1.0F), 1));
                }

                world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, soundCategory);
                user.resetFallDistance();
                if (user instanceof Player playerEntity) {
                    playerEntity.resetCurrentImpulseContext();
                    playerEntity.getCooldowns().addCooldown(stack, 60);
                }
                FoodProperties foodComponent = stack.get(DataComponents.FOOD);
                Consumable consumableComponent = (Consumable)stack.get(DataComponents.CONSUMABLE);
                return consumableComponent != null ? consumableComponent.onConsume(world, user, stack) : stack;
            } else {
                SoundSource soundCategory;
                if (user instanceof Fox) {
                    soundCategory = SoundSource.NEUTRAL;
                } else {
                    soundCategory = SoundSource.PLAYERS;
                }
                world.playSound(null, user.getX(), user.getY(), user.getZ(), CustomSounds.ASCEND_FRUIT_ERROR, soundCategory);
                if (user instanceof Player playerEntity) {

                    playerEntity.getCooldowns().addCooldown(stack, 10);
                    return stack;
                }
            }


        } else {

            // Searches above and around the entity for the target position to teleport to
            Vec3 targetPos = findBestTarget(world, user);
            if (targetPos == null || !user.randomTeleport(targetPos.x, targetPos.y, targetPos.z, false)) {
                return stack;
            } else {
                FoodProperties foodComponent = stack.get(DataComponents.FOOD);
//                return foodComponent != null ? user.eatFood(world, stack, foodComponent) : stack;
                Consumable consumableComponent = (Consumable)stack.get(DataComponents.CONSUMABLE);
                return consumableComponent != null ? consumableComponent.onConsume(world, user, stack) : stack;
            }
        }
        // This shouldn't technically ever be called
        FoodProperties foodComponent = stack.get(DataComponents.FOOD);
        Consumable consumableComponent = (Consumable)stack.get(DataComponents.CONSUMABLE);
        return consumableComponent != null ? consumableComponent.onConsume(world, user, stack) : stack;
    }

    // Returns blocks adjacent to the entity factoring in its bounding box
    private ArrayList<Vec3> getAdjacentPositions(LivingEntity user) {
        AABB boundingBox = user.getBoundingBox();
        double closeX = Mth.clamp(roundCoord(user.getX()), boundingBox.minX, boundingBox.maxX - 0.001);
        double closeZ = Mth.clamp(roundCoord(user.getZ()), boundingBox.minZ, boundingBox.maxZ - 0.001);
        ArrayList<Vec3> adjacentPositions = new ArrayList<>();
        adjacentPositions.add(new Vec3(user.getX(), user.getY(), user.getZ()));
        adjacentPositions.add(new Vec3(closeX, user.getY(), user.getZ()));
        adjacentPositions.add(new Vec3(user.getX(), user.getY(), closeZ));
        adjacentPositions.add(new Vec3(closeX, user.getY(), closeZ));

        return adjacentPositions;
    }

    // Rounds either one higher or one lower but very close to the original coord
    private double roundCoord(double coord) {
        int floored = Mth.floor(coord);
        double fraction = coord - floored;
        if (fraction >= 0.5) {
            return floored + 1;
        } else {
            return floored - 0.001;
        }
    }

    private Vec3 findBestTarget(Level world, LivingEntity user) {
        BlockPos targetPos = null;
        Vec3 userPos = user.position();
        double targetX = userPos.x();
        double targetZ = userPos.z();

        ArrayList<Vec3> adjacentPositions = getAdjacentPositions(user);
        // Goes through potential targets and gets the highest one
        for (Vec3 adjacentPosition : adjacentPositions) {
            BlockPos potentialTarget = findTarget(world, BlockPos.containing(adjacentPosition), user);
            if (potentialTarget == null) {
                continue;
            }
            if (targetPos == null) {
                targetPos = potentialTarget;
                targetX = adjacentPosition.x();
                targetZ = adjacentPosition.z();
                continue;
            }
            if (potentialTarget.getY() > targetPos.getY()) {
                targetPos = potentialTarget;
                targetX = adjacentPosition.x();
                targetZ = adjacentPosition.z();
            }
        }
        if (targetPos == null) {
            return null;
        }
        return new Vec3(targetX, targetPos.getY(), targetZ);
    }

    // Combines findCeiling and findTop
    private BlockPos findTarget(Level world, BlockPos startPos, LivingEntity user) {
        BlockPos ceilingPos = findCeiling(world, startPos);
        if (ceilingPos != null) {
            return findTop(world, ceilingPos, user);
        }
        return null;
    }

    // Finds the first non-air block above player
    private BlockPos findCeiling(Level world, BlockPos startPos) {
        for (int y = 1; y < maxCeilingDistance; y++) {
            BlockPos pos = startPos.above(y);
            if (world.getBlockState(pos).blocksMotion() && !world.getBlockState(pos).is(AscendFruit.NOT_ASCENDABLE)) {
                return pos;
            }
        }
        // no place found
        return null;
    }

    // Finds the first air block from some starting position (can go through small gaps
    private BlockPos findTop(Level world, BlockPos ceilingPos, LivingEntity user) {
        for (int y = 0; y < maxTerrainDistance; y++) {
            BlockPos tpos = ceilingPos.above(y);
            // Trying to go through a not ascendable block is not possible
            if (world.getBlockState(tpos).is(AscendFruit.NOT_ASCENDABLE)) {
                return null;
            }
            if (!world.getBlockState(tpos).blocksMotion() && !world.containsAnyLiquid(user.getBoundingBox())) {
                // FIXME: a fox could actually fit here so check more thoroughly (maybe using the entity's bounding box)
                if (world.isEmptyBlock(tpos.above(1))) {
                    return tpos;
                }
                // Found air gap where player can't fit -> continue past it
                continue;
            }
        }
        return null;
    }
}
