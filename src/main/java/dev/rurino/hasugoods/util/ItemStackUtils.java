package dev.rurino.hasugoods.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
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

  public static ActionResult replaceItemsToPlayerOrDrop(PlayerEntity player, Item item, int count) {
    int remainingCount = count;
    while (remainingCount > item.getMaxCount()) {
      int giveCount = Math.min(remainingCount, item.getMaxCount());
      ItemStack stack = new ItemStack(item, giveCount);
      giveItemsToPlayerOrDrop(player, stack);
      remainingCount -= giveCount;
    }
    return ActionResult.SUCCESS.withNewHandStack(new ItemStack(item, remainingCount));
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
}
