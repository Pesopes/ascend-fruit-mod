package com.pesopes.ascendfruit;

import me.emafire003.dev.custombrewrecipes.CustomBrewRecipeRegister;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;

import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Optional;

public class ModItems {

    public static Item register(Item item, String id) {
        Identifier itemID = Identifier.of(AscendFruit.MOD_ID, id);
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);
        return registeredItem;
    }

    public static final FoodComponent ASCEND_FRUIT_COMPONENT = new FoodComponent(0, 0, true, 2.4F, Optional.empty(), new ArrayList<>());

    public static final Item ASCEND_FRUIT = register(
            new AscendFruitItem(new Item.Settings().food(ASCEND_FRUIT_COMPONENT)),
            "ascend_fruit"
    );

    public static void initialize() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register((itemGroup) -> itemGroup.add(ASCEND_FRUIT));

        Registry.register(Registries.SOUND_EVENT, Identifier.of(AscendFruit.MOD_ID, "ascend_fruit_teleport"),
                SoundEvent.of(Identifier.of(AscendFruit.MOD_ID, "ascend_fruit_teleport")));
        CustomBrewRecipeRegister.registerCustomRecipe(Items.CHORUS_FRUIT, Items.WIND_CHARGE, ASCEND_FRUIT);
    }
}