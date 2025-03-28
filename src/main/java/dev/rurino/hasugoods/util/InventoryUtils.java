package dev.rurino.hasugoods.util;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
  public static boolean isInHotbar(int slot) {
    return slot >= 0 && slot < 9;
  }

  public static Stream<ItemStack> getHotbarStacks(PlayerEntity entity) {
    return IntStream.range(0, PlayerInventory.getHotbarSize())
        .mapToObj(entity.getInventory()::getStack)
        .filter(stack -> !stack.isEmpty());
  }
}
