package dev.rurino.hasugoods.config;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import dev.rurino.hasugoods.util.config.HcObj;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;

public class NesoConfig {

  public static final HcObj NESO = ModConfig.ROOT.child("neso");
  public static final HcObj SMALL = NESO.child("small");
  public static final HcObj MEDIUM = NESO.child("medium");
  public static final HcObj LARGE = NESO.child("large");

  public static final HcObj KAHO = NESO.child("kaho");
  public static final HcObj KAHO_SMALL = KAHO.child("small");
  public static final HcObj KAHO_MEDIUM = KAHO.child("medium");
  public static final HcObj KAHO_LARGE = KAHO.child("large");

  public static final HcObj KOZUE = NESO.child("kozue");
  public static final HcObj KOZUE_SMALL = KOZUE.child("small");
  public static final HcObj KOZUE_MEDIUM = KOZUE.child("medium");
  public static final HcObj KOZUE_LARGE = KOZUE.child("large");

  public static final HcObj GINKO = NESO.child("ginko");
  public static final HcObj GINKO_SMALL = GINKO.child("small");
  public static final HcObj GINKO_MEDIUM = GINKO.child("medium");
  public static final HcObj GINKO_LARGE = GINKO.child("large");

  public static final HcObj KOSUZU = NESO.child("kosuzu");
  public static final HcObj KOSUZU_SMALL = KOSUZU.child("small");
  public static final HcObj KOSUZU_MEDIUM = KOSUZU.child("medium");
  public static final HcObj KOSUZU_LARGE = KOSUZU.child("large");

  public static final HcObj TSUZURI = NESO.child("tsuzuri");
  public static final HcObj TSUZURI_SMALL = TSUZURI.child("small");
  public static final HcObj TSUZURI_MEDIUM = TSUZURI.child("medium");
  public static final HcObj TSUZURI_LARGE = TSUZURI.child("large");

  public static final HcObj SAYAKA = NESO.child("sayaka");
  public static final HcObj SAYAKA_SMALL = SAYAKA.child("small");
  public static final HcObj SAYAKA_MEDIUM = SAYAKA.child("medium");
  public static final HcObj SAYAKA_LARGE = SAYAKA.child("large");

  public static final HcObj HIME = NESO.child("hime");
  public static final HcObj HIME_SMALL = HIME.child("small");
  public static final HcObj HIME_MEDIUM = HIME.child("medium");
  public static final HcObj HIME_LARGE = HIME.child("large");

  public static final HcObj MEGUMI = NESO.child("megumi");
  public static final HcObj MEGUMI_SMALL = MEGUMI.child("small");
  public static final HcObj MEGUMI_MEDIUM = MEGUMI.child("medium");
  public static final HcObj MEGUMI_LARGE = MEGUMI.child("large");

  public static final HcObj RURINO = NESO.child("rurino");
  public static final HcObj RURINO_SMALL = RURINO.child("small");
  public static final HcObj RURINO_MEDIUM = RURINO.child("medium");
  public static final HcObj RURINO_LARGE = RURINO.child("large");

  public static String nesoKey(String charaKey, NesoSize size) {
    return charaKey + "_neso_" + size.name().toLowerCase();
  }

  // #region Config impls
  public static abstract class Base {
    private final long maxEnergy;

    protected Base(NesoSize size) {
      this.maxEnergy = switch (size) {
        case SMALL -> SMALL.getLong("maxEnergy", 128 * 1000).val();
        case MEDIUM -> MEDIUM.getLong("maxEnergy", 1000 * 1000).val();
        case LARGE -> LARGE.getLong("maxEnergy", 16 * 1000 * 1000).val();
      };
    }

    public long maxEnergy() {
      return maxEnergy;
    }

    public abstract float useCooldown();
  }

  public static class Placeholder extends Base {
    public Placeholder(NesoSize size) {
      super(size);
      Hasugoods.LOGGER.warn("Config for {} not implemented, using placeholder values", nesoKey(size.name(), size));
    }

    @Override
    public float useCooldown() {
      return 1f;
    }
  }

  public static class Kaho extends Base {
    private final long energyPerAction;
    private final long energyPerReplace;
    private final int intervalTicks;
    private final int radius;
    private final float flowerRatio;
    private final float useCooldown;

