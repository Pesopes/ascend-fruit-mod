package com.pesopes.ascendfruit;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

public class CustomPackets {
    public final static Identifier PARTICLE_PAYLOAD_ID = Identifier.fromNamespaceAndPath(AscendFruit.MOD_ID, "ascend_particle");


    public record SendParticlePayload(Vector3f pos, Direction direction, Vector3f velocity,
                                      double offsetMultiplier) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<SendParticlePayload> ID = new CustomPacketPayload.Type<>(PARTICLE_PAYLOAD_ID);

        public static final StreamCodec<RegistryFriendlyByteBuf, SendParticlePayload> CODEC = StreamCodec.composite(
                ByteBufCodecs.VECTOR3F, SendParticlePayload::pos,
                Direction.STREAM_CODEC, SendParticlePayload::direction,
//                ParticleEffect.PACKET_CODE /*<--- THIS DOES NOT EXIST*/, SendParticlePayload::effect,
                ByteBufCodecs.VECTOR3F, SendParticlePayload::velocity,
                ByteBufCodecs.DOUBLE, SendParticlePayload::offsetMultiplier,
                (pos, direction, velocity, offsetMultiplier) -> new SendParticlePayload(new Vector3f(pos), direction, new Vector3f(velocity), offsetMultiplier)
//                SendParticlePayload::new
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }


}
