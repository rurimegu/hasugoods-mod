package dev.rurino.hasugoods.config;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.ModConfigSpec.LongValue;

import java.util.HashMap;
import java.util.Map;

public class NesoConfig {

  // #region Individual Configs
  public static abstract class Base {
    private final LongValue maxEnergy;
    private final DoubleValue useCooldown;

    private final String charaKey;
    private final NesoSize size;

    protected Base(ModConfigSpec.Builder builder, String charaKey, NesoSize size) {
      this.charaKey = charaKey;
      this.size = size;

      String sizeName = size.name().toLowerCase();
      builder.push(charaKey);
      builder.push(sizeName);

      builder.comment("Maximum energy capacity for " + charaKey + " " + sizeName + " neso");
      long defaultMaxEnergy = getDefaultMaxEnergy(size);
      maxEnergy = builder.defineInRange("max_energy", defaultMaxEnergy, 0, Long.MAX_VALUE);

      builder.comment("Use cooldown for " + charaKey + " " + sizeName + " neso");
      float defaultUseCooldown = getDefaultUseCooldown(size);
      useCooldown = builder.defineInRange("use_cooldown", defaultUseCooldown, 0f, Float.MAX_VALUE);

      // Each subclass will add its own configuration entries and call builder.pop()
    }

    protected long getDefaultMaxEnergy(NesoSize size) {
      return switch (size) {
        case SMALL -> 128 * 1000;
        case MEDIUM -> 1000 * 1000;
        case LARGE -> 16 * 1000 * 1000;
      };
    }

    protected float getDefaultUseCooldown(NesoSize size) {
      return 0f;
    }

    public long maxEnergy() {
      return maxEnergy.get();
    }

    public String getCharaKey() {
      return charaKey;
    }

    public NesoSize getSize() {
      return size;
    }

    public float useCooldown() {
      return useCooldown.get().floatValue();
    }
  }

  public static class Placeholder extends Base {
    private Placeholder(ModConfigSpec.Builder builder, String charaKey, NesoSize size) {
      super(builder, charaKey, size);

      builder.pop(); // sizeName
      builder.pop(); // charaKey
    }
  }

  public static class Kaho extends Base {
    private final LongValue energyPerAction;
    private final LongValue energyPerReplace;
    private final IntValue intervalTicks;
    private final IntValue radius;
    private final DoubleValue flowerRatio;
    private final IntValue numParticles; // TODO: This might better be a client-side config
    private final IntValue maxSpreadDeltaY;

    private Kaho(ModConfigSpec.Builder builder, String charaKey, NesoSize size) {
      super(builder, charaKey, size);

      builder.comment("Energy consumed per action for " + charaKey + " " + size.name().toLowerCase() + " neso");
      energyPerAction = builder.defineInRange("energy_per_action", getDefaultEnergyPerAction(size), 0, Long.MAX_VALUE);

      builder.comment("Energy consumed per block replacement (-1 means disabled)");
      energyPerReplace = builder.defineInRange("energy_per_replace", getDefaultEnergyPerReplace(size), -1,
          Long.MAX_VALUE);

      builder.comment("Interval in ticks between actions");
      intervalTicks = builder.defineInRange("interval_ticks", getDefaultIntervalTicks(size), 1, Integer.MAX_VALUE);

      builder.comment("Radius of effect");
      radius = builder.defineInRange("radius", getDefaultRadius(size), 1, Integer.MAX_VALUE);

      builder.comment("Ratio of flowers to place");
      flowerRatio = builder.defineInRange("flower_ratio", getDefaultFlowerRatio(size), 0.0, 1.0);

      builder.comment("Number of particles to spawn");
      numParticles = builder.defineInRange("num_particles", 4, 0, 128);

      builder.comment("Maximum height difference for spreading to adjacent blocks");
      maxSpreadDeltaY = builder.defineInRange("max_spread_delta_y", 3, 0, 128);

      builder.pop(); // sizeName
      builder.pop(); // charaKey
    }

