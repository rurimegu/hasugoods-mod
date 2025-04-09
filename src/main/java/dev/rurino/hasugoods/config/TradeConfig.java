package dev.rurino.hasugoods.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

public class TradeConfig {
  public final IntValue exp;
  public final DoubleValue prob;
  public final DoubleValue secretProb;
  public final IntValue maxUses;
  public final IntValue secretMaxUses;

  TradeConfig(ModConfigSpec.Builder builder) {
    builder.push("trade");

    builder.comment("Experience rewarded to the villager for trading badges");
    exp = builder.defineInRange("exp", 1, 0, Integer.MAX_VALUE);

    builder.comment("Probability of offering to trade a badge");
    prob = builder.defineInRange("prob", 0.5, 0.0, 1.0);

    builder.comment("Probability of offering to trade a secret badge");
    secretProb = builder.defineInRange("secret_prob", 0.1, 0.0, 1.0);

    builder.comment("Maximum uses for regular badge trades");
    maxUses = builder.defineInRange("max_uses", 12, 1, Integer.MAX_VALUE);

    builder.comment("Maximum uses for secret badge trades");
    secretMaxUses = builder.defineInRange("secret_max_uses", 3, 1, Integer.MAX_VALUE);

    builder.pop();
  }
}
