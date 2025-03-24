package dev.rurino.hasugoods.item.neso;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.neso.KahoNesoComponent.TickContext;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.MathUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import dev.rurino.hasugoods.util.config.HcObj;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class KahoNesoItem extends NesoItem {
  // #region Config
  public static final HcObj HC_KAHO = HC_NESO.child("kaho");

  private static final HcObj HC_SMALL = HC_KAHO.child("small");
  private static final HcObj HC_MEDIUM = HC_KAHO.child("medium");
  private static final HcObj HC_LARGE = HC_KAHO.child("large");

  public static class Config extends NesoItem.Config {
    private final long energyPerAction;
    private final long energyPerReplace;
    private final int intervalTicks;
    private final int radius;
    private final float flowerRatio;
    private final float useCooldown;

    public Config(NesoSize size) {
      super(size);
      switch (size) {
        case SMALL:
          energyPerAction = HC_SMALL.getLong("energyPerAction", 500).val();
          energyPerReplace = HC_SMALL.getLong("energyPerReplace", -1).val();
          intervalTicks = HC_SMALL.getInt("intervalTicks", 10).val();
          radius = HC_SMALL.getInt("radius", 5).val();
          flowerRatio = HC_SMALL.getFloat("flowerRatio", 0.2f).val();
          useCooldown = HC_SMALL.getFloat("useCooldown", 5).val();
          break;
        case MEDIUM:
          energyPerAction = HC_MEDIUM.getLong("energyPerAction", 800).val();
          energyPerReplace = HC_MEDIUM.getLong("energyPerReplace", -1).val();
          intervalTicks = HC_MEDIUM.getInt("intervalTicks", 5).val();
          radius = HC_MEDIUM.getInt("radius", 8).val();
          flowerRatio = HC_MEDIUM.getFloat("flowerRatio", 0.3f).val();
          useCooldown = HC_MEDIUM.getFloat("useCooldown", 5).val();
          break;
        case LARGE:
          energyPerAction = HC_LARGE.getLong("energyPerAction", 1000).val();
          energyPerReplace = HC_LARGE.getLong("energyPerReplace", 5000).val();
          intervalTicks = HC_LARGE.getInt("intervalTicks", 3).val();
          radius = HC_LARGE.getInt("radius", 12).val();
          flowerRatio = HC_LARGE.getFloat("flowerRatio", 0.5f).val();
          useCooldown = HC_LARGE.getFloat("useCooldown", 10).val();
          break;
        default:
          throw new IllegalArgumentException("Unknown NesoSize: " + size);
      }
      ;
    }

    @Override
    public long energyPerAction() {
      return energyPerAction;
    }

    @Override
    public float useCooldown() {
      return useCooldown;
    }

    public long energyPerReplace() {
      return energyPerReplace;
    }

    public boolean canReplace() {
      return energyPerReplace >= 0;
    }

    public int intervalTicks() {
      return intervalTicks;
    }

    public int radius() {
      return radius;
    }

    public float flowerRatio() {
      return flowerRatio;
    }
  }
  // #endregion

  private final Config config;

  public KahoNesoItem(Settings settings, NesoSize size) {
    super(settings, CharaUtils.KAHO_KEY, size);
    config = (Config) super.config;
  }

  @Override
  public ActionResult use(World world, PlayerEntity user, Hand hand) {
    if (world.isClient) {
      return ActionResult.PASS;
    }
    if (!user.getActiveItem().isEmpty()) {
      return ActionResult.PASS;
    }
    ItemStack stack = user.getStackInHand(hand);
    // Get the block under the player
    TickContext context = new TickContext(this, config, stack, (ServerWorld) world, 0, user.getRandom());
    var component = KahoNesoComponent.fromPlayerPos(context, user.getBlockPos());
    if (component.isEmpty()) {
      return ActionResult.FAIL;
    }
    stack.set(NesoItem.KAHO_NESO_COMPONENT, component);
    user.setCurrentHand(hand);
    return ActionResult.SUCCESS;
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
  public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
    if (world.isClient) {
      return true;
    }
    stack.set(NesoItem.KAHO_NESO_COMPONENT, KahoNesoComponent.EMPTY);
    return true;
  }

}
