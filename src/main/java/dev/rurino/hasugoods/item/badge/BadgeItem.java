package dev.rurino.hasugoods.item.badge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.IWithChara;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;
import net.minecraft.village.VillagerProfession;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class BadgeItem extends Item implements IWithChara {
  // #region Static fields

  private static final IntValue CHEST_BADGE_DROP_MIN_COUNT = Hasugoods.CONFIG.loot.chestBadgeDropMinCount;
  private static final IntValue CHEST_BADGE_DROP_MAX_COUNT = Hasugoods.CONFIG.loot.chestBadgeDropMaxCount;
  private static final IntValue CHEST_EMPTY_DROP_WEIGHT = Hasugoods.CONFIG.loot.chestEmptyDropWeight;
  private static final IntValue CHEST_BADGE_DROP_WEIGHT = Hasugoods.CONFIG.loot.chestBadgeDropWeight;
  private static final IntValue CHEST_BOX_DROP_WEIGHT = Hasugoods.CONFIG.loot.chestBoxDropWeight;

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

  public static Optional<BadgeItem> getBadgeItem(String charaKey, boolean isSecret) {
    return Optional.ofNullable((isSecret ? ALL_SECRET_BADGES : ALL_REGULAR_BADGES).get(charaKey))
        .map(entry -> entry.item);
  }

  public static Optional<BadgeItem> getBadgeItem(String charaKey) {
    return getBadgeItem(charaKey, false);
  }

  public static Optional<RegistryKey<Item>> getBadgeItemKey(String charaKey, boolean isSecret) {
    return Optional.ofNullable((isSecret ? ALL_SECRET_BADGES : ALL_REGULAR_BADGES).get(charaKey))
        .map(entry -> entry.key);
  }

  public static Optional<RegistryKey<Item>> getBadgeItemKey(String charaKey) {
    return getBadgeItemKey(charaKey, false);
  }

  public static List<BadgeItem> getAllBadges(boolean isSecret) {
    return (isSecret ? ALL_SECRET_BADGES : ALL_REGULAR_BADGES).values().stream()
        .map(entry -> entry.item).toList();
  }

  public static List<BadgeItem> getAllBadges() {
    return Stream.concat(ALL_REGULAR_BADGES.values().stream(), ALL_SECRET_BADGES.values().stream())
        .map(entry -> entry.item).toList();
  }

  public static final RegistryKey<Item> UNOPENED_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id("unopened_badge"));
  public static final Item UNOPENED_BADGE = ModItems.register(UNOPENED_BADGE_KEY,
      new UnopenedBadgeItem(new Item.Settings().maxCount(16).rarity(Rarity.COMMON)));

  public static final RegistryKey<Item> BOX_OF_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id("box_of_badge"));
  public static final Item BOX_OF_BADGE = ModItems.register(BOX_OF_BADGE_KEY,
      new BoxOfBadgeItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)));

  private static Item registerBadge(String charaKey, boolean isSecret) {
    RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM,
        Hasugoods.id(charaKey + (isSecret ? "_secret" : "") + "_badge"));
    Item item = ModItems.register(
        key,
        new BadgeItem(new Settings().maxCount(16).rarity(isSecret ? Rarity.UNCOMMON : Rarity.COMMON),
            charaKey, isSecret));
    (isSecret ? ALL_SECRET_BADGES : ALL_REGULAR_BADGES).put(charaKey, new BadgeItemEntry(key, (BadgeItem) item));
    return item;
  }

  public static void initialize() {
    BadgeTradeOffers.initialize();

    // Register badges
    List<Item> badgeItems = new ArrayList<Item>();
    for (String charaKey : CharaUtils.ALL_CHARA_KEYS) {
      badgeItems.add(registerBadge(charaKey, false));
      badgeItems.add(registerBadge(charaKey, true));
    }

    // Modify loot tables to include the badges
    LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
      // Add loot table to all chests.
      if (source.isBuiltin() && key.getValue().getPath().startsWith("chests/")) {
        // Give 0~3 badges, or 20 badges if really lucky.
        LootPool.Builder poolBuilder = LootPool.builder()
            .with(EmptyEntry.builder().weight(CHEST_EMPTY_DROP_WEIGHT.get()))
            .with(ItemEntry.builder(UNOPENED_BADGE).weight(CHEST_BADGE_DROP_WEIGHT.get())
                .apply(SetCountLootFunction
                    .builder(UniformLootNumberProvider.create(CHEST_BADGE_DROP_MIN_COUNT.get(),
                        CHEST_BADGE_DROP_MAX_COUNT.get()))))
            .with(ItemEntry.builder(BOX_OF_BADGE).weight(CHEST_BOX_DROP_WEIGHT.get()));
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

  private final String charaKey;
  private final boolean isSecret;

  public BadgeItem(Settings settings, String charaKey, boolean isSecret) {
    super(settings);
    this.charaKey = charaKey;
    this.isSecret = isSecret;
  }

  public boolean isSecret() {
    return isSecret;
  }

  @Override
  public String getCharaKey() {
    return charaKey;
  }

  @Override
  public boolean hasGlint(ItemStack stack) {
    return isSecret() ? true : super.hasGlint(stack);
  }
}
