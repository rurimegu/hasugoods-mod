package dev.rurino.hasugoods.item.badge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.effect.ToutoshiEffectsConsumeEffect;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.item.OshiItem;
import dev.rurino.hasugoods.util.OshiUtils;
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

  protected static record BadgeItemEntry(RegistryKey<Item> key, BadgeItem item) {
  }

  protected static final Map<String, BadgeItemEntry> ALL_REGULAR_BADGES = new HashMap<String, BadgeItemEntry>();
  protected static final Map<String, BadgeItemEntry> ALL_SECRET_BADGES = new HashMap<String, BadgeItemEntry>();

  public static Optional<BadgeItem> getBadgeItem(String oshiKey, boolean isSecret) {
    return Optional.ofNullable((isSecret ? ALL_SECRET_BADGES : ALL_REGULAR_BADGES).get(oshiKey))
        .map(entry -> entry.item);
  }

  public static Optional<BadgeItem> getBadgeItem(String oshiKey) {
    return getBadgeItem(oshiKey, false);
  }

  public static Optional<RegistryKey<Item>> getBadgeItemKey(String oshiKey, boolean isSecret) {
    return Optional.ofNullable((isSecret ? ALL_SECRET_BADGES : ALL_REGULAR_BADGES).get(oshiKey))
        .map(entry -> entry.key);
  }

  public static Optional<RegistryKey<Item>> getBadgeItemKey(String oshiKey) {
    return getBadgeItemKey(oshiKey, false);
  }

  public static List<BadgeItem> getAllBadges(boolean isSecret) {
    return new ArrayList<BadgeItem>((isSecret ? ALL_SECRET_BADGES : ALL_REGULAR_BADGES).values().stream()
        .map(entry -> entry.item).toList());
  }

  public static final RegistryKey<Item> UNOPENED_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id("unopened_badge"));
  public static final Item UNOPENED_BADGE = ModItems.register(UNOPENED_BADGE_KEY,
      new UnopenedBadge(new Item.Settings().maxCount(16).rarity(Rarity.COMMON).registryKey(UNOPENED_BADGE_KEY)));

  public static final RegistryKey<Item> BOX_OF_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id("box_of_badge"));
  public static final Item BOX_OF_BADGE = ModItems.register(BOX_OF_BADGE_KEY,
      new BoxOfBadgeItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)
          .registryKey(BOX_OF_BADGE_KEY)));

  private static Item registerBadge(String oshiKey, boolean isSecret) {
    RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM,
        Hasugoods.id(oshiKey + (isSecret ? "_secret" : "") + "_badge"));
    Item item = ModItems.register(
        key,
        new BadgeItem(new Settings().maxCount(16).rarity(isSecret ? Rarity.UNCOMMON : Rarity.COMMON)
            .component(DataComponentTypes.DEATH_PROTECTION, HASU_BADGE_DEATH_PROTECTION)
            .registryKey(key), oshiKey, isSecret));
    if (isSecret)
      ALL_SECRET_BADGES.put(oshiKey, new BadgeItemEntry(key, (BadgeItem) item));
    else
      ALL_REGULAR_BADGES.put(oshiKey, new BadgeItemEntry(key, (BadgeItem) item));
    return item;
  }

  public static void initialize() {
    // Register badges
    List<Item> badgeItems = new ArrayList<Item>();
    for (String oshiKey : OshiUtils.ALL_OSHI_KEYS) {
      badgeItems.add(registerBadge(oshiKey, false));
      badgeItems.add(registerBadge(oshiKey, true));
    }

    // Add the badge to the badge item group
    ItemGroupEvents.modifyEntriesEvent(ModItems.BADGE_ITEM_GROUP_KEY).register(itemGroup -> {
      for (Item badgeItem : badgeItems) {
        itemGroup.add(badgeItem);
      }
      itemGroup.add(UNOPENED_BADGE);
      itemGroup.add(BOX_OF_BADGE);
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
