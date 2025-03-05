package dev.rurino.hasugoods;

import dev.rurino.hasugoods.datagen.HasugoodsModelProvider;
import dev.rurino.hasugoods.datagen.HasugoodsTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class HasugoodsDataGenerator implements DataGeneratorEntrypoint {
  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
    pack.addProvider(HasugoodsModelProvider::new);
    pack.addProvider(HasugoodsTagProvider.ItemTags::new);
  }
}
