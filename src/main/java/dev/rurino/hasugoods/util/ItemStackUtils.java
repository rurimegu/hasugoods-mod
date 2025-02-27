package dev.rurino.hasugoods.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackUtils {
  public static void giveItemsToPlayerOrDrop(PlayerEntity player, Item item, int count) {
    int remainingCount = count;
    while (remainingCount > 0) {
      int giveCount = Math.min(remainingCount, item.getMaxCount());
      ItemStack stack = new ItemStack(item, giveCount);
      giveItemsToPlayerOrDrop(player, stack);
      remainingCount -= giveCount;
    }
  }

  public static void giveItemsToPlayerOrDrop(PlayerEntity player, Item item) {
    giveItemsToPlayerOrDrop(player, item, 1);
  }

  public static void giveItemsToPlayerOrDrop(PlayerEntity player, ItemStack stack) {
    if (!player.giveItemStack(stack)) {
      player.dropItem(stack, false);
    }
  }
}
