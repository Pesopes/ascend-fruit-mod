package com.pesopes.ascendfruit;

import me.emafire003.dev.custombrewrecipes.CustomBrewRecipeRegister;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.Identifier;

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

    public static Item register(String name, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        // Create the item key.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AscendFruit.MOD_ID, name));

        // Create the item instance.
        Item item = itemFactory.apply(settings.setId(itemKey));

        // Register the item.
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static final FoodProperties ASCEND_FRUIT_FOOD_COMPONENT = (new FoodProperties.Builder()).nutrition(0).saturationModifier(0.0F).alwaysEdible().build();
    public static final Consumable ASCEND_FRUIT_CONSUMABLE_COMPONENT = Consumable.builder().animation(ItemUseAnimation.EAT).sound(SoundEvents.GENERIC_EAT).hasConsumeParticles(true).consumeSeconds(2.4F).build();

    public static final Item ASCEND_FRUIT = register(
            "ascend_fruit",
            settings -> new AscendFruitItem(settings.food(ASCEND_FRUIT_FOOD_COMPONENT, ASCEND_FRUIT_CONSUMABLE_COMPONENT)),
            new Item.Properties()
    );

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register((itemGroup) -> itemGroup.accept(ASCEND_FRUIT));

//        Registry.register(Registries.SOUND_EVENT, Identifier.of(AscendFruit.MOD_ID, "ascend_fruit_teleport"),
//                SoundEvent.of(Identifier.of(AscendFruit.MOD_ID, "ascend_fruit_teleport")));
        CustomBrewRecipeRegister.registerCustomRecipe(Items.CHORUS_FRUIT, Items.WIND_CHARGE, ASCEND_FRUIT);
    }
}