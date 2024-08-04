package com.pesopes.ascendfruit;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;

public class AscendFruitItem extends Item {
    public AscendFruitItem(Settings settings) {
        super(settings);
    }

    private static final int maxCeilingDistance = 8;
    private static final int maxTerrainDistance = 200;


    // TODO: what if there's ocean above you (probably teleport but then what about lava)
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack itemStack = super.finishUsing(stack, world, user);
        if (!world.isClient) {
            Vec3d userPos = user.getPos();
            Vec3d targetPos = findBestTarget(world, user);
            // If nothing found cancel the teleport
            if (targetPos == null) {
                //TODO: add failed teleport sound
                return itemStack;
            }
            if (user.hasVehicle()) {
                user.stopRiding();
            }
            if (user.teleport(targetPos.x, targetPos.y, targetPos.z, true)) {
                world.emitGameEvent(GameEvent.TELEPORT, userPos, GameEvent.Emitter.of(user));
                SoundCategory soundCategory;
                SoundEvent soundEvent;
                if (user instanceof FoxEntity) {
                    soundEvent = CustomSounds.ASCEND_FRUIT_TELEPORT;
                    soundCategory = SoundCategory.NEUTRAL;
                } else {
                    soundEvent = CustomSounds.ASCEND_FRUIT_TELEPORT;
                    soundCategory = SoundCategory.PLAYERS;
                }
                //TODO: add particle effect that have a high upwards velocity
                world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, soundCategory);
                user.onLanding();
            }

            if (user instanceof PlayerEntity playerEntity) {
                playerEntity.clearCurrentExplosion();
                playerEntity.getItemCooldownManager().set(this, 60);
            }
        }

        return itemStack;
    }

    // Returns blocks adjacent to the entity factoring in its bounding box
    private ArrayList<Vec3d> getAdjacentPositions(LivingEntity user) {
        Box boundingBox = user.getBoundingBox();
        double closeX = MathHelper.clamp(roundCoord(user.getX()), boundingBox.minX, boundingBox.maxX - 0.001);
        double closeZ = MathHelper.clamp(roundCoord(user.getZ()), boundingBox.minZ, boundingBox.maxZ - 0.001);
        ArrayList<Vec3d> adjacentPositions = new ArrayList<>();
        adjacentPositions.add(new Vec3d(user.getX(), user.getY(), user.getZ()));
        adjacentPositions.add(new Vec3d(closeX, user.getY(), user.getZ()));
        adjacentPositions.add(new Vec3d(user.getX(), user.getY(), closeZ));
        adjacentPositions.add(new Vec3d(closeX, user.getY(), closeZ));

        return adjacentPositions;
    }

    // Rounds either one higher or one lower but very close to the original coord
    private double roundCoord(double coord) {
        int floored = MathHelper.floor(coord);
        double fraction = coord - floored;
        if (fraction >= 0.5) {
            return floored + 1;
        } else {
            return floored - 0.001;
        }
    }

    private Vec3d findBestTarget(World world, LivingEntity user) {
        BlockPos targetPos = null;
        Vec3d userPos = user.getPos();
        double targetX = userPos.getX();
        double targetZ = userPos.getZ();

        ArrayList<Vec3d> adjacentPositions = getAdjacentPositions(user);
        for (Vec3d adjacentPosition : adjacentPositions) {
            BlockPos potentialTarget = findTarget(world, BlockPos.ofFloored(adjacentPosition));
            if (potentialTarget == null) {
                continue;
            }
            if (targetPos == null) {
                targetPos = potentialTarget;
                targetX = adjacentPosition.getX();
                targetZ = adjacentPosition.getZ();
                continue;
            }
            if (potentialTarget.getY() > targetPos.getY()) {
                targetPos = potentialTarget;
                targetX = adjacentPosition.getX();
                targetZ = adjacentPosition.getZ();
            }
        }
        if (targetPos == null) {
            return null;
        }
        return new Vec3d(targetX, targetPos.getY(), targetZ);
    }

    // Combines findCeiling and findTop
    private BlockPos findTarget(World world, BlockPos startPos) {
        BlockPos ceilingPos = findCeiling(world, startPos);
        if (ceilingPos != null) {
            return findTop(world, ceilingPos);
        }
        return null;
    }

    // Finds the first non-air block above player
    private BlockPos findCeiling(World world, BlockPos startPos) {
        for (int y = 1; y < maxCeilingDistance; y++) {
            BlockPos pos = startPos.up(y);
            if (!world.isAir(pos) && !world.getBlockState(pos).isIn(AscendFruit.NOT_ASCENDABLE)) {
                return pos;
            }
        }
        // no place found
        return null;
    }

    // Finds the first air block from some starting position (can go through small gaps
    private BlockPos findTop(World world, BlockPos ceilingPos) {
        for (int y = 0; y < maxTerrainDistance; y++) {
            BlockPos tpos = ceilingPos.up(y);
            // Trying to go through a not ascendable block is not possible
            if (world.getBlockState(tpos).isIn(AscendFruit.NOT_ASCENDABLE)) {
                return null;
            }
            if (world.isAir(tpos)) {
                // FIXME: a fox could actually fit here so check more thoroughly (maybe using the entity's bounding box)
                if (world.isAir(tpos.up(1))) {
                    return tpos;
                }
                // Found air gap where player can't fit -> continue past it
                continue;
            }
        }
        return null;
    }
}