    private static long getDefaultEnergyPerAction(NesoSize size) {
      return switch (size) {
        case SMALL -> 500;
        case MEDIUM -> 800;
        case LARGE -> 1000;
      };
    }

    private static long getDefaultEnergyPerReplace(NesoSize size) {
      return switch (size) {
        case SMALL -> -1;
        case MEDIUM -> -1;
        case LARGE -> 5000;
      };
    }

    private static int getDefaultIntervalTicks(NesoSize size) {
      return switch (size) {
        case SMALL -> 10;
        case MEDIUM -> 5;
        case LARGE -> 3;
      };
    }

    private static int getDefaultRadius(NesoSize size) {
      return switch (size) {
        case SMALL -> 5;
        case MEDIUM -> 8;
        case LARGE -> 12;
      };
    }

    private static float getDefaultFlowerRatio(NesoSize size) {
      return switch (size) {
        case SMALL -> 0.2f;
        case MEDIUM -> 0.3f;
        case LARGE -> 0.5f;
      };
    }

    public long energyPerAction() {
      return energyPerAction.get();
    }

    @Override
    public float getDefaultUseCooldown(NesoSize size) {
      return switch (size) {
        case SMALL -> 5f;
        case MEDIUM -> 5f;
        case LARGE -> 10f;
      };
    }

    public long energyPerReplace() {
      return energyPerReplace.get();
    }

    public boolean canReplace() {
      return energyPerReplace.get() >= 0;
    }

    public int intervalTicks() {
      return intervalTicks.get();
    }

    public int radius() {
      return radius.get();
    }

    public float flowerRatio() {
      return flowerRatio.get().floatValue();
    }

    public int numParticles() {
      return numParticles.get();
    }

    public int maxSpreadDeltaY() {
      return maxSpreadDeltaY.get();
    }
  }

  public static class Rurino extends Base {
    private final LongValue energyTransferPerTick;
    private final LongValue energyPerAction;
    private final IntValue maxBoxSize;
    private final LongValue energyChargeInBoxPerTick;

    private Rurino(ModConfigSpec.Builder builder, String charaKey, NesoSize size) {
      super(builder, charaKey, size);

      builder.comment("Energy transferred per tick");
      energyTransferPerTick = builder.defineInRange("energy_transfer_per_tick", getDefaultEnergyTransferPerTick(size),
          0, Long.MAX_VALUE);

      builder.comment("Energy consumed per action");
      energyPerAction = builder.defineInRange("energy_per_action", getDefaultEnergyPerAction(size), 0, Long.MAX_VALUE);

      builder.comment("Maximum box size in blocks");
      maxBoxSize = builder.defineInRange("max_box_size", getDefaultMaxBoxSize(size), 1, Integer.MAX_VALUE);

      builder.comment("Energy charged per tick while in box");
      energyChargeInBoxPerTick = builder.defineInRange("energy_charge_in_box_per_tick",
          getDefaultEnergyChargeInBoxPerTick(size), 0, Long.MAX_VALUE);

      builder.pop(); // sizeName
      builder.pop(); // charaKey
    }

    private static long getDefaultEnergyTransferPerTick(NesoSize size) {
      return switch (size) {
        case SMALL -> 16;
        case MEDIUM -> 64;
        case LARGE -> 512;
      };
    }

    private static long getDefaultEnergyPerAction(NesoSize size) {
      return switch (size) {
        case SMALL -> 16_000;
        case MEDIUM -> 128_000;
        case LARGE -> 1_000_000;
      };
    }

    private static int getDefaultMaxBoxSize(NesoSize size) {
      return switch (size) {
        case SMALL -> 3 * 3 * 3;
        case MEDIUM -> 4 * 4 * 4;
        case LARGE -> 5 * 5 * 5;
      };
    }

    private static long getDefaultEnergyChargeInBoxPerTick(NesoSize size) {
      return switch (size) {
        case SMALL -> 32;
        case MEDIUM -> 128;
        case LARGE -> 512;
      };
    }

    @Override
    protected long getDefaultMaxEnergy(NesoSize size) {
      return switch (size) {
        case SMALL -> 1000 * 1000;
        case MEDIUM -> 16 * 1000 * 1000;
        case LARGE -> 128 * 1000 * 1000;
      };
    }

