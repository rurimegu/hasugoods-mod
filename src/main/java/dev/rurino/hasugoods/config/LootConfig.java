package dev.rurino.hasugoods.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class LootConfig {
  public final IntValue chestBadgeDropMinCount;
  public final IntValue chestBadgeDropMaxCount;
  public final IntValue chestEmptyDropWeight;
  public final IntValue chestBadgeDropWeight;
  public final IntValue chestBoxDropWeight;
  public final IntValue regularBadgeDropWeight;
  public final IntValue secretBadgeDropWeight;
  public final IntValue numBadgeInBox;

  LootConfig(ModConfigSpec.Builder builder) {
    builder.push("loot");

    builder.comment("Minimum number of badges to drop in chests");
    chestBadgeDropMinCount = builder.defineInRange("chest_badge_drop_min_count", 1, 0, Integer.MAX_VALUE);

    builder.comment("Maximum number of badges to drop in chests");
    chestBadgeDropMaxCount = builder.defineInRange("chest_badge_drop_max_count", 3, 0, Integer.MAX_VALUE);

    builder.comment("Weight for empty drops in chests");
    chestEmptyDropWeight = builder.defineInRange("chest_empty_drop_weight", 20, 0, Integer.MAX_VALUE);

    builder.comment("Weight for badge drops in chests");
    chestBadgeDropWeight = builder.defineInRange("chest_badge_drop_weight", 70, 0, Integer.MAX_VALUE);

    builder.comment("Weight for badge box drops in chests");
    chestBoxDropWeight = builder.defineInRange("chest_box_drop_weight", 10, 0, Integer.MAX_VALUE);

    builder.comment("Weight for regular badges in unopened badges");
    regularBadgeDropWeight = builder.defineInRange("regular_badge_drop_weight", 95, 0, Integer.MAX_VALUE);

    builder.comment("Weight for secret badges in unopened badges");
    secretBadgeDropWeight = builder.defineInRange("secret_badge_drop_weight", 5, 0, Integer.MAX_VALUE);

    builder.comment("Number of badges in a box of badges");
    numBadgeInBox = builder.defineInRange("num_badge_in_box", 16, 0, Integer.MAX_VALUE);

    builder.pop();
  }
}
