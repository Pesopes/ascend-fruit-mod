package com.pesopes.ascendfruit;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AscendFruit implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("ascendfruit");
    public static final String MOD_ID = "ascendfruit";
    public static final TagKey<Block> NOT_ASCENDABLE = TagKey.of(RegistryKeys.BLOCK, Identifier.of("ascendfruit", "not_ascendable"));

    @Override
    public void onInitialize() {
        CustomSounds.initialize();
        ModItems.initialize();
        PayloadTypeRegistry.playS2C().register(CustomParticleUtil.SendParticlePayload.ID, CustomParticleUtil.SendParticlePayload.CODEC);
    }
}