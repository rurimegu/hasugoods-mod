package dev.rurino.hasugoods.item.badge;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.effect.ToutoshiEffectsConsumeEffect;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.item.OshiItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
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
import net.minecraft.village.VillagerProfession;

public class BadgeItem extends OshiItem {
  // #region Static fields
  public static final DeathProtectionComponent HASU_BADGE_DEATH_PROTECTION = new DeathProtectionComponent(
      List.<ConsumeEffect>of(new ClearAllEffectsConsumeEffect(), new ToutoshiEffectsConsumeEffect()));
  public static final TagKey<Item> REGULAR_BADGE_TAG = TagKey.of(RegistryKeys.ITEM, Hasugoods.id("regular_badges"));
  public static final TagKey<Item> SECRET_BADGE_TAG = TagKey.of(RegistryKeys.ITEM, Hasugoods.id("secret_badges"));

  public static final ImmutableSet<VillagerProfession> BADGE_TRADE_VILLAGER_PROFESSIONS = ImmutableSet
      .of(VillagerProfession.ARMORER,
          VillagerProfession.BUTCHER,
          VillagerProfession.CARTOGRAPHER,
          VillagerProfession.CLERIC,
          VillagerProfession.FARMER,
          VillagerProfession.FISHERMAN,
          VillagerProfession.FLETCHER,
          VillagerProfession.TOOLSMITH,
          VillagerProfession.LEATHERWORKER,
          VillagerProfession.LIBRARIAN,
          VillagerProfession.MASON,
          VillagerProfession.NITWIT,
          VillagerProfession.SHEPHERD,
          VillagerProfession.WEAPONSMITH);

  public static final ImmutableSet<VillagerProfession> UNOPENED_BADGE_TRADE_VILLAGER_PROFESSIONS = ImmutableSet
      .of(VillagerProfession.ARMORER,
          VillagerProfession.CLERIC,
          VillagerProfession.TOOLSMITH,
          VillagerProfession.LIBRARIAN,
          VillagerProfession.WEAPONSMITH);
  public static final ArrayList<BadgeItem> ALL_REGULAR_BADGES = new ArrayList<BadgeItem>();
  public static final ArrayList<BadgeItem> ALL_SECRET_BADGES = new ArrayList<BadgeItem>();

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

