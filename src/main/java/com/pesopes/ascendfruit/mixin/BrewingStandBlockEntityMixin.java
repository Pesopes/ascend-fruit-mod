package com.pesopes.ascendfruit.mixin;

import com.pesopes.ascendfruit.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingStandBlockEntity.class)
public class BrewingStandBlockEntityMixin {

    @Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
    private void allowHopperAutomation(int slot, ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        // Handle slots 0, 1, 2 (Potion slots) accepting Chorus Fruit from hoppers
        if (slot >= 0 && slot <= 2 && itemStack.is(Items.CHORUS_FRUIT)) {
            // Ensure the slot is empty before letting the hopper place it, matching vanilla behavior
            BrewingStandBlockEntity stand = (BrewingStandBlockEntity) (Object) this;
            if (stand.getItem(slot).isEmpty()) {
                cir.setReturnValue(true);
            }
        }
    }
}