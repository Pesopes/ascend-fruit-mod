package com.pesopes.ascendfruit;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.item.Item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Optional;

public class ModItems {

    public static Item register(Item item, String id) {
        // Create the identifier for the item.
        Identifier itemID = Identifier.of(AscendFruit.MOD_ID, id);

        // Register the item.
        Item registeredItem = Registry.register(Registries.ITEM, itemID, item);

        // Return the registered item!
        return registeredItem;
    }

    public static final FoodComponent ASCEND_FRUIT_COMPONENT = new FoodComponent(0, 0, true, 3.2F, Optional.empty(), new ArrayList<>());

    public static final Item ASCEND_FRUIT = register(
            new AscendFruitItem(new Item.Settings().food(ASCEND_FRUIT_COMPONENT)),
            "ascend_fruit"
    );

    public static void initialize() {

//        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS)
//                .register((itemGroup) -> itemGroup.add(ModItems.SUSPICIOUS_SUBSTANCE));
//

//        Registry.register(Registries.ITEM_GROUP, CUSTOM_ITEM_GROUP_KEY, CUSTOM_ITEM_GROUP);
//
//        ItemGroupEvents.modifyEntriesEvent(CUSTOM_ITEM_GROUP_KEY).register(itemGroup -> {
//            itemGroup.add(ModItems.SUSPICIOUS_SUBSTANCE);
//        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register((itemGroup) -> itemGroup.add(ASCEND_FRUIT));
    }
}