  public static final RegistryKey<Item> GINKO_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.GINKO_KEY + "_badge"));
  public static final Item GINKO_BADGE = registerBadge(GINKO_BADGE_KEY, OshiItem.GINKO_KEY);
  public static final RegistryKey<Item> GINKO_SECRET_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.GINKO_KEY + "_secret_badge"));
  public static final Item GINKO_SECRET_BADGE = registerSecretBadge(GINKO_SECRET_BADGE_KEY, OshiItem.GINKO_KEY);

  public static final RegistryKey<Item> HIME_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.HIME_KEY + "_badge"));
  public static final Item HIME_BADGE = registerBadge(HIME_BADGE_KEY, OshiItem.HIME_KEY);
  public static final RegistryKey<Item> HIME_SECRET_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.HIME_KEY + "_secret_badge"));
  public static final Item HIME_SECRET_BADGE = registerSecretBadge(HIME_SECRET_BADGE_KEY, OshiItem.HIME_KEY);

  public static final RegistryKey<Item> SAYAKA_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.SAYAKA_KEY + "_badge"));
  public static final Item SAYAKA_BADGE = registerBadge(SAYAKA_BADGE_KEY, OshiItem.SAYAKA_KEY);
  public static final RegistryKey<Item> SAYAKA_SECRET_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.SAYAKA_KEY + "_secret_badge"));
  public static final Item SAYAKA_SECRET_BADGE = registerSecretBadge(SAYAKA_SECRET_BADGE_KEY, OshiItem.SAYAKA_KEY);

  public static final RegistryKey<Item> TSUZURI_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.TSUZURI_KEY + "_badge"));
  public static final Item TSUZURI_BADGE = registerBadge(TSUZURI_BADGE_KEY, OshiItem.TSUZURI_KEY);
  public static final RegistryKey<Item> TSUZURI_SECRET_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.TSUZURI_KEY + "_secret_badge"));
  public static final Item TSUZURI_SECRET_BADGE = registerSecretBadge(TSUZURI_SECRET_BADGE_KEY, OshiItem.TSUZURI_KEY);

  public static final RegistryKey<Item> KOSUZU_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.KOSUZU_KEY + "_badge"));
  public static final Item KOSUZU_BADGE = registerBadge(KOSUZU_BADGE_KEY, OshiItem.KOSUZU_KEY);
  public static final RegistryKey<Item> KOSUZU_SECRET_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.KOSUZU_KEY + "_secret_badge"));
  public static final Item KOSUZU_SECRET_BADGE = registerSecretBadge(KOSUZU_SECRET_BADGE_KEY, OshiItem.KOSUZU_KEY);

  public static final RegistryKey<Item> KAHO_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.KAHO_KEY + "_badge"));
  public static final Item KAHO_BADGE = registerBadge(KAHO_BADGE_KEY, OshiItem.KAHO_KEY);
  public static final RegistryKey<Item> KAHO_SECRET_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.KAHO_KEY + "_secret_badge"));
  public static final Item KAHO_SECRET_BADGE = registerSecretBadge(KAHO_SECRET_BADGE_KEY, OshiItem.KAHO_KEY);

  public static final RegistryKey<Item> KOZUE_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.KOZUE_KEY + "_badge"));
  public static final Item KOZUE_BADGE = registerBadge(KOZUE_BADGE_KEY, OshiItem.KOZUE_KEY);
  public static final RegistryKey<Item> KOZUE_SECRET_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id(OshiItem.KOZUE_KEY + "_secret_badge"));
  public static final Item KOZUE_SECRET_BADGE = registerSecretBadge(KOZUE_SECRET_BADGE_KEY, OshiItem.KOZUE_KEY);

  public static final RegistryKey<Item> UNOPENED_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id("unopened_badge"));
  public static final Item UNOPENED_BADGE = ModItems.register(
      new UnopenedBadge(new Item.Settings().maxCount(16).rarity(Rarity.COMMON).registryKey(UNOPENED_BADGE_KEY)),
      UNOPENED_BADGE_KEY);

  public static final RegistryKey<Item> BOX_OF_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id("box_of_badge"));
  public static final Item BOX_OF_BADGE = ModItems.register(
      new BoxOfBadgeItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)
          .registryKey(BOX_OF_BADGE_KEY)),
      BOX_OF_BADGE_KEY);

  private static Item registerBadge(RegistryKey<Item> key, String oshiKey, boolean isSecret) {
    Item item = ModItems.register(
        new BadgeItem(new Settings().maxCount(16).rarity(isSecret ? Rarity.UNCOMMON : Rarity.COMMON)
            .component(DataComponentTypes.DEATH_PROTECTION, HASU_BADGE_DEATH_PROTECTION)
            .registryKey(key), oshiKey, isSecret),
        key);
    if (isSecret)
      ALL_SECRET_BADGES.add((BadgeItem) item);
    else
      ALL_REGULAR_BADGES.add((BadgeItem) item);
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
            .with(ItemEntry.builder(BOX_OF_BADGE).weight(Hasugoods.CONFIG.chestBoxDropWeight()));
        tableBuilder.pool(poolBuilder);
      }
    });

    // Add villager trades
    for (var profession : BADGE_TRADE_VILLAGER_PROFESSIONS) {
      TradeOfferHelper.registerVillagerOffers(profession, 1, factories -> {
        factories.add(new BadgeTradeOffers.Trade());
        factories.add(new BadgeTradeOffers.Buy());
        factories.add(new BadgeTradeOffers.Sell());
      });
    }

    for (var profession : UNOPENED_BADGE_TRADE_VILLAGER_PROFESSIONS) {
      TradeOfferHelper.registerVillagerOffers(profession, 1, factories -> {
        factories.add(new BadgeTradeOffers.SellPacket());
      });
      TradeOfferHelper.registerVillagerOffers(profession, 3, factories -> {
        factories.add(new BadgeTradeOffers.SellBox());
      });
    }

    // Add wandering trader trades
    TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
      factories.add(new BadgeTradeOffers.Sell());
      factories.add(new BadgeTradeOffers.SellPacket());
    });
    TradeOfferHelper.registerWanderingTraderOffers(3, factories -> {
      factories.add(new BadgeTradeOffers.SellBox());
    });
  }
  // #endregion Static fields

  private final boolean isSecret;

  public BadgeItem(Settings settings, String oshiKey, boolean isSecret) {
    super(settings, oshiKey);
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
