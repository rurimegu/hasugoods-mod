package dev.rurino.hasugoods.item.neso;

import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
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
  public ActionResult use(World world, PlayerEntity user, Hand hand) {
    // For testing only
    if (world.isClient) {
      return super.use(world, user, hand);
    }
    ItemStack stack = user.getStackInHand(hand);
    setStoredEnergy(stack, 0);
    return ActionResult.SUCCESS;
  }

}
