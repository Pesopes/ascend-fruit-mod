package com.pesopes.ascendfruit;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Vector3f;

public class CustomParticleUtil {
    public final static Identifier PARTICLE_PAYLOAD_ID = Identifier.of(AscendFruit.MOD_ID, "ascend_particle");

//    public class ParticleEffectCodec {
//        public static final Codec<ParticleEffect> CODEC = Codec.STRING.xmap(
//                id -> Registry.PARTICLE_TYPE.get(new Identifier(id)).create(),
//                effect -> Registry.PARTICLE_TYPE.getId(effect.getType()).toString()
//        );
//    }

    public record SendParticlePayload(BlockPos pos, Direction direction, Vector3f velocity,
                                      double offsetMultiplier) implements CustomPayload {
        public static final CustomPayload.Id<SendParticlePayload> ID = new CustomPayload.Id<>(PARTICLE_PAYLOAD_ID);

        public static final PacketCodec<RegistryByteBuf, SendParticlePayload> CODEC = PacketCodec.tuple(
                BlockPos.PACKET_CODEC, SendParticlePayload::pos,
                Direction.PACKET_CODEC, SendParticlePayload::direction,
//                ParticleEffect.PACKET_CODE /*<--- THIS DOES NOT EXIST*/, SendParticlePayload::effect,
                PacketCodecs.VECTOR3F, SendParticlePayload::velocity,
                PacketCodecs.DOUBLE, SendParticlePayload::offsetMultiplier,
                SendParticlePayload::new
        );

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}
