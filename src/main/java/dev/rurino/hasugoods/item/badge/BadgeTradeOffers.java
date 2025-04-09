package dev.rurino.hasugoods.item.badge;

import java.util.Optional;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.component.IOshiComponent;
import dev.rurino.hasugoods.component.ModComponents;
import dev.rurino.hasugoods.util.CharaUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;

public class BadgeTradeOffers {
  // #region Config
  private static final IntValue TRADE_EXP = Hasugoods.CONFIG.trade.exp;
  private static final DoubleValue TRADE_PROB = Hasugoods.CONFIG.trade.prob;
  private static final DoubleValue TRADE_SECRET_PROB = Hasugoods.CONFIG.trade.secretProb;
  private static final IntValue TRADE_MAX_USES = Hasugoods.CONFIG.trade.maxUses;
  private static final IntValue TRADE_SECRET_MAX_USES = Hasugoods.CONFIG.trade.secretMaxUses;

  // Buy config values
  private static final IntValue BUY_EXP = Hasugoods.CONFIG.buy.exp;
  private static final DoubleValue BUY_PROB = Hasugoods.CONFIG.buy.prob;
  private static final DoubleValue BUY_SECRET_PROB = Hasugoods.CONFIG.buy.secretProb;
  private static final IntValue BUY_MAX_USES = Hasugoods.CONFIG.buy.maxUses;
  private static final IntValue BUY_SECRET_MAX_USES = Hasugoods.CONFIG.buy.secretMaxUses;
  private static final IntValue BUY_REGULAR_PRICE = Hasugoods.CONFIG.buy.regularPrice;
  private static final IntValue BUY_SECRET_PRICE = Hasugoods.CONFIG.buy.secretPrice;

  // Sell config values
  private static final IntValue SELL_EXP = Hasugoods.CONFIG.sell.exp;
  private static final DoubleValue SELL_PROB = Hasugoods.CONFIG.sell.prob;
  private static final DoubleValue SELL_SECRET_PROB = Hasugoods.CONFIG.sell.secretProb;
  private static final IntValue SELL_MAX_USES = Hasugoods.CONFIG.sell.maxUses;
  private static final IntValue SELL_SECRET_MAX_USES = Hasugoods.CONFIG.sell.secretMaxUses;
  private static final IntValue SELL_REGULAR_PRICE = Hasugoods.CONFIG.sell.regularPrice;
  private static final IntValue SELL_SECRET_PRICE = Hasugoods.CONFIG.sell.secretPrice;

  private static final IntValue UNOPENED_PACKET_PRICE = Hasugoods.CONFIG.sell.unopenedPacketPrice;
  private static final IntValue UNOPENED_PACKET_MAX_USES = Hasugoods.CONFIG.sell.unopenedPacketMaxUses;
  private static final DoubleValue UNOPENED_BOX_DISCOUNT = Hasugoods.CONFIG.sell.unopenedBoxDiscount;
  private static final IntValue UNOPENED_BOX_MAX_USES = Hasugoods.CONFIG.sell.unopenedBoxMaxUses;
  // #endregion Config

