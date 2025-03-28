package dev.rurino.hasugoods.item.neso;

import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.InventoryUtils;
import dev.rurino.hasugoods.util.ItemStackUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RurinoNesoItem extends NesoItem {

  private final NesoConfig.Rurino config;

  public RurinoNesoItem(Settings settings, NesoSize size) {
    super(settings, CharaUtils.RURINO_KEY, size);
    config = (NesoConfig.Rurino) super.config;
  }

  @Override
  public long getEnergyMaxInput(ItemStack stack) {
    return config.maxEnergy();
  }

  @Override
  public long getEnergyMaxOutput(ItemStack stack) {
    return config.maxEnergy();
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
        .filter(s -> s.getItem() instanceof NesoItem nesoItem && !nesoItem.getCharaKey().equals(getCharaKey()))
        .forEach(s -> ItemStackUtils.transferEnergy(stack, s, config.energyTransferPerTick()));
  }

  @Override
  public ActionResult use(World world, PlayerEntity user, Hand hand) {
    if (world.isClient || hand != Hand.MAIN_HAND)
      return ActionResult.PASS;
    ItemStack stack = user.getStackInHand(hand);
    boolean success = false;
    for (ItemStack s : user.getHandItems()) {
      if (s == stack || !(s.getItem() instanceof NesoItem))
        continue;
      long amountTransfered = ItemStackUtils.transferEnergy(stack, s, config.energyPerAction());
      if (amountTransfered <= 0)
        continue;
      success = true;
      if (s == user.getMainHandStack()) {
        user.swingHand(Hand.MAIN_HAND);
      } else if (s == user.getOffHandStack()) {
        user.swingHand(Hand.OFF_HAND);
      }
    }
    if (success) {
      user.swingHand(hand);
      return ActionResult.SUCCESS;
    }
    return ActionResult.PASS;
  }

  @Override
  public NesoConfig.Rurino getConfig() {
    return config;
  }

}
