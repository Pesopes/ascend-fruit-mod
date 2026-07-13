package com.pesopes.ascendfruit.mixin;

import com.pesopes.ascendfruit.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public class PotionBrewingRuntimeMixin {

    // basically make a recipe using mixins
    // very hacky but in newer versions should hopefully be replaced by a data driven approach

    // Force the UI to accept Chorus Fruit in the bottom slots
    @Inject(method = "isContainer", at = @At("HEAD"), cancellable = true)
    private void forceChorusFruitAsContainer(ItemStack input, CallbackInfoReturnable<Boolean> cir) {
        if (input.is(Items.CHORUS_FRUIT)) {
            cir.setReturnValue(true);
        }
    }
    // Force the UI to accept Wind charge in the top slots
    @Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
    private void forceWindChargeAsIngredient(ItemStack input, CallbackInfoReturnable<Boolean> cir) {
        if (input.is(Items.WIND_CHARGE)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasContainerMix", at = @At("HEAD"), cancellable = true)
    private void forceHasContainerMix(ItemStack source, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
        if (source.is(Items.CHORUS_FRUIT) && ingredient.is(Items.WIND_CHARGE)) {
            cir.setReturnValue(true);
        }
    }

    // 2. Intercept the mix loop BEFORE Mojang searches for POTION_CONTENTS components
    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private void customItemToItemMix(ItemStack ingredient, ItemStack source, CallbackInfoReturnable<ItemStack> cir) {
        if (source.is(Items.CHORUS_FRUIT) && ingredient.is(Items.WIND_CHARGE)) {
            // Return exactly 1 Ascend Fruit stack, copying over any matching stack count requirements
            ItemStack result = new ItemStack(ModItems.ASCEND_FRUIT, 1);
            cir.setReturnValue(result);
        }
    }
}