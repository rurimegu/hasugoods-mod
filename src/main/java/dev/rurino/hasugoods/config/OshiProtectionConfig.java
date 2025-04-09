package dev.rurino.hasugoods.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class OshiProtectionConfig {
  public final IntValue duration;
  public final IntValue secretDuration;
  public final DoubleValue toutoshiDamage;

  OshiProtectionConfig(ModConfigSpec.Builder builder) {
    builder.push("oshi_protection");

    builder.comment("Duration of the oshi protection effect in ticks");
    duration = builder.defineInRange("duration", 2 * 20, 0, Integer.MAX_VALUE);

    builder.comment("Duration of the oshi protection effect in ticks when triggered by a secret badge");
    secretDuration = builder.defineInRange("secret_duration", 10 * 20, 0, Integer.MAX_VALUE);

    builder.comment("Damage dealt by the Toutoshi effect when it expires");
    toutoshiDamage = builder.defineInRange("toutoshi_damage", 1000.0, 0.0, Double.MAX_VALUE);

    builder.pop();
  }
}