    public long energyTransferPerTick() {
      return energyTransferPerTick.get();
    }

    public long energyPerAction() {
      return energyPerAction.get();
    }

    public int maxBoxSize() {
      return maxBoxSize.get();
    }

    public long energyChargeInBoxPerTick() {
      return energyChargeInBoxPerTick.get();
    }

    @Override
    public float getDefaultUseCooldown(NesoSize size) {
      return 1f;
    }

    public long chargeAmount(long prev, long amount) {
      return Math.min(amount, maxEnergy() - prev);
    }
  }

  public static class Megumi extends Base {
    private final LongValue energyPerAction;
    private final DoubleValue rurinoChargeBoost;
    private final DoubleValue damageMultiplier;
    private final LongValue damageEnergy;
    private final IntValue oshiHenInterval;
    private final LongValue oshiHenEnergy;
    private final DoubleValue oshiHenWeight;
    private final DoubleValue oshiHenRadius;

    private Megumi(ModConfigSpec.Builder builder, String charaKey, NesoSize size) {
      super(builder, charaKey, size);

      builder.comment("Energy consumed per action");
      energyPerAction = builder.defineInRange("energy_per_action", getDefaultEnergyPerAction(size), 0, Long.MAX_VALUE);

      builder.comment("Boost to Rurino neso charge rate");
      rurinoChargeBoost = builder.defineInRange("rurino_charge_boost", getDefaultRurinoChargeBoost(size), 0.0,
          Float.MAX_VALUE);

      builder.comment("Damage multiplier when holding this neso");
      damageMultiplier = builder.defineInRange("damage_multiplier", getDefaultDamageMultiplier(size), -1.0,
          Float.MAX_VALUE);

      builder.comment("Energy consumed per damage modification");
      damageEnergy = builder.defineInRange("damage_energy", getDefaultDamageEnergy(size), 0, Long.MAX_VALUE);

      builder.comment("Interval in ticks between oshi hen checks");
      oshiHenInterval = builder.defineInRange("oshi_hen_interval", getDefaultOshiHenInterval(size), 1,
          Integer.MAX_VALUE);

      builder.comment("Energy consumed per oshi hen conversion");
      oshiHenEnergy = builder.defineInRange("oshi_hen_energy", getDefaultOshiHenEnergy(size), 0, Long.MAX_VALUE);

      builder.comment("Weight for oshi hen effect");
      oshiHenWeight = builder.defineInRange("oshi_hen_weight", getDefaultOshiHenWeight(size), 0.0, Float.MAX_VALUE);

      builder.comment("Radius for oshi hen effect");
      oshiHenRadius = builder.defineInRange("oshi_hen_radius", getDefaultOshiHenRadius(size), 0.0, Float.MAX_VALUE);

      builder.pop(); // sizeName
      builder.pop(); // charaKey
    }

    private static long getDefaultEnergyPerAction(NesoSize size) {
      return switch (size) {
        case SMALL -> 100;
        case MEDIUM -> 150;
        case LARGE -> 200;
      };
    }

    private static float getDefaultRurinoChargeBoost(NesoSize size) {
      return switch (size) {
        case SMALL -> 1.0f;
        case MEDIUM -> 3.0f;
        case LARGE -> 7.0f;
      };
    }

    private static float getDefaultDamageMultiplier(NesoSize size) {
      return switch (size) {
        case SMALL -> 0.5f;
        case MEDIUM -> 0.0f;
        case LARGE -> -0.5f;
      };
    }

    private static long getDefaultDamageEnergy(NesoSize size) {
      return switch (size) {
        case SMALL -> 100;
        case MEDIUM -> 150;
        case LARGE -> 200;
      };
    }

    private static int getDefaultOshiHenInterval(NesoSize size) {
      return switch (size) {
        case SMALL -> 20;
        case MEDIUM -> 20;
        case LARGE -> 10;
      };
    }

