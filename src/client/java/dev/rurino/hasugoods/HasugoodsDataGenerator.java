package dev.rurino.hasugoods;

import dev.rurino.hasugoods.datagen.HasugoodsModelProvider;
import dev.rurino.hasugoods.datagen.HasugoodsRecipeProvider;
import dev.rurino.hasugoods.datagen.HasugoodsTagProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.util.Identifier;

public class HasugoodsDataGenerator implements DataGeneratorEntrypoint {

  public static HasugoodsDataGenerator V;

  private FabricDataGenerator fabricDataGenerator;

  public boolean resourceExists(Identifier id) {
    return fabricDataGenerator.getModContainer().findPath(
        String.format("assets/%s/%s", id.getNamespace(), id.getPath())).isPresent();
  }

  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    this.fabricDataGenerator = fabricDataGenerator;
    V = this;
    FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
    pack.addProvider(HasugoodsModelProvider::new);
    pack.addProvider(HasugoodsTagProvider.ItemTags::new);
    pack.addProvider(HasugoodsRecipeProvider::new);
  }
}
