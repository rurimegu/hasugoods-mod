package dev.rurino.hasugoods.item.badge;

import java.util.Optional;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.component.IOshiComponent;
import dev.rurino.hasugoods.component.ModComponents;
import dev.rurino.hasugoods.util.BadgeUtils;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;

public class BadgeTradeOffers {
  public static class Trade implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
      Optional<IOshiComponent> oshiComponentOptional = ModComponents.OSHI.maybeGet(entity);
      if (oshiComponentOptional.isEmpty()) {
        Hasugoods.LOGGER.warn("Cannot trade: oshiComponent is not found");
        return null;
      }
      if (random.nextFloat() >= Hasugoods.CONFIG.trade.prob())
        return null;
      String oshiKey = oshiComponentOptional.get().getOshiKey();
      boolean isSecret = random.nextFloat() < Hasugoods.CONFIG.trade.secretProb();
      Optional<BadgeItem> toBuy = BadgeItem.getBadgeItem(oshiKey, isSecret);
      if (!toBuy.isPresent()) {
        Hasugoods.LOGGER.warn("Cannot trade: %s oshiKey is not found", oshiKey);
        return null;
      }
      Item toSell = BadgeUtils.getRandomBadge(random, isSecret, oshiKey);
      if (toSell == null) {
        Hasugoods.LOGGER.warn("Cannot trade: no candidate after excluding %s", oshiKey);
        return null;
      }
      int maxUses = random.nextBetween(1,
          isSecret ? Hasugoods.CONFIG.trade.secretMaxUses() : Hasugoods.CONFIG.trade.maxUses());
      return new TradeOffer(
          new TradedItem(toBuy.get(), 1),
          new ItemStack(toSell, 1),
          maxUses, Hasugoods.CONFIG.trade.exp(), 1f);
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
      if (random.nextFloat() >= Hasugoods.CONFIG.buy.prob())
        return null;
      boolean isSecret = random.nextFloat() < Hasugoods.CONFIG.buy.secretProb();
      String oshiKey = oshiComponentOptional.get().getOshiKey();
      Optional<BadgeItem> toBuy = BadgeItem.getBadgeItem(oshiKey, isSecret);
      if (!toBuy.isPresent()) {
        Hasugoods.LOGGER.warn("Cannot buy: %s oshiKey is not found", oshiKey);
        return null;
      }
      int maxUses = random.nextBetween(1,
          isSecret ? Hasugoods.CONFIG.buy.secretMaxUses() : Hasugoods.CONFIG.buy.maxUses());
      int price = isSecret ? Hasugoods.CONFIG.buy.secretPrice() : Hasugoods.CONFIG.buy.regularPrice();
      return new TradeOffer(
          new TradedItem(toBuy.get(), 1),
          new ItemStack(Items.EMERALD, price),
          maxUses, Hasugoods.CONFIG.buy.exp(), 1f);
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
      if (random.nextFloat() >= Hasugoods.CONFIG.sell.prob())
        return null;
      boolean isSecret = random.nextFloat() < Hasugoods.CONFIG.sell.secretProb();
      String oshiKey = oshiComponentOptional.get().getOshiKey();
      Item toSell = BadgeUtils.getRandomBadge(random, isSecret, oshiKey);
      if (toSell == null) {
        Hasugoods.LOGGER.warn("Cannot sell: no candidate after excluding %s", oshiKey);
        return null;
      }
      int maxUses = random.nextBetween(1,
          isSecret ? Hasugoods.CONFIG.sell.secretMaxUses() : Hasugoods.CONFIG.sell.maxUses());
      int price = isSecret ? Hasugoods.CONFIG.sell.secretPrice() : Hasugoods.CONFIG.sell.regularPrice();
      return new TradeOffer(
          new TradedItem(Items.EMERALD, price),
          new ItemStack(toSell, 1),
          maxUses, Hasugoods.CONFIG.sell.exp(), 1f);
    }
  }

  public static class SellPacket implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
      Item toSell = BadgeItem.UNOPENED_BADGE;
      return new TradeOffer(
          new TradedItem(Items.EMERALD, Hasugoods.CONFIG.unopenedPacketPrice()),
          new ItemStack(toSell, 1),
          Hasugoods.CONFIG.unopenedPacketMaxUses(),
          1, 1f);
    }
  }

  public static class SellBox implements TradeOffers.Factory {
    @Override
    public TradeOffer create(Entity entity, Random random) {
      Item toSell = BadgeItem.BOX_OF_BADGE;
      int price = (int) Math.ceil(
          Hasugoods.CONFIG.unopenedPacketPrice()
              * Hasugoods.CONFIG.numBadgeInBox()
              * (1 - Hasugoods.CONFIG.unopenedBoxDiscount()));
      return new TradeOffer(
          new TradedItem(Items.EMERALD, price),
          new ItemStack(toSell, 1),
          Hasugoods.CONFIG.unopenedBoxMaxUses(),
          1, 1f);
    }
  }
}
