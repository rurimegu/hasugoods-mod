package dev.rurino.hasugoods.mixin.client;

import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.CollectionUtils;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import team.reborn.energy.api.EnergyStorage;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
  @Redirect(method = "updateHeldItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;areEqual(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z"), require = 2)
  private boolean nesoHandComparison(ItemStack from, ItemStack to) {
    if (from.isEmpty() || to.isEmpty() || !(from.getItem() instanceof NesoItem)) {
      return ItemStack.areEqual(from, to);
    }
    if (!ItemStack.areItemsEqual(from, to) || from.getCount() != to.getCount()) {
      return false;
    }
    // Ignore the energy component which changes every tick.
    var filteredFrom = from.getComponents().stream().filter(c -> !EnergyStorage.ENERGY_COMPONENT.equals(c.type()));
    var filteredTo = to.getComponents().stream().filter(c -> !EnergyStorage.ENERGY_COMPONENT.equals(c.type()));
    return CollectionUtils.equalsIgnoreOrder(filteredFrom, filteredTo);
  }
}
