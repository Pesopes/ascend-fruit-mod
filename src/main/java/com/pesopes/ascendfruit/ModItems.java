package com.pesopes.ascendfruit;

import me.emafire003.dev.custombrewrecipes.CustomBrewRecipeRegister;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;

import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

public class ModItems {

//    public static Item register(Item item, String name) {
//        Identifier itemID = Identifier.of(AscendFruit.MOD_ID, name);
//
//        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, itemID);
//
////        Item.Settings settings = new Item.Settings().registryKey(key);
//
//        Item registeredItem = Registry.register(Registries.ITEM, key, item);
//
//        return registeredItem;
//    }

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(AscendFruit.MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static final FoodComponent ASCEND_FRUIT_FOOD_COMPONENT = (new FoodComponent.Builder()).nutrition(0).saturationModifier(0.0F).alwaysEdible().build();
    public static final ConsumableComponent ASCEND_FRUIT_CONSUMABLE_COMPONENT = ConsumableComponent.builder().useAction(UseAction.EAT).sound(SoundEvents.ENTITY_GENERIC_EAT).consumeParticles(true).consumeSeconds(2.4F).build();

    public static final Item ASCEND_FRUIT = register(
            "ascend_fruit",
            settings -> new AscendFruitItem(settings.food(ASCEND_FRUIT_FOOD_COMPONENT, ASCEND_FRUIT_CONSUMABLE_COMPONENT)),
            new Item.Settings()
    );

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register((itemGroup) -> itemGroup.add(ASCEND_FRUIT));

//        Registry.register(Registries.SOUND_EVENT, Identifier.of(AscendFruit.MOD_ID, "ascend_fruit_teleport"),
//                SoundEvent.of(Identifier.of(AscendFruit.MOD_ID, "ascend_fruit_teleport")));
        CustomBrewRecipeRegister.registerCustomRecipe(Items.CHORUS_FRUIT, Items.WIND_CHARGE, ASCEND_FRUIT);
    }
}