    public Kaho(NesoSize size) {
      super(size);
      switch (size) {
        case SMALL:
          energyPerAction = KAHO_SMALL.getLong("energyPerAction", 500).val();
          energyPerReplace = KAHO_SMALL.getLong("energyPerReplace", -1).val();
          intervalTicks = KAHO_SMALL.getInt("intervalTicks", 10).val();
          radius = KAHO_SMALL.getInt("radius", 5).val();
          flowerRatio = KAHO_SMALL.getFloat("flowerRatio", 0.2f).val();
          useCooldown = KAHO_SMALL.getFloat("useCooldown", 5).val();
          break;
        case MEDIUM:
          energyPerAction = KAHO_MEDIUM.getLong("energyPerAction", 800).val();
          energyPerReplace = KAHO_MEDIUM.getLong("energyPerReplace", -1).val();
          intervalTicks = KAHO_MEDIUM.getInt("intervalTicks", 5).val();
          radius = KAHO_MEDIUM.getInt("radius", 8).val();
          flowerRatio = KAHO_MEDIUM.getFloat("flowerRatio", 0.3f).val();
          useCooldown = KAHO_MEDIUM.getFloat("useCooldown", 5).val();
          break;
        case LARGE:
          energyPerAction = KAHO_LARGE.getLong("energyPerAction", 1000).val();
          energyPerReplace = KAHO_LARGE.getLong("energyPerReplace", 5000).val();
          intervalTicks = KAHO_LARGE.getInt("intervalTicks", 3).val();
          radius = KAHO_LARGE.getInt("radius", 12).val();
          flowerRatio = KAHO_LARGE.getFloat("flowerRatio", 0.5f).val();
          useCooldown = KAHO_LARGE.getFloat("useCooldown", 10).val();
          break;
        default:
          throw new IllegalArgumentException("Unknown NesoSize: " + size);
      }
    }

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

  public static class Rurino extends Base {
    private final long maxEnergy;
    private final long energyTransferPerTick;
    private final long energyPerAction;
    private final int maxBoxSize;
    private final long energyChargeInBoxPerTick;
    private final float useCooldown;

    public Rurino(NesoSize size) {
      super(size);
      switch (size) {
        case SMALL:
          maxEnergy = RURINO_SMALL.getLong("maxEnergy", 1_000_000).val();
          energyTransferPerTick = RURINO_SMALL.getLong("energyTransferPerTick", 16).val();
          energyPerAction = RURINO_SMALL.getLong("energyPerAction", 16_000).val();
          maxBoxSize = RURINO_SMALL.getInt("maxBoxSize", 3 * 3 * 3).val();
          energyChargeInBoxPerTick = RURINO_SMALL.getLong("energyChargeInBoxPerTick", 32).val();
          useCooldown = RURINO_SMALL.getFloat("useCooldown", 1f).val();
          break;
        case MEDIUM:
          maxEnergy = RURINO_MEDIUM.getLong("maxEnergy", 8_000_000).val();
          energyTransferPerTick = RURINO_MEDIUM.getLong("energyTransferPerTick", 64).val();
          energyPerAction = RURINO_MEDIUM.getLong("energyPerAction", 128_000).val();
          maxBoxSize = RURINO_MEDIUM.getInt("maxBoxSize", 4 * 4 * 4).val();
          energyChargeInBoxPerTick = RURINO_MEDIUM.getLong("energyChargeInBoxPerTick", 128).val();
          useCooldown = RURINO_MEDIUM.getFloat("useCooldown", 1f).val();
          break;
        case LARGE:
          maxEnergy = RURINO_LARGE.getLong("maxEnergy", 128_000_000).val();
          energyTransferPerTick = RURINO_LARGE.getLong("energyTransferPerTick", 512).val();
          energyPerAction = RURINO_LARGE.getLong("energyPerAction", 1_000_000).val();
          maxBoxSize = RURINO_LARGE.getInt("maxBoxSize", 5 * 5 * 5).val();
          energyChargeInBoxPerTick = RURINO_LARGE.getLong("energyChargeInBoxPerTick", 512).val();
          useCooldown = RURINO_LARGE.getFloat("useCooldown", 1f).val();
          break;
        default:
          throw new IllegalArgumentException("Unknown NesoSize: " + size);
      }
    }

    @Override
    public long maxEnergy() {
      return maxEnergy;
    }

    public long energyTransferPerTick() {
      return energyTransferPerTick;
    }

    public long energyPerAction() {
      return energyPerAction;
    }

    public int maxBoxSize() {
      return maxBoxSize;
    }

    public long energyChargeInBoxPerTick() {
      return energyChargeInBoxPerTick;
    }

    @Override
    public float useCooldown() {
      return useCooldown;
    }

    public long chargeAmount(long prev, long amount) {
      return Math.min(amount, maxEnergy - prev);
    }
  }

  public static class Megumi extends Base {
    private final long energyPerAction;
    private final float useCooldown;
    private final float rurinoChargeBoost;
    private final float damageMultiplier;
    private final long damageEnergy;
    private final int oshiHenInterval;
    private final long oshiHenEnergy;
    private final float oshiHenWeight;
    private final float oshiHenRadius;

