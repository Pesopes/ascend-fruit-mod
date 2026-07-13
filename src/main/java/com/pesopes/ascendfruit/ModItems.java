package com.pesopes.ascendfruit;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
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

public class ModItems{
    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings) {
        // Create the item key.
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AscendFruit.MOD_ID, name));

        // Create the item instance.
        T item = itemFactory.apply(settings.setId(itemKey));

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
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FOOD_AND_DRINKS).register((itemGroup) -> itemGroup.accept(ASCEND_FRUIT));

//        Registry.register(Registries.SOUND_EVENT, Identifier.of(AscendFruit.MOD_ID, "ascend_fruit_teleport"),
//                SoundEvent.of(Identifier.of(AscendFruit.MOD_ID, "ascend_fruit_teleport")));
//        CustomBrewRecipeRegister.registerCustomRecipe(Items.CHORUS_FRUIT, Items.WIND_CHARGE, ASCEND_FRUIT);
//        FabricPotionBrewingBuilder.BUILD.register(builder -> {
//            // Cast the builder to access the underlying vanilla list directly, bypassing expectPotion() validation!
//            if (builder instanceof net.minecraft.world.item.alchemy.PotionBrewing.Builder vanillaBuilder) {
//                vanillaBuilder.containerMixes.add(new net.minecraft.world.item.alchemy.PotionBrewing.Mix<>(
//                        net.minecraft.world.item.Items.CHORUS_FRUIT.builtInRegistryHolder(),
//                        net.minecraft.world.item.crafting.Ingredient.of(net.minecraft.world.item.Items.WIND_CHARGE),
//                        ASCEND_FRUIT.builtInRegistryHolder()
//                ));
//            }
//        });
//        FabricPotionBrewingBuilder.BUILD.register(builder -> {
//            builder.addContainer(Items.CHORUS_FRUIT);
//            builder.addContainerRecipe(Items.CHORUS_FRUIT, Items.WIND_CHARGE, ModItems.ASCEND_FRUIT);
//        });
    }
}