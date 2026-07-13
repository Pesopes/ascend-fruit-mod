package com.pesopes.ascendfruit;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;

public class CustomSounds {
    private CustomSounds() {
    }


    public static final SoundEvent ASCEND_FRUIT_TELEPORT = registerSound("ascend_fruit_teleport");
    public static final SoundEvent ASCEND_FRUIT_ERROR = registerSound("ascend_fruit_error");

    // actual registration of all the custom SoundEvents
    private static SoundEvent registerSound(String id) {
        Identifier identifier = Identifier.fromNamespaceAndPath(AscendFruit.MOD_ID, id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
    }


    public static void initialize() {
        AscendFruit.LOGGER.info("Registering " + AscendFruit.MOD_ID + " Sounds");

    }
}