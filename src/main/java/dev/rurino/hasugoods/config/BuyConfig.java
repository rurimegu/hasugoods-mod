package dev.rurino.hasugoods.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

public class BuyConfig {
  public final IntValue exp;
  public final DoubleValue prob;
  public final DoubleValue secretProb;
  public final IntValue maxUses;
  public final IntValue secretMaxUses;
  public final IntValue regularPrice;
  public final IntValue secretPrice;

  BuyConfig(ModConfigSpec.Builder builder) {
    builder.push("buy");

    builder.comment("Experience rewarded to villager for buying badges");
    exp = builder.defineInRange("exp", 3, 0, Integer.MAX_VALUE);

    builder.comment("Probability of offering to buy a badge");
    prob = builder.defineInRange("prob", 0.5, 0.0, 1.0);

    builder.comment("Probability of offering to buy a secret badge");
    secretProb = builder.defineInRange("secret_prob", 0.1, 0.0, 1.0);

    builder.comment("Maximum uses for buying regular badges");
    maxUses = builder.defineInRange("max_uses", 12, 1, Integer.MAX_VALUE);

    builder.comment("Maximum uses for buying secret badges");
    secretMaxUses = builder.defineInRange("secret_max_uses", 3, 1, Integer.MAX_VALUE);

    builder.comment("Price in emeralds for buying regular badges");
    regularPrice = builder.defineInRange("regular_price", 1, 0, Integer.MAX_VALUE);

    builder.comment("Price in emeralds for buying secret badges");
    secretPrice = builder.defineInRange("secret_price", 5, 0, Integer.MAX_VALUE);

    builder.pop();
  }
}
