package com.pesopes.ascendfruit;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.ParticleUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class AscendFruitClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AscendFruit.LOGGER.info("Client init");

        ClientPlayNetworking.registerGlobalReceiver(CustomParticleUtil.SendParticlePayload.ID, (payload, context) -> {
            context.client().execute(() -> {

                AscendFruit.LOGGER.info("SENT PACKET!!!!");
                for (int i = 0; i < 100; i++) {

                    ParticleUtil.spawnParticle(context.client().world,
                            payload.pos(),
                            payload.direction(),
                            ParticleTypes.CRIT,
                            new Vec3d(payload.velocity()),
                            payload.offsetMultiplier());
                }
            });
        });
    }
}
