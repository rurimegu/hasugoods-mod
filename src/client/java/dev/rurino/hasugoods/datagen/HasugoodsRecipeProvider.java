package dev.rurino.hasugoods.datagen;

import java.util.concurrent.CompletableFuture;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.OshiUtils;
import dev.rurino.hasugoods.util.OshiUtils.NesoSize;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

public class HasugoodsRecipeProvider extends FabricRecipeProvider {

  public HasugoodsRecipeProvider(FabricDataOutput output,
      CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
    super(output, registriesFuture);
  }

  protected ShapedRecipeJsonBuilder buildKahoRecipe(ShapedRecipeJsonBuilder builder, NesoItem smallNeso,
      BadgeItem badge) {
    return builder
        .pattern("BO ")
        .pattern("OXB")
        .pattern(" BW")
        .input('O', Items.ORANGE_WOOL)
        .input('B', Items.LIGHT_BLUE_WOOL)
        .input('W', Items.WHITE_WOOL)
        .input('X', badge);
  }

  protected ShapedRecipeJsonBuilder buildRurinoRecipe(ShapedRecipeJsonBuilder builder, NesoItem smallNeso,
      BadgeItem badge) {
    return builder
        .pattern("PY ")
        .pattern("CXB")
        .pattern(" BW")
        .input('P', Items.PINK_WOOL)
        .input('Y', Items.YELLOW_WOOL)
        .input('C', Items.CYAN_WOOL)
        .input('B', Items.LIGHT_BLUE_WOOL)
        .input('W', Items.WHITE_WOOL)
        .input('X', badge);
  }

  @Override
  protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
    return new RecipeGenerator(registryLookup, exporter) {
      @Override
      public void generate() {
        for (NesoItem mediumNeso : NesoItem.getAllNesos(NesoSize.MEDIUM)) {
          NesoItem smallNeso = NesoItem.getNesoItem(mediumNeso.getOshiKey(), NesoSize.SMALL).orElseThrow();
          BadgeItem badge = BadgeItem.getBadgeItem(mediumNeso.getOshiKey(), false).orElseThrow();
          BadgeItem secretBadge = BadgeItem.getBadgeItem(mediumNeso.getOshiKey(), true).orElseThrow();
          createShaped(RecipeCategory.MISC, mediumNeso)
              .pattern("BSB")
              .pattern("BXB")
              .pattern("BBB")
              .input('B', badge)
              .input('S', secretBadge)
              .input('X', smallNeso)
              .criterion(hasItem(smallNeso), conditionsFromItem(smallNeso))
              .showNotification(true)
              .offerTo(exporter);
          ShapedRecipeJsonBuilder smallBuilder = createShaped(RecipeCategory.MISC, smallNeso);
          switch (mediumNeso.getOshiKey()) {
            case OshiUtils.KAHO_KEY:
              smallBuilder = buildKahoRecipe(smallBuilder, smallNeso, badge);
              break;
            case OshiUtils.RURINO_KEY:
              smallBuilder = buildRurinoRecipe(smallBuilder, smallNeso, badge);
              break;
            default:
              Hasugoods.LOGGER.error("Unknown oshi key: {} when building neso recipe", mediumNeso.getOshiKey());
          }
          smallBuilder
              .criterion(hasItem(badge), conditionsFromItem(badge))
              .showNotification(true)
              .offerTo(exporter);
        }
      }
    };
  }

  @Override
  public String getName() {
    return "HasugoodsRecipeProvider";
  }
}
