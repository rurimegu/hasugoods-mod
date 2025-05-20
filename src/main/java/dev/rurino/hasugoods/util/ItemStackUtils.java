package dev.rurino.hasugoods.util;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.neso.NesoItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;

public class ItemStackUtils {
  public static final int MAX_ITEM_BAR_STEPS = 13;

  public static void giveItemsToPlayerOrDrop(PlayerEntity player, Item item, int count) {
    int remainingCount = count;
    while (remainingCount > 0) {
      int giveCount = Math.min(remainingCount, item.getMaxCount());
      ItemStack stack = new ItemStack(item, giveCount);
      giveItemsToPlayerOrDrop(player, stack);
      remainingCount -= giveCount;
    }
  }

  public static TypedActionResult<ItemStack> replaceItemsToPlayerOrDrop(PlayerEntity player, Item item, int count) {
    int remainingCount = count;
    while (remainingCount > item.getMaxCount()) {
      int giveCount = Math.min(remainingCount, item.getMaxCount());
      ItemStack stack = new ItemStack(item, giveCount);
      giveItemsToPlayerOrDrop(player, stack);
      remainingCount -= giveCount;
    }
    return TypedActionResult.success(new ItemStack(item, remainingCount));
  }

  public static void giveItemsToPlayerOrDrop(PlayerEntity player, Item item) {
    giveItemsToPlayerOrDrop(player, item, 1);
  }

  public static void giveItemsToPlayerOrDrop(PlayerEntity player, ItemStack stack) {
    if (!player.giveItemStack(stack)) {
      player.dropItem(stack, false);
    }
  }

  public static int getItemBarStep(float progress) {
    return MathHelper.clamp(Math.round(progress * MAX_ITEM_BAR_STEPS), 0, MAX_ITEM_BAR_STEPS);
  }

  public static long transferEnergy(ItemStack source, ItemStack target, long amount) {
    NesoItem sourceNeso = (NesoItem) source.getItem();
    NesoItem targetNeso = (NesoItem) target.getItem();
    long maxEnergyTransfer = Math.min(amount, sourceNeso.getStoredEnergy(source));
    long energyTransferred = targetNeso.chargeEnergy(target, maxEnergyTransfer);
    long energyExtracted = sourceNeso.extractEnergy(source, energyTransferred);
    if (energyExtracted != energyTransferred) {
      Hasugoods.LOGGER.warn("Tranferred energy does not match: charged {}, extracted {}",
          energyTransferred, energyExtracted);
    }
    return energyTransferred;
  }

}
