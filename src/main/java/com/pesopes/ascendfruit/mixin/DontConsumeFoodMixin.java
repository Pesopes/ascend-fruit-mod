package com.pesopes.ascendfruit.mixin;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class DontConsumeFoodMixin {

    @Inject(at = @At("HEAD"), method = "eatFood", cancellable = true)
    private void eatFood(World world, @NotNull ItemStack stack, FoodComponent foodComponent, CallbackInfoReturnable<ItemStack> cir) {
//        LOGGER.info(stack.getItem().toString());
//        Item item = stack.getItem();
//        if (item instanceof AscendFruitItem ascendFruitItem) {
//            if (ascendFruitItem.)
//            LOGGER.info("CANCELLED!!!!");
//            cir.setReturnValue(stack);
//        }
    }
}
