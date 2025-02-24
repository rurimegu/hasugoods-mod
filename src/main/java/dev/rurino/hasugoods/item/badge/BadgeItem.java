package dev.rurino.hasugoods.item.badge;

import java.util.List;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.effect.ToutoshiEffectsConsumeEffect;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.item.OshiItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Rarity;

public class BadgeItem extends OshiItem {
  // #region Static fields
  public static final DeathProtectionComponent HASU_BADGE_DEATH_PROTECTION = new DeathProtectionComponent(
      List.<ConsumeEffect>of(new ClearAllEffectsConsumeEffect(), new ToutoshiEffectsConsumeEffect()));
  public static final TagKey<Item> REGULAR_BADGE_TAG = TagKey.of(RegistryKeys.ITEM, Hasugoods.id("regular_badges"));
  public static final TagKey<Item> SECRET_BADGE_TAG = TagKey.of(RegistryKeys.ITEM, Hasugoods.id("secret_badges"));

  public static final RegistryKey<Item> RURINO_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.RURINO_KEY + "_badge"));
  public static final Item RURINO_BADGE = registerBadge(RURINO_BADGE_KEY, OshiItem.RURINO_KEY);
  public static final RegistryKey<Item> RURINO_SECRET_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.RURINO_KEY + "_secret_badge"));
  public static final Item RURINO_SECRET_BADGE = registerSecretBadge(RURINO_SECRET_BADGE_KEY, OshiItem.RURINO_KEY);

  public static final RegistryKey<Item> MEGUMI_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.MEGUMI_KEY + "_badge"));
  public static final Item MEGUMI_BADGE = registerBadge(MEGUMI_BADGE_KEY, OshiItem.MEGUMI_KEY);
  public static final RegistryKey<Item> MEGUMI_SECRET_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.MEGUMI_KEY + "_secret_badge"));
  public static final Item MEGUMI_SECRET_BADGE = registerSecretBadge(MEGUMI_SECRET_BADGE_KEY, OshiItem.MEGUMI_KEY);

  public static final RegistryKey<Item> UNOPENED_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id("unopened_badge"));
  public static final Item UNOPENED_BADGE = ModItems.register(
      new UnopenedBadge(new Item.Settings().maxCount(16).rarity(Rarity.COMMON).registryKey(UNOPENED_BADGE_KEY)),
      UNOPENED_BADGE_KEY);

  private static Item registerBadge(RegistryKey<Item> key, String oshiKey, boolean isSecret) {
    Item item = ModItems.register(
        new BadgeItem(new Settings().maxCount(16).rarity(isSecret ? Rarity.UNCOMMON : Rarity.COMMON)
            .component(DataComponentTypes.DEATH_PROTECTION, HASU_BADGE_DEATH_PROTECTION)
            .registryKey(key), oshiKey, isSecret),
        key);
    return item;
  }

  private static Item registerBadge(RegistryKey<Item> key, String oshiKey) {
    return registerBadge(key, oshiKey, false);
  }

  private static Item registerSecretBadge(RegistryKey<Item> key, String oshiKey) {
    return registerBadge(key, oshiKey, true);
  }

  public static void initialize() {
    // Add the badge to the badge item group
    ItemGroupEvents.modifyEntriesEvent(ModItems.BADGE_ITEM_GROUP_KEY).register(itemGroup -> {
      itemGroup.add(RURINO_BADGE);
    });

    // Modify loot tables to include the badges
    LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
      // Add loot table to all chests.
      if (source.isBuiltin() && key.getValue().getPath().startsWith("chests/")) {
        // Give 0~3 badges, or 20 badges if really lucky.
        LootPool.Builder poolBuilder = LootPool.builder()
            .with(EmptyEntry.builder().weight(Hasugoods.CONFIG.chestEmptyDropWeight()))
            .with(ItemEntry.builder(UNOPENED_BADGE).weight(Hasugoods.CONFIG.chestBadgeDropWeight())
                .apply(SetCountLootFunction
                    .builder(UniformLootNumberProvider.create(Hasugoods.CONFIG.chestBadgeDropMinCount(),
                        Hasugoods.CONFIG.chestBadgeDropMaxCount()))))
            .with(ItemEntry.builder(ModItems.BOX_OF_BADGE).weight(Hasugoods.CONFIG.chestBoxDropWeight()));
        tableBuilder.pool(poolBuilder);
      }
    });
  }
  // #endregion Static fields

  private final boolean isSecret;

  public BadgeItem(Settings settings, String oshiName, boolean isSecret) {
    super(settings, oshiName);
    this.isSecret = isSecret;
  }

  public boolean isSecret() {
    return isSecret;
  }

  @Override
  public boolean hasGlint(ItemStack stack) {
    return isSecret() ? true : super.hasGlint(stack);
  }
}
