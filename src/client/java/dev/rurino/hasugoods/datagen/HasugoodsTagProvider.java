package dev.rurino.hasugoods.datagen;

import java.util.concurrent.CompletableFuture;

import dev.rurino.hasugoods.block.ModBlocks;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import dev.rurino.hasugoods.item.neso.NesoItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;

public class HasugoodsTagProvider {
  public static class ItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public ItemTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    protected void configure(WrapperLookup wrapperLookup) {
      getOrCreateTagBuilder(ModItems.TAG_REGULAR_BADGES).add(BadgeItem.getAllBadges(false).toArray(new BadgeItem[0]));
      getOrCreateTagBuilder(ModItems.TAG_SECRET_BADGES).add(BadgeItem.getAllBadges(true).toArray(new BadgeItem[0]));
      for (NesoItem neso : NesoItem.getAllNesos()) {
        getOrCreateTagBuilder(ModItems.TAG_NESOS).add(neso);
        switch (neso.getNesoSize()) {
          case SMALL -> getOrCreateTagBuilder(ModItems.TAG_SMALL_NESOS).add(neso);
          case MEDIUM -> getOrCreateTagBuilder(ModItems.TAG_MEDIUM_NESOS).add(neso);
          case LARGE -> getOrCreateTagBuilder(ModItems.TAG_LARGE_NESOS).add(neso);
        }
      }
    }
  }

  public static class BlockTagProvider extends FabricTagProvider.BlockTagProvider {

    public BlockTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
      super(output, registriesFuture);
    }

    @Override
    protected void configure(WrapperLookup wrapperLookup) {
      getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
          .add(ModBlocks.NESO_BASE_BLOCK, ModBlocks.POSITION_ZERO_BLOCK);
    }
  }

}
