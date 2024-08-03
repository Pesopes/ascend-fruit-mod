package com.pesopes.ascendfruit;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class CustomSounds {
    private CustomSounds() {
    }


    public static final SoundEvent ASCEND_FRUIT_TELEPORT = registerSound("ascend_fruit_teleport");

    // actual registration of all the custom SoundEvents
    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.of(AscendFruit.MOD_ID, id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }


    public static void initialize() {
        AscendFruit.LOGGER.info("Registering " + AscendFruit.MOD_ID + " Sounds");

    }
}