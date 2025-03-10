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

  protected ShapedRecipeJsonBuilder buildGinkoRecipe(
      ShapedRecipeJsonBuilder builder,
      NesoItem smallNeso,
      BadgeItem badge) {
    return builder
        .pattern("OR ")
        .pattern("OXB")
        .pattern(" BW")
        .input('O', Items.BLACK_WOOL)
        .input('R', Items.RED_WOOL)
        .input('B', Items.LIGHT_BLUE_WOOL)
        .input('W', Items.WHITE_WOOL)
        .input('X', badge);
  }

  protected ShapedRecipeJsonBuilder buildHimeRecipe(
      ShapedRecipeJsonBuilder builder,
      NesoItem smallNeso,
      BadgeItem badge) {
    return builder
        .pattern("PO ")
        .pattern("PXW")
        .pattern(" WB")
        .input('O', Items.BLACK_WOOL)
        .input('P', Items.MAGENTA_WOOL)
        .input('B', Items.LIGHT_BLUE_WOOL)
        .input('W', Items.WHITE_WOOL)
        .input('X', badge);
  }

  protected ShapedRecipeJsonBuilder buildKosuzuRecipe(
      ShapedRecipeJsonBuilder builder,
      NesoItem smallNeso,
      BadgeItem badge) {
    return builder
        .pattern("RG ")
        .pattern("GXB")
        .pattern(" BW")
        .input('R', Items.BROWN_WOOL)
        .input('G', Items.LIGHT_GRAY_WOOL)
        .input('B', Items.LIGHT_BLUE_WOOL)
        .input('W', Items.WHITE_WOOL)
        .input('X', badge);
  }

  protected ShapedRecipeJsonBuilder buildKahoRecipe(
      ShapedRecipeJsonBuilder builder,
      NesoItem smallNeso,
      BadgeItem badge) {
    return builder
        .pattern("OB ")
        .pattern("OXB")
        .pattern(" BW")
        .input('O', Items.ORANGE_WOOL)
        .input('B', Items.LIGHT_BLUE_WOOL)
        .input('W', Items.WHITE_WOOL)
        .input('X', badge);
  }

  protected ShapedRecipeJsonBuilder buildRurinoRecipe(
      ShapedRecipeJsonBuilder builder,
      NesoItem smallNeso,
      BadgeItem badge) {
    return builder
        .pattern("YP ")
        .pattern("CXB")
        .pattern(" BW")
        .input('P', Items.PINK_WOOL)
        .input('Y', Items.YELLOW_WOOL)
        .input('C', Items.CYAN_WOOL)
        .input('B', Items.LIGHT_BLUE_WOOL)
        .input('W', Items.WHITE_WOOL)
        .input('X', badge);
  }

  protected ShapedRecipeJsonBuilder buildSayakaRecipe(
      ShapedRecipeJsonBuilder builder,
      NesoItem smallNeso,
      BadgeItem badge) {
    return builder
        .pattern("OO ")
        .pattern("OXB")
        .pattern(" BW")
        .input('O', Items.BLUE_WOOL)
        .input('B', Items.LIGHT_BLUE_WOOL)
        .input('W', Items.WHITE_WOOL)
        .input('X', badge);
  }

  protected ShapedRecipeJsonBuilder buildKozueRecipe(
      ShapedRecipeJsonBuilder builder,
      NesoItem smallNeso,
      BadgeItem badge) {
    return builder
        .pattern("PG ")
        .pattern("PXB")
        .pattern(" BW")
        .input('P', Items.PURPLE_WOOL)
        .input('G', Items.GREEN_WOOL)
        .input('B', Items.LIGHT_BLUE_WOOL)
        .input('W', Items.WHITE_WOOL)
        .input('X', badge);
  }

  protected ShapedRecipeJsonBuilder buildMegumiRecipe(
      ShapedRecipeJsonBuilder builder,
      NesoItem smallNeso,
      BadgeItem badge) {
    return builder
        .pattern("OO ")
        .pattern("OXB")
        .pattern(" BW")
        .input('O', Items.BROWN_WOOL)
        .input('B', Items.LIGHT_BLUE_WOOL)
        .input('W', Items.WHITE_WOOL)
        .input('X', badge);
  }

  protected ShapedRecipeJsonBuilder buildTsuzuriRecipe(
      ShapedRecipeJsonBuilder builder,
      NesoItem smallNeso,
      BadgeItem badge) {
    return builder
        .pattern("WW ")
        .pattern("RXB")
        .pattern(" BW")
        .input('R', Items.RED_WOOL)
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
            case OshiUtils.HIME_KEY:
              smallBuilder = buildHimeRecipe(smallBuilder, smallNeso, badge);
              break;
            case OshiUtils.SAYAKA_KEY:
              smallBuilder = buildSayakaRecipe(smallBuilder, smallNeso, badge);
              break;
            case OshiUtils.TSUZURI_KEY:
              smallBuilder = buildTsuzuriRecipe(smallBuilder, smallNeso, badge);
              break;
            case OshiUtils.KOSUZU_KEY:
              smallBuilder = buildKosuzuRecipe(smallBuilder, smallNeso, badge);
              break;
            case OshiUtils.KOZUE_KEY:
              smallBuilder = buildKozueRecipe(smallBuilder, smallNeso, badge);
              break;
            case OshiUtils.MEGUMI_KEY:
              smallBuilder = buildMegumiRecipe(smallBuilder, smallNeso, badge);
              break;
            case OshiUtils.GINKO_KEY:
              smallBuilder = buildGinkoRecipe(smallBuilder, smallNeso, badge);
              break;
            default:
              Hasugoods.LOGGER.error("Unknown oshi key: {} when building neso recipe", mediumNeso.getOshiKey());
              continue;
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
