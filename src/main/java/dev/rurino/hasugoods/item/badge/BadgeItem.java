package dev.rurino.hasugoods.item.badge;

import java.util.List;

import dev.rurino.hasugoods.ModConstants;
import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.effect.ToutoshiEffectsConsumeEffect;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.item.OshiItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.item.Item;
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class BadgeItem extends OshiItem {

  public static final DeathProtectionComponent HASU_BADGE_DEATH_PROTECTION = new DeathProtectionComponent(
      List.<ConsumeEffect>of(new ClearAllEffectsConsumeEffect(), new ToutoshiEffectsConsumeEffect()));

  public static final RegistryKey<Item> RURINO_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Identifier.of(Hasugoods.MOD_ID, "rurino_badge"));
  public static final Item RURINO_BADGE = ModItems.register(
      new BadgeItem(new Item.Settings().maxCount(16).rarity(Rarity.COMMON)
          .component(DataComponentTypes.DEATH_PROTECTION, HASU_BADGE_DEATH_PROTECTION)
          .registryKey(RURINO_BADGE_KEY), "rurino"),
      RURINO_BADGE_KEY);

  public BadgeItem(Settings settings, String oshiName) {
    super(settings, oshiName);
  }

  public static void initialize() {
    // Add the badge to the badge item group
    ItemGroupEvents.modifyEntriesEvent(ModItems.BADGE_ITEM_GROUP_KEY).register(itemGroup -> {
      itemGroup.add(RURINO_BADGE);
    });

    // Modify loot tables to include the badges
    LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
      if (source.isBuiltin() && ModConstants.BADGE_LOOT_TABLES.contains(key)) {
        LootPool.Builder poolBuilder = LootPool.builder().with(ItemEntry.builder(RURINO_BADGE).weight(10).quality(1));
        tableBuilder.pool(poolBuilder);
      }
    });
  }
}
