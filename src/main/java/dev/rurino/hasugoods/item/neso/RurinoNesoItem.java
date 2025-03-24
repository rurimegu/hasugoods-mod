package dev.rurino.hasugoods.item.neso;

import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import dev.rurino.hasugoods.util.config.HcObj;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class RurinoNesoItem extends NesoItem {
  // #region Config
  public static final HcObj HC_RURINO = HC_NESO.child("rurino");

  private static final HcObj HC_SMALL = HC_RURINO.child("small");
  private static final HcObj HC_MEDIUM = HC_RURINO.child("medium");
  private static final HcObj HC_LARGE = HC_RURINO.child("large");

  public static class Config extends NesoItem.Config {
    private final long maxEnergy;
    private final long energyTransferPerTick;
    private final long energyPerAction;
    private final int maxBoxSize;
    private final long energyChargeInBoxPerTick;
    private final float energyBoostByMeguPerTick;
    private final float useCooldown;

    public Config(NesoSize size) {
      super(size);
      switch (size) {
        case SMALL:
          maxEnergy = HC_SMALL.getLong("maxEnergy", 1_000_000).val();
          energyTransferPerTick = HC_SMALL.getLong("energyTransferPerTick", 16).val();
          energyPerAction = HC_SMALL.getLong("energyPerAction", 16_000).val();
          maxBoxSize = HC_SMALL.getInt("maxBoxSize", 3 * 3 * 3).val();
          energyChargeInBoxPerTick = HC_SMALL.getLong("energyChargeInBoxPerTick", 128).val();
          energyBoostByMeguPerTick = HC_SMALL.getFloat("energyBoostByMeguPerTick", 4f).val();
          useCooldown = HC_SMALL.getFloat("useCooldown", 1f).val();
          break;
        case MEDIUM:
          maxEnergy = HC_MEDIUM.getLong("maxEnergy", 8_000_000).val();
          energyTransferPerTick = HC_MEDIUM.getLong("energyTransferPerTick", 64).val();
          energyPerAction = HC_MEDIUM.getLong("energyPerAction", 128_000).val();
          maxBoxSize = HC_MEDIUM.getInt("maxBoxSize", 4 * 4 * 4).val();
          energyChargeInBoxPerTick = HC_MEDIUM.getLong("energyChargeInBoxPerTick", 512).val();
          energyBoostByMeguPerTick = HC_MEDIUM.getFloat("energyBoostByMeguPerTick", 4f).val();
          useCooldown = HC_MEDIUM.getFloat("useCooldown", 1f).val();
          break;
        case LARGE:
          maxEnergy = HC_LARGE.getLong("maxEnergy", 128_000_000).val();
          energyTransferPerTick = HC_LARGE.getLong("energyTransferPerTick", 512).val();
          energyPerAction = HC_LARGE.getLong("energyPerAction", 1_000_000).val();
          maxBoxSize = HC_LARGE.getInt("maxBoxSize", 5 * 5 * 5).val();
          energyChargeInBoxPerTick = HC_LARGE.getLong("energyChargeInBoxPerTick", 2048).val();
          energyBoostByMeguPerTick = HC_LARGE.getFloat("energyBoostByMeguPerTick", 8f).val();
          useCooldown = HC_LARGE.getFloat("useCooldown", 1f).val();
          break;
        default:
          throw new IllegalArgumentException("Unknown NesoSize: " + size);
      }
      ;
    }

    @Override
    public long maxEnergy() {
      return maxEnergy;
    }

    public long energyTransferPerTick() {
      return energyTransferPerTick;
    }

    @Override
    public long energyPerAction() {
      return energyPerAction;
    }

    public int maxBoxSize() {
      return maxBoxSize;
    }

    public long energyChargeInBoxPerTick() {
      return energyChargeInBoxPerTick;
    }

    public float energyBoostByMeguPerTick() {
      return energyBoostByMeguPerTick;
    }

    @Override
    public float useCooldown() {
      return useCooldown;
    }

    public long chargeAmount(long prev, long amount) {
      return Math.min(amount, maxEnergy - prev);
    }
  }
  // #endregion

  private final Config config;

  public RurinoNesoItem(Settings settings, NesoSize size) {
    super(settings, CharaUtils.RURINO_KEY, size);
    config = (Config) super.config;
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
