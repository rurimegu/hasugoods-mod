package dev.rurino.hasugoods.item.neso;

import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import dev.rurino.hasugoods.util.config.HcObj;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class KahoNesoItem extends NesoItem {
  // #region Config
  private static final HcObj HC_KAHO = HC_NESO.child("kaho");

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
