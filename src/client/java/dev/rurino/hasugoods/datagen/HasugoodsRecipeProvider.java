package dev.rurino.hasugoods.datagen;

import java.util.concurrent.CompletableFuture;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.block.ModBlocks;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

public class HasugoodsRecipeProvider extends FabricRecipeProvider {

  public HasugoodsRecipeProvider(FabricDataOutput output,
      CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
    super(output, registriesFuture);
  }

  private ShapedRecipeJsonBuilder buildGinkoRecipe(
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

  private ShapedRecipeJsonBuilder buildHimeRecipe(
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

  private ShapedRecipeJsonBuilder buildKosuzuRecipe(
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

  private ShapedRecipeJsonBuilder buildKahoRecipe(
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

  private ShapedRecipeJsonBuilder buildRurinoRecipe(
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

  private ShapedRecipeJsonBuilder buildSayakaRecipe(
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

  private ShapedRecipeJsonBuilder buildKozueRecipe(
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

  private ShapedRecipeJsonBuilder buildMegumiRecipe(
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

  private ShapedRecipeJsonBuilder buildTsuzuriRecipe(
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

  private void generateNesoBaseRecipes(RecipeExporter exporter) {
    Item nesoBaseBlockItem = ModBlocks.NESO_BASE_BLOCK.asItem();
    Item positionZeroBlockItem = ModBlocks.POSITION_ZERO_BLOCK.asItem();

    ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, nesoBaseBlockItem)
        .input(ModItems.TAG_REGULAR_BADGES)
        .input(Items.WHITE_CONCRETE)
        .input(Items.NOTE_BLOCK)
        .criterion("has_regular_badge", conditionsFromTag(ModItems.TAG_REGULAR_BADGES))
        .offerTo(exporter);
    ShapedRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, positionZeroBlockItem)
        .pattern("SNS")
        .pattern("RBR")
        .pattern(" R ")
        .input('S', ModItems.TAG_SECRET_BADGES)
        .input('N', nesoBaseBlockItem)
        .input('R', Items.RED_CONCRETE)
        .input('B', Items.BEACON)
        .criterion("has_secret_badge", conditionsFromTag(ModItems.TAG_SECRET_BADGES))
        .criterion(hasItem(nesoBaseBlockItem), conditionsFromItem(nesoBaseBlockItem))
        .offerTo(exporter);
  }

  @Override
  public void generate(RecipeExporter exporter) {
    for (NesoItem mediumNeso : NesoItem.getAllNesos(NesoSize.MEDIUM)) {
      NesoItem smallNeso = NesoItem.getNesoItem(mediumNeso.getCharaKey(), NesoSize.SMALL).orElseThrow();
      BadgeItem badge = BadgeItem.getBadgeItem(mediumNeso.getCharaKey(), false).orElseThrow();
      BadgeItem secretBadge = BadgeItem.getBadgeItem(mediumNeso.getCharaKey(), true).orElseThrow();
      ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, mediumNeso)
          .pattern("BSB")
          .pattern("BXB")
          .pattern("BBB")
          .input('B', badge)
          .input('S', secretBadge)
          .input('X', smallNeso)
          .criterion(hasItem(smallNeso), conditionsFromItem(smallNeso))
          .showNotification(true)
          .offerTo(exporter);
      ShapedRecipeJsonBuilder smallBuilder = ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, smallNeso);
      switch (mediumNeso.getCharaKey()) {
        case CharaUtils.KAHO_KEY:
          smallBuilder = buildKahoRecipe(smallBuilder, smallNeso, badge);
          break;
        case CharaUtils.RURINO_KEY:
          smallBuilder = buildRurinoRecipe(smallBuilder, smallNeso, badge);
          break;
        case CharaUtils.HIME_KEY:
          smallBuilder = buildHimeRecipe(smallBuilder, smallNeso, badge);
          break;
        case CharaUtils.SAYAKA_KEY:
          smallBuilder = buildSayakaRecipe(smallBuilder, smallNeso, badge);
          break;
        case CharaUtils.TSUZURI_KEY:
          smallBuilder = buildTsuzuriRecipe(smallBuilder, smallNeso, badge);
          break;
        case CharaUtils.KOSUZU_KEY:
          smallBuilder = buildKosuzuRecipe(smallBuilder, smallNeso, badge);
          break;
        case CharaUtils.KOZUE_KEY:
          smallBuilder = buildKozueRecipe(smallBuilder, smallNeso, badge);
          break;
        case CharaUtils.MEGUMI_KEY:
          smallBuilder = buildMegumiRecipe(smallBuilder, smallNeso, badge);
          break;
        case CharaUtils.GINKO_KEY:
          smallBuilder = buildGinkoRecipe(smallBuilder, smallNeso, badge);
          break;
        default:
          Hasugoods.LOGGER.error("Unknown chara key: {} when building neso recipe", mediumNeso.getCharaKey());
          continue;
      }
      smallBuilder
          .criterion(hasItem(badge), conditionsFromItem(badge))
          .showNotification(true)
          .offerTo(exporter);
    }
    generateNesoBaseRecipes(exporter);
  }

  @Override
  public String getName() {
    return "HasugoodsRecipeProvider";
  }
}
