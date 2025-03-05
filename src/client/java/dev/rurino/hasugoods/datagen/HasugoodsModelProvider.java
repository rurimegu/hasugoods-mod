package dev.rurino.hasugoods.datagen;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;

public class HasugoodsModelProvider extends FabricModelProvider {
  public HasugoodsModelProvider(FabricDataOutput output) {
    super(output);
  }

  @Override
  public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
    // Noop

  }

  @Override
  public void generateItemModels(ItemModelGenerator itemModelGenerator) {
    for (var item : BadgeItem.getAllBadges()) {
      Hasugoods.LOGGER.info("Generate item model: " + item);
      itemModelGenerator.register(item, Models.GENERATED);
    }
  }

}
