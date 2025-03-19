package dev.rurino.hasugoods.item.neso;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class KahoNesoItem extends NesoItem {

  // #region Config
  public static class Config extends NesoItem.Config {
    private final long energyPerAction;
    private final long energyPerReplace;
    private final int intervalTicks;
    private final int radius;
    private final float flowerRatio;
    private final float useCooldown;

    public Config(NesoSize size) {
      super(size);
      var kaho = Hasugoods.CONFIG.neso.kaho;
      switch (size) {
        case SMALL:
          this.energyPerAction = kaho.small.energyPerAction();
          this.energyPerReplace = kaho.small.energyPerReplace();
          this.intervalTicks = kaho.small.intervalTicks();
          this.radius = kaho.small.radius();
          this.flowerRatio = kaho.small.flowerRatio();
          this.useCooldown = kaho.small.useCooldown();
          break;
        case MEDIUM:
          this.energyPerAction = kaho.medium.energyPerAction();
          this.energyPerReplace = kaho.medium.energyPerReplace();
          this.intervalTicks = kaho.medium.intervalTicks();
          this.radius = kaho.medium.radius();
          this.flowerRatio = kaho.medium.flowerRatio();
          this.useCooldown = kaho.medium.useCooldown();
          break;
        case LARGE:
          this.energyPerAction = kaho.large.energyPerAction();
          this.energyPerReplace = kaho.large.energyPerReplace();
          this.intervalTicks = kaho.large.intervalTicks();
          this.radius = kaho.large.radius();
          this.flowerRatio = kaho.large.flowerRatio();
          this.useCooldown = kaho.large.useCooldown();
          break;
        default:
          throw new IllegalArgumentException("Invalid neso size when initializing config: " + size);
      }
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
    ItemStack stack = user.getStackInHand(hand);
    if (!tryUseEnergy(stack, config.energyPerAction())) {
      return ActionResult.PASS;
    }
    return ActionResult.SUCCESS;
  }

}
