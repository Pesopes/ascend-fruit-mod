package com.pesopes.ascendfruit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

public class AscendFruitClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AscendFruit.LOGGER.debug("Ascend Fruit Client init");

        ClientPlayNetworking.registerGlobalReceiver(CustomPackets.SendParticlePayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                Random random = world.random;
                Vector3f vel = payload.velocity();
                Vector3f pos = payload.pos();
                final double radius = 0.8;
                final int particleCount = 50;
                //TODO: make a nice shape using the particles (a circle at least)
                for (int i = 0; i < particleCount; i++) {
                    float fract = (float) i / particleCount;
                    double angle = 2 * MathHelper.PI * fract;
                    double x = (double) pos.x + radius * MathHelper.cos((float) angle);
                    double y = (double) pos.y - 0.05;
                    double z = (double) pos.z + radius * MathHelper.sin((float) angle);

                    world.addParticleClient(
                            ParticleTypes.REVERSE_PORTAL,
                            x, y, z, (pos.x - x) * vel.x * random.nextDouble(), vel.y + random.nextDouble(), (pos.z - z) * vel.z * random.nextDouble());
                }
            });
        });

    }
}
