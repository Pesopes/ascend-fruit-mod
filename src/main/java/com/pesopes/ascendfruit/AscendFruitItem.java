package com.pesopes.ascendfruit;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.joml.Vector3f;

import java.util.ArrayList;


public class AscendFruitItem extends Item {
    public AscendFruitItem(Settings settings) {
        super(settings);
    }

    private static final int maxCeilingDistance = 8;
    private static final int maxTerrainDistance = 200;


    // TODO: what if there's ocean above you (probably teleport but then what about lava)
    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {

        if (!world.isClient) {
            Vec3d userPos = user.getPos();

            // Searches above and around the entity for the target position to teleport to
            Vec3d targetPos = findBestTarget(world, user);
            // If nothing found cancel the teleport

            if (user.hasVehicle()) {
                user.stopRiding();
            }
            // "Borrowed" from the chorus fruit implementation
            if (targetPos != null && user.teleport(targetPos.x, targetPos.y, targetPos.z, false)) {
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

                // Find players in 32 block area (that's what the /particle commands uses normally)
                // Send a packet to display particles for both the starting and ending positions
                //FIXME: when teleporting a long distance you can't see particles, even the top ones which should be visible always
                for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world, userPos, 32.0)) {
                    ServerPlayNetworking.send(player, new CustomPackets.SendParticlePayload(userPos.toVector3f(), Direction.UP, new Vector3f(1.0F, 4.0F, 1.0F), 1));
                }
                for (ServerPlayerEntity player : PlayerLookup.around((ServerWorld) world, targetPos, 32.0)) {
                    ServerPlayNetworking.send(player, new CustomPackets.SendParticlePayload(targetPos.toVector3f(), Direction.UP, new Vector3f(-1.0F, 4.0F, -1.0F), 1));
                }

                world.playSound(null, user.getX(), user.getY(), user.getZ(), soundEvent, soundCategory);
                user.onLanding();
                if (user instanceof PlayerEntity playerEntity) {
                    playerEntity.clearCurrentExplosion();
                    playerEntity.getItemCooldownManager().set(this, 60);
                }
                FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);
                return foodComponent != null ? user.eatFood(world, stack, foodComponent) : stack;
            } else {
                SoundCategory soundCategory;
                if (user instanceof FoxEntity) {
                    soundCategory = SoundCategory.NEUTRAL;
                } else {
                    soundCategory = SoundCategory.PLAYERS;
                }
                world.playSound(null, user.getX(), user.getY(), user.getZ(), CustomSounds.ASCEND_FRUIT_ERROR, soundCategory);
                if (user instanceof PlayerEntity playerEntity) {

                    playerEntity.getItemCooldownManager().set(this, 10);
                    return stack;
                }
            }


        } else {

            // Searches above and around the entity for the target position to teleport to
            Vec3d targetPos = findBestTarget(world, user);
            if (targetPos == null || !user.teleport(targetPos.x, targetPos.y, targetPos.z, false)) {
                return stack;
            } else {
                FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);
                return foodComponent != null ? user.eatFood(world, stack, foodComponent) : stack;
            }
        }
        // This shouldn't technically ever be called
        FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);
        return foodComponent != null ? user.eatFood(world, stack, foodComponent) : stack;
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
        // Goes through potential targets and gets the highest one
        for (Vec3d adjacentPosition : adjacentPositions) {
            BlockPos potentialTarget = findTarget(world, BlockPos.ofFloored(adjacentPosition), user);
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
    private BlockPos findTarget(World world, BlockPos startPos, LivingEntity user) {
        BlockPos ceilingPos = findCeiling(world, startPos);
        if (ceilingPos != null) {
            return findTop(world, ceilingPos, user);
        }
        return null;
    }

    // Finds the first non-air block above player
    private BlockPos findCeiling(World world, BlockPos startPos) {
        for (int y = 1; y < maxCeilingDistance; y++) {
            BlockPos pos = startPos.up(y);
            if (world.getBlockState(pos).blocksMovement() && !world.getBlockState(pos).isIn(AscendFruit.NOT_ASCENDABLE)) {
                return pos;
            }
        }
        // no place found
        return null;
    }

    // Finds the first air block from some starting position (can go through small gaps
    private BlockPos findTop(World world, BlockPos ceilingPos, LivingEntity user) {
        for (int y = 0; y < maxTerrainDistance; y++) {
            BlockPos tpos = ceilingPos.up(y);
            // Trying to go through a not ascendable block is not possible
            if (world.getBlockState(tpos).isIn(AscendFruit.NOT_ASCENDABLE)) {
                return null;
            }
            if (!world.getBlockState(tpos).blocksMovement() && !world.containsFluid(user.getBoundingBox())) {
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
