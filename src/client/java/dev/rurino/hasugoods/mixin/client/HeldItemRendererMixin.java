package dev.rurino.hasugoods.mixin.client;

import com.google.common.collect.Iterators;
import dev.rurino.hasugoods.item.neso.NesoItem;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.reborn.energy.api.EnergyStorage;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
  // Stop play item swap animation if only energy is modified
  @Inject(method = "shouldSkipHandAnimationOnSwap(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
  private void shouldSkipHandAnimationOnSwap(ItemStack from, ItemStack to, CallbackInfoReturnable<Boolean> cir) {
    if (from.isEmpty() || to.isEmpty() || !(from.getItem() instanceof NesoItem)) {
      return;
    }
    if (!ItemStack.areItemsEqual(from, to) || from.getCount() != to.getCount()) {
      cir.setReturnValue(false);
      return;
    }
    // Ignore the energy component which changes every tick.
    if (Iterators.elementsEqual(
        from.getComponents().stream().filter(c -> c.type() != EnergyStorage.ENERGY_COMPONENT).iterator(),
        to.getComponents().stream().filter(c -> c.type() != EnergyStorage.ENERGY_COMPONENT).iterator()
    )) {
      cir.setReturnValue(true);
    }
  }
}