    public Megumi(NesoSize size) {
      super(size);
      switch (size) {
        case SMALL:
          energyPerAction = MEGUMI_SMALL.getLong("energyPerAction", 100).val();
          useCooldown = MEGUMI_SMALL.getFloat("useCooldown", 1f).val();
          rurinoChargeBoost = MEGUMI_SMALL.getFloat("rurinoChargeBoost", 1f).val();
          damageMultiplier = MEGUMI_SMALL.getFloat("damageMultiplier", 0.5f).val();
          damageEnergy = MEGUMI_SMALL.getLong("damageEnergy", 100).val();
          oshiHenInterval = MEGUMI_SMALL.getInt("oshiHenInterval", 20).val();
          oshiHenEnergy = MEGUMI_SMALL.getLong("oshiHenEnergy", 1000).val();
          oshiHenWeight = MEGUMI_SMALL.getFloat("oshiHenWeight", 1f).val();
          oshiHenRadius = MEGUMI_SMALL.getFloat("oshiHenRadius", 4).val();
          break;
        case MEDIUM:
          energyPerAction = MEGUMI_MEDIUM.getLong("energyPerAction", 150).val();
          useCooldown = MEGUMI_MEDIUM.getFloat("useCooldown", 1f).val();
          rurinoChargeBoost = MEGUMI_MEDIUM.getFloat("rurinoChargeBoost", 3f).val();
          damageMultiplier = MEGUMI_MEDIUM.getFloat("damageMultiplier", 0f).val();
          damageEnergy = MEGUMI_MEDIUM.getLong("damageEnergy", 150).val();
          oshiHenInterval = MEGUMI_MEDIUM.getInt("oshiHenInterval", 20).val();
          oshiHenEnergy = MEGUMI_MEDIUM.getLong("oshiHenEnergy", 2000).val();
          oshiHenWeight = MEGUMI_MEDIUM.getFloat("oshiHenWeight", 2f).val();
          oshiHenRadius = MEGUMI_MEDIUM.getFloat("oshiHenRadius", 8).val();
          break;
        case LARGE:
          energyPerAction = MEGUMI_LARGE.getLong("energyPerAction", 200).val();
          useCooldown = MEGUMI_LARGE.getFloat("useCooldown", 1f).val();
          rurinoChargeBoost = MEGUMI_LARGE.getFloat("rurinoChargeBoost", 7f).val();
          damageMultiplier = MEGUMI_LARGE.getFloat("damageMultiplier", -0.5f).val();
          damageEnergy = MEGUMI_LARGE.getLong("damageEnergy", 200).val();
          oshiHenInterval = MEGUMI_LARGE.getInt("oshiHenInterval", 10).val();
          oshiHenEnergy = MEGUMI_LARGE.getLong("oshiHenEnergy", 4000).val();
          oshiHenWeight = MEGUMI_LARGE.getFloat("oshiHenWeight", 4f).val();
          oshiHenRadius = MEGUMI_LARGE.getFloat("oshiHenRadius", 16).val();
          break;
        default:
          throw new IllegalArgumentException("Unknown NesoSize: " + size);
      }
    }

    public long energyPerAction() {
      return energyPerAction;
    }

    @Override
    public float useCooldown() {
      return useCooldown;
    }

    public float rurinoChargeBoost() {
      return rurinoChargeBoost;
    }

    public float damageMultiplier() {
      return damageMultiplier;
    }

    public long damageEnergy() {
      return damageEnergy;
    }

    public int oshiHenInterval() {
      return oshiHenInterval;
    }

    public long oshiHenEnergy() {
      return oshiHenEnergy;
    }

    public float oshiHenWeight() {
      return oshiHenWeight;
    }

    public boolean shouldOshiHen(float health, Random random) {
      return random.nextFloat() * health < oshiHenWeight();
    }

    public boolean shouldOshiHen(LivingEntity entity) {
      if (entity instanceof PlayerEntity) {
        return false;
      }
      return shouldOshiHen(entity.getHealth(), entity.getRandom());
    }

    public float oshiHenRadius() {
      return oshiHenRadius;
    }
  }
  // #endregion Config impls

  private static final Map<String, Base> CONFIGS = new HashMap<>();

  private static void registerConfig(String charaKey, NesoSize size, Base config) {
    String key = nesoKey(charaKey, size);
    if (CONFIGS.containsKey(key)) {
      Hasugoods.LOGGER.warn("Config for {} already registered, replacing", key);
    } else {
      Hasugoods.LOGGER.info("Register config: {}", key);
    }
    CONFIGS.put(key, config);
  }

  public static Base getConfig(String charaKey, NesoSize size) {
    String key = nesoKey(charaKey, size);
    if (!CONFIGS.containsKey(key)) {
      Hasugoods.LOGGER.error("Config for {} not found", key);
      return null;
    }
    return CONFIGS.get(key);
  }

  public static <T extends Base> T getConfig(Class<T> type, String charaKey, NesoSize size) {
    return type.cast(getConfig(charaKey, size));
  }

  static {
    for (NesoSize size : NesoSize.values()) {
      registerConfig(CharaUtils.RURINO_KEY, size, new Rurino(size));
      registerConfig(CharaUtils.MEGUMI_KEY, size, new Megumi(size));
      registerConfig(CharaUtils.HIME_KEY, size, new Placeholder(size));
      registerConfig(CharaUtils.KAHO_KEY, size, new Kaho(size));
      registerConfig(CharaUtils.GINKO_KEY, size, new Placeholder(size));
      registerConfig(CharaUtils.KOZUE_KEY, size, new Placeholder(size));
      registerConfig(CharaUtils.TSUZURI_KEY, size, new Placeholder(size));
      registerConfig(CharaUtils.SAYAKA_KEY, size, new Placeholder(size));
      registerConfig(CharaUtils.KOSUZU_KEY, size, new Placeholder(size));
    }
  }
}