    private static long getDefaultOshiHenEnergy(NesoSize size) {
      return switch (size) {
        case SMALL -> 1000;
        case MEDIUM -> 2000;
        case LARGE -> 4000;
      };
    }

    private static float getDefaultOshiHenWeight(NesoSize size) {
      return switch (size) {
        case SMALL -> 1.0f;
        case MEDIUM -> 2.0f;
        case LARGE -> 4.0f;
      };
    }

    private static float getDefaultOshiHenRadius(NesoSize size) {
      return switch (size) {
        case SMALL -> 4.0f;
        case MEDIUM -> 8.0f;
        case LARGE -> 16.0f;
      };
    }

    public long energyPerAction() {
      return energyPerAction.get();
    }

    @Override
    public float getDefaultUseCooldown(NesoSize size) {
      return 1f;
    }

    public float rurinoChargeBoost() {
      return rurinoChargeBoost.get().floatValue();
    }

    public float damageMultiplier() {
      return damageMultiplier.get().floatValue();
    }

    public long damageEnergy() {
      return damageEnergy.get();
    }

    public int oshiHenInterval() {
      return oshiHenInterval.get();
    }

    public long oshiHenEnergy() {
      return oshiHenEnergy.get();
    }

    public float oshiHenWeight() {
      return oshiHenWeight.get().floatValue();
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
      return oshiHenRadius.get().floatValue();
    }
  }

  // #endregion Individual Configs

  // #region Registry
  private final Map<String, Base> CONFIGS = new HashMap<>();
  public final LongValue pos0ChargeAmountPerTick;
  public final LongValue nesoBaseChargeAmountPerTick;

  public Base getConfig(String charaKey, NesoSize size) {
    String key = CharaUtils.nesoKey(charaKey, size);
    if (!CONFIGS.containsKey(key)) {
      Hasugoods.LOGGER.error("Config for {} not found", key);
      return null;
    }
    return CONFIGS.get(key);
  }

  public <T extends Base> T getConfig(Class<T> type, String charaKey, NesoSize size) {
    return type.cast(getConfig(charaKey, size));
  }

  // Method to register configs created from ModConfigSpec
  NesoConfig(ModConfigSpec.Builder builder) {
    builder.push("neso");

    builder.comment("Energy charge amount per tick for nesos on the position 0 block");
    pos0ChargeAmountPerTick = builder.defineInRange("pos0_charge_amount_per_tick", 128, 0, Long.MAX_VALUE);

    builder.comment("Energy charge amount per tick for nesos on the neso base block");
    nesoBaseChargeAmountPerTick = builder.defineInRange("neso_base_charge_amount_per_tick", 8, 0, Long.MAX_VALUE);

    // For each character and size, register the appropriate config
    for (NesoSize size : NesoSize.values()) {
      registerConfig(new Rurino(builder, CharaUtils.RURINO_KEY, size));
      registerConfig(new Megumi(builder, CharaUtils.MEGUMI_KEY, size));
      registerConfig(new Kaho(builder, CharaUtils.KAHO_KEY, size));

      // Use placeholder for the rest
      registerConfig(new Placeholder(builder, CharaUtils.HIME_KEY, size));
      registerConfig(new Placeholder(builder, CharaUtils.GINKO_KEY, size));
      registerConfig(new Placeholder(builder, CharaUtils.KOZUE_KEY, size));
      registerConfig(new Placeholder(builder, CharaUtils.TSUZURI_KEY, size));
      registerConfig(new Placeholder(builder, CharaUtils.SAYAKA_KEY, size));
      registerConfig(new Placeholder(builder, CharaUtils.KOSUZU_KEY, size));
    }
    builder.pop(); // neso
  }

  private void registerConfig(Base config) {
    String key = CharaUtils.nesoKey(config.getCharaKey(), config.getSize());
    if (CONFIGS.containsKey(key)) {
      Hasugoods.LOGGER.warn("Config for {} already registered, replacing", key);
    } else {
      Hasugoods.LOGGER.debug("Register config: {}", key);
    }
    CONFIGS.put(key, config);
  }
  // #endregion Registry
}
