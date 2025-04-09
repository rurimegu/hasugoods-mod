package dev.rurino.hasugoods.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

public class SellConfig {
  public final IntValue exp;
  public final DoubleValue prob;
  public final DoubleValue secretProb;
  public final IntValue maxUses;
  public final IntValue secretMaxUses;
  public final IntValue regularPrice;
  public final IntValue secretPrice;
  public final IntValue unopenedPacketPrice;
  public final IntValue unopenedPacketMaxUses;
  public final DoubleValue unopenedBoxDiscount;
  public final IntValue unopenedBoxMaxUses;

  SellConfig(ModConfigSpec.Builder builder) {
    builder.push("sell");

    builder.comment("Experience rewarded to the villager for selling badges");
    exp = builder.defineInRange("exp", 3, 0, Integer.MAX_VALUE);

    builder.comment("Probability of offering to sell a badge");
    prob = builder.defineInRange("prob", 0.5, 0.0, 1.0);

    builder.comment("Probability of offering to sell a secret badge");
    secretProb = builder.defineInRange("secret_prob", 0.1, 0.0, 1.0);

    builder.comment("Maximum uses for selling regular badges");
    maxUses = builder.defineInRange("max_uses", 12, 1, Integer.MAX_VALUE);

    builder.comment("Maximum uses for selling secret badges");
    secretMaxUses = builder.defineInRange("secret_max_uses", 3, 1, Integer.MAX_VALUE);

    builder.comment("Price in emeralds for selling regular badges");
    regularPrice = builder.defineInRange("regular_price", 4, 0, Integer.MAX_VALUE);

    builder.comment("Price in emeralds for selling secret badges");
    secretPrice = builder.defineInRange("secret_price", 30, 0, Integer.MAX_VALUE);

    builder.comment("Price in emeralds for unopened badge packets");
    unopenedPacketPrice = builder.defineInRange("unopened_packet_price", 3, 0, Integer.MAX_VALUE);

    builder.comment("Maximum uses for unopened badge packet trades");
    unopenedPacketMaxUses = builder.defineInRange("unopened_packet_max_uses", 9, 1, Integer.MAX_VALUE);

    builder.comment("Discount factor for unopened badge boxes");
    unopenedBoxDiscount = builder.defineInRange("unopened_box_discount", 0.1, 0.0, 1.0);

    builder.comment("Maximum uses for unopened badge box trades");
    unopenedBoxMaxUses = builder.defineInRange("unopened_box_max_uses", 2, 1, Integer.MAX_VALUE);

    builder.pop();
  }
}
