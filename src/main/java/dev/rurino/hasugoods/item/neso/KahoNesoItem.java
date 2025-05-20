package dev.rurino.hasugoods.item.neso;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.item.neso.KahoNesoComponent.TickContext;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.MathUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class KahoNesoItem extends NesoItem {

  private final NesoConfig.Kaho config;

  public KahoNesoItem(Settings settings, NesoSize size) {
    super(settings, CharaUtils.KAHO_KEY, size);
    config = (NesoConfig.Kaho) super.config;
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    ItemStack stack = user.getStackInHand(hand);
    if (world.isClient) {
      return TypedActionResult.pass(stack);
    }
    if (!user.getActiveItem().isEmpty() || hand != Hand.MAIN_HAND) {
      return TypedActionResult.pass(stack);
    }
    // Get the block under the player
    TickContext context = new TickContext(this, config, stack, (ServerWorld) world, 0, user.getRandom());
    var component = KahoNesoComponent.fromPlayerPos(context, user.getBlockPos());
    if (component.isEmpty()) {
      return TypedActionResult.fail(stack);
    }
    stack.set(NesoItem.KAHO_NESO_COMPONENT, component);
    user.setCurrentHand(hand);
    return TypedActionResult.success(stack);
  }

  @Override
  public UseAction getUseAction(ItemStack stack) {
    return UseAction.BOW;
  }

  @Override
  public int getMaxUseTime(ItemStack stack, LivingEntity user) {
    return MathUtils.VERY_LARGE;
  }

  @Override
  public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
    super.usageTick(world, user, stack, remainingUseTicks);

    if (world.isClient)
      return;

    int usedTicks = getMaxUseTime(stack, user) - remainingUseTicks;
    if (usedTicks % config.intervalTicks() != 0)
      return;

    KahoNesoComponent component = stack.get(NesoItem.KAHO_NESO_COMPONENT);
    if (component == null) {
      Hasugoods.LOGGER.error("KahoNesoComponent is null for stack: {}", stack);
      return;
    }
    TickContext context = new TickContext(this, config, stack, (ServerWorld) world, usedTicks, user.getRandom());
    if (component.tick(context)) {
      // Finished
      user.stopUsingItem();
    }
  }

  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (world.isClient) {
      return;
    }
    stack.set(NesoItem.KAHO_NESO_COMPONENT, KahoNesoComponent.EMPTY);
  }

  @Override
  public NesoConfig.Kaho getConfig() {
    return config;
  }

}
