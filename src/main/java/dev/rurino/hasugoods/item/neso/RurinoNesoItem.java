package dev.rurino.hasugoods.item.neso;

import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.InventoryUtils;
import dev.rurino.hasugoods.util.ItemStackUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class RurinoNesoItem extends NesoItem {

  private final NesoConfig.Rurino config;

  public RurinoNesoItem(Settings settings, NesoSize size) {
    super(settings, CharaUtils.RURINO_KEY, size);
    config = (NesoConfig.Rurino) super.config;
  }

  @Override
  public long getEnergyMaxInput(ItemStack stack) {
    return config.energyTransferPerTick() * PlayerInventory.getHotbarSize();
  }

  @Override
  public long getEnergyMaxOutput(ItemStack stack) {
    return config.energyTransferPerTick() * PlayerInventory.getHotbarSize();
  }

  @Override
  public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
    if (world.isClient
        || !(entity instanceof PlayerEntity playerEntity)
        || !InventoryUtils.isInHotbar(slot)
        || getStoredEnergy(stack) <= 0) {
      return;
    }
    InventoryUtils.getHotbarStacks(playerEntity, true)
        .filter(s -> s.getItem() instanceof NesoItem nesoItem &&
            !nesoItem.getCharaKey().equals(getCharaKey()))
        .forEach(s -> ItemStackUtils.transferEnergy(stack, s,
            config.energyTransferPerTick()));
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack stack = user.getStackInHand(hand);
    if (world.isClient || hand != Hand.MAIN_HAND)
      return TypedActionResult.pass(stack);
    boolean success = false;
    for (ItemStack s : user.getHandItems()) {
      if (s == stack || !(s.getItem() instanceof NesoItem))
        continue;
      long amountTransfered = ItemStackUtils.transferEnergy(stack, s, config.energyPerAction());
      if (amountTransfered <= 0)
        continue;
      success = true;
      if (s == user.getMainHandStack()) {
        user.swingHand(Hand.MAIN_HAND, true);
      } else if (s == user.getOffHandStack()) {
        user.swingHand(Hand.OFF_HAND, true);
      }
    }
    if (success) {
      user.swingHand(hand, true);
      return TypedActionResult.success(stack);
    }
    return TypedActionResult.pass(stack);
  }

  @Override
  public NesoConfig.Rurino getConfig() {
    return config;
  }

}
