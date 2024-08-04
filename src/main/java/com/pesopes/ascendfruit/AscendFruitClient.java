package com.pesopes.ascendfruit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

public class AscendFruitClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AscendFruit.LOGGER.info("Client init");

        ClientPlayNetworking.registerGlobalReceiver(CustomParticleUtil.SendParticlePayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                Random random = world.random;
                Vector3f vel = payload.velocity();
                Vector3f pos = payload.pos();
                //TODO: make a nice shape using the particles (a circle at least)
                for (int i = 0; i < 100; i++) {
                    double x = (double) pos.x + random.nextDouble() - 0.5D;
                    double y = (double) pos.y - 0.05;
                    double z = (double) pos.z + random.nextDouble() - 0.5D;

                    world.addParticle(
                            ParticleTypes.REVERSE_PORTAL,
                            x, y, z, vel.x, vel.y, vel.z);
                }
            });
        });
    }
}
