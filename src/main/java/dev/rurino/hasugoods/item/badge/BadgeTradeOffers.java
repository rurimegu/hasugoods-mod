package dev.rurino.hasugoods.item.badge;

import java.util.Optional;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.component.IOshiComponent;
import dev.rurino.hasugoods.component.ModComponents;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.config.HcVal;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;

public class BadgeTradeOffers {
  // #region Config
  private static final HcVal.Int TRADE_EXP = Hasugoods.CONFIG.getInt("trade.exp", 1);
  private static final HcVal.Float TRADE_PROB = Hasugoods.CONFIG.getFloat("trade.prob", 0.5f);
  private static final HcVal.Float TRADE_SECRET_PROB = Hasugoods.CONFIG.getFloat("trade.secretProb", 0.1f);
  private static final HcVal.Int TRADE_MAX_USES = Hasugoods.CONFIG.getInt("trade.maxUses", 12);
  private static final HcVal.Int TRADE_SECRET_MAX_USES = Hasugoods.CONFIG.getInt("trade.secretMaxUses", 3);

  private static final HcVal.Int BUY_EXP = Hasugoods.CONFIG.getInt("buy.exp", 3);
  private static final HcVal.Float BUY_PROB = Hasugoods.CONFIG.getFloat("buy.prob", 0.5f);
  private static final HcVal.Float BUY_SECRET_PROB = Hasugoods.CONFIG.getFloat("buy.secretProb", 0.1f);
  private static final HcVal.Int BUY_MAX_USES = Hasugoods.CONFIG.getInt("buy.maxUses", 12);
  private static final HcVal.Int BUY_SECRET_MAX_USES = Hasugoods.CONFIG.getInt("buy.secretMaxUses",
      3);
  private static final HcVal.Int BUY_REGULAR_PRICE = Hasugoods.CONFIG.getInt("buy.regularPrice", 1);
  private static final HcVal.Int BUY_SECRET_PRICE = Hasugoods.CONFIG.getInt("buy.secretPrice", 5);

  private static final HcVal.Int SELL_EXP = Hasugoods.CONFIG.getInt("sell.exp", 3);
  private static final HcVal.Float SELL_PROB = Hasugoods.CONFIG.getFloat("sell.prob", 0.5f);
  private static final HcVal.Float SELL_SECRET_PROB = Hasugoods.CONFIG.getFloat("sell.secretProb", 0.1f);
  private static final HcVal.Int SELL_MAX_USES = Hasugoods.CONFIG.getInt("sell.maxUses", 12);
  private static final HcVal.Int SELL_SECRET_MAX_USES = Hasugoods.CONFIG.getInt("sell.secretMaxUses",
      3);
  private static final HcVal.Int SELL_REGULAR_PRICE = Hasugoods.CONFIG.getInt("sell.regularPrice", 4);
  private static final HcVal.Int SELL_SECRET_PRICE = Hasugoods.CONFIG.getInt("sell.secretPrice", 30);

  private static final HcVal.Int UNOPENED_PACKET_PRICE = Hasugoods.CONFIG.getInt("sell.unopenedPacketPrice", 3);
  private static final HcVal.Int UNOPENED_PACKET_MAX_USES = Hasugoods.CONFIG.getInt("sell.unopenedPacketMaxUses", 9);
  private static final HcVal.Float UNOPENED_BOX_DISCOUNT = Hasugoods.CONFIG.getFloat("sell.unopenedBoxDiscount", 0.1f);
  private static final HcVal.Int UNOPENED_BOX_MAX_USES = Hasugoods.CONFIG.getInt("sell.unopenedBoxMaxUses", 2);
  // #endregion Config

  public static class Trade implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
      Optional<IOshiComponent> oshiComponentOptional = ModComponents.OSHI.maybeGet(entity);
      if (oshiComponentOptional.isEmpty()) {
        Hasugoods.LOGGER.warn("Cannot trade: oshiComponent is not found");
        return null;
      }
      if (random.nextFloat() >= TRADE_PROB.val())
        return null;
      String oshiKey = oshiComponentOptional.get().getOshiKey();
      boolean isSecret = random.nextFloat() < TRADE_SECRET_PROB.val();
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
          isSecret ? TRADE_SECRET_MAX_USES.val() : TRADE_MAX_USES.val());
      return new TradeOffer(
          new TradedItem(toBuy.get(), 1),
          new ItemStack(toSell, 1),
          maxUses, TRADE_EXP.val(), 1f);
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
      if (random.nextFloat() >= BUY_PROB.val())
        return null;
      boolean isSecret = random.nextFloat() < BUY_SECRET_PROB.val();
      String oshiKey = oshiComponentOptional.get().getOshiKey();
      Optional<BadgeItem> toBuy = BadgeItem.getBadgeItem(oshiKey, isSecret);
      if (!toBuy.isPresent()) {
        Hasugoods.LOGGER.warn("Cannot buy: {} oshiKey is not found", oshiKey);
        return null;
      }
      int maxUses = random.nextBetween(1,
          isSecret ? BUY_SECRET_MAX_USES.val() : BUY_MAX_USES.val());
      int price = isSecret ? BUY_SECRET_PRICE.val() : BUY_REGULAR_PRICE.val();
      return new TradeOffer(
          new TradedItem(toBuy.get(), 1),
          new ItemStack(Items.EMERALD, price),
          maxUses, BUY_EXP.val(), 1f);
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
      if (random.nextFloat() >= SELL_PROB.val())
        return null;
      boolean isSecret = random.nextFloat() < SELL_SECRET_PROB.val();
      String oshiKey = oshiComponentOptional.get().getOshiKey();
      Item toSell = CharaUtils.getRandomBadge(random, isSecret, oshiKey);
      if (toSell == null) {
        Hasugoods.LOGGER.warn("Cannot sell: no candidate after excluding {}", oshiKey);
        return null;
      }
      int maxUses = random.nextBetween(1,
          isSecret ? SELL_SECRET_MAX_USES.val() : SELL_MAX_USES.val());
      int price = isSecret ? SELL_SECRET_PRICE.val() : SELL_REGULAR_PRICE.val();
      return new TradeOffer(
          new TradedItem(Items.EMERALD, price),
          new ItemStack(toSell, 1),
          maxUses, SELL_EXP.val(), 1f);
    }
  }

  public static class SellPacket implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
      Item toSell = BadgeItem.UNOPENED_BADGE;
      return new TradeOffer(
          new TradedItem(Items.EMERALD, UNOPENED_PACKET_PRICE.val()),
          new ItemStack(toSell, 1),
          UNOPENED_PACKET_MAX_USES.val(),
          1, 1f);
    }
  }

  public static class SellBox implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
      Item toSell = BadgeItem.BOX_OF_BADGE;
      int price = (int) Math.ceil(
          UNOPENED_PACKET_PRICE.val() *
              BoxOfBadgeItem.NUM_BADGE_IN_BOX.val() *
              (1 - UNOPENED_BOX_DISCOUNT.val()));
      return new TradeOffer(
          new TradedItem(Items.EMERALD, price),
          new ItemStack(toSell, 1),
          UNOPENED_BOX_MAX_USES.val(),
          1, 1f);
    }
  }

  static void initialize() {
  }
}