  public static class Trade implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
      Optional<IOshiComponent> oshiComponentOptional = ModComponents.OSHI.maybeGet(entity);
      if (oshiComponentOptional.isEmpty()) {
        Hasugoods.LOGGER.warn("Cannot trade: oshiComponent is not found");
        return null;
      }
      if (random.nextFloat() >= TRADE_PROB.get())
        return null;
      String oshiKey = oshiComponentOptional.get().getOshiKey();
      boolean isSecret = random.nextFloat() < TRADE_SECRET_PROB.get();
      Optional<BadgeItem> toBuy = BadgeItem.getBadgeItem(oshiKey, isSecret);
      if (!toBuy.isPresent()) {
        Hasugoods.LOGGER.warn("Cannot trade: {} oshiKey is not found", oshiKey);
        return null;
      }
      Item toSell = CharaUtils.getRandomBadge(random, isSecret, oshiKey);
      if (toSell == null) {
        Hasugoods.LOGGER.warn("Cannot trade: no candidate after excluding {}", oshiKey);
        return null;
      }
      int maxUses = random.nextBetween(1,
          isSecret ? TRADE_SECRET_MAX_USES.get() : TRADE_MAX_USES.get());
      return new TradeOffer(
          new TradedItem(toBuy.get(), 1),
          new ItemStack(toSell, 1),
          maxUses, TRADE_EXP.get(), 1f);
    }
  }

  public static class Buy implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
      Optional<IOshiComponent> oshiComponentOptional = ModComponents.OSHI.maybeGet(entity);
      if (oshiComponentOptional.isEmpty()) {
        Hasugoods.LOGGER.warn("Cannot trade: oshiComponent is not found");
        return null;
      }
      if (random.nextFloat() >= BUY_PROB.get())
        return null;
      boolean isSecret = random.nextFloat() < BUY_SECRET_PROB.get();
      String oshiKey = oshiComponentOptional.get().getOshiKey();
      Optional<BadgeItem> toBuy = BadgeItem.getBadgeItem(oshiKey, isSecret);
      if (!toBuy.isPresent()) {
        Hasugoods.LOGGER.warn("Cannot buy: {} oshiKey is not found", oshiKey);
        return null;
      }
      int maxUses = random.nextBetween(1,
          isSecret ? BUY_SECRET_MAX_USES.get() : BUY_MAX_USES.get());
      int price = isSecret ? BUY_SECRET_PRICE.get() : BUY_REGULAR_PRICE.get();
      return new TradeOffer(
          new TradedItem(toBuy.get(), 1),
          new ItemStack(Items.EMERALD, price),
          maxUses, BUY_EXP.get(), 1f);
    }
  }

  public static class Sell implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
      Optional<IOshiComponent> oshiComponentOptional = ModComponents.OSHI.maybeGet(entity);
      if (oshiComponentOptional.isEmpty()) {
        Hasugoods.LOGGER.warn("Cannot trade: oshiComponent is not found");
        return null;
      }
      if (random.nextFloat() >= SELL_PROB.get())
        return null;
      boolean isSecret = random.nextFloat() < SELL_SECRET_PROB.get();
      String oshiKey = oshiComponentOptional.get().getOshiKey();
      Item toSell = CharaUtils.getRandomBadge(random, isSecret, oshiKey);
      if (toSell == null) {
        Hasugoods.LOGGER.warn("Cannot sell: no candidate after excluding {}", oshiKey);
        return null;
      }
      int maxUses = random.nextBetween(1,
          isSecret ? SELL_SECRET_MAX_USES.get() : SELL_MAX_USES.get());
      int price = isSecret ? SELL_SECRET_PRICE.get() : SELL_REGULAR_PRICE.get();
      return new TradeOffer(
          new TradedItem(Items.EMERALD, price),
          new ItemStack(toSell, 1),
          maxUses, SELL_EXP.get(), 1f);
    }
  }

  public static class SellPacket implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
      Item toSell = BadgeItem.UNOPENED_BADGE;
      return new TradeOffer(
          new TradedItem(Items.EMERALD, UNOPENED_PACKET_PRICE.get()),
          new ItemStack(toSell, 1),
          UNOPENED_PACKET_MAX_USES.get(),
          1, 1f);
    }
  }

  public static class SellBox implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
      Item toSell = BadgeItem.BOX_OF_BADGE;
      int price = (int) Math.ceil(
          UNOPENED_PACKET_PRICE.get() *
              Hasugoods.CONFIG.loot.numBadgeInBox.get() *
              (1 - UNOPENED_BOX_DISCOUNT.get()));
      return new TradeOffer(
          new TradedItem(Items.EMERALD, price),
          new ItemStack(toSell, 1),
          UNOPENED_BOX_MAX_USES.get(),
          1, 1f);
    }
  }

  static void initialize() {
  }
}
