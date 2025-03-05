package dev.rurino.hasugoods.datagen;

import java.util.concurrent.CompletableFuture;

import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class HasugoodsTagProvider {
  public static class ItemTags extends FabricTagProvider.ItemTagProvider {

    public ItemTags(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    protected void configure(WrapperLookup wrapperLookup) {
      getOrCreateTagBuilder(ModItems.REGULAR_BADGE_TAG).add(BadgeItem.getAllBadges(false).toArray(new BadgeItem[0]));
      getOrCreateTagBuilder(ModItems.SECRET_BADGE_TAG).add(BadgeItem.getAllBadges(true).toArray(new BadgeItem[0]));
    }
  }

}
