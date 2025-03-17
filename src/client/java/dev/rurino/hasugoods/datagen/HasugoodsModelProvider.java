package dev.rurino.hasugoods.datagen;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ItemModels;
import net.minecraft.client.data.ModelIds;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.data.Models;
import net.minecraft.client.render.item.model.ItemModel;
import net.minecraft.client.render.item.property.bool.CustomModelDataFlagProperty;
import net.minecraft.util.Identifier;

public class HasugoodsModelProvider extends FabricModelProvider {
  private static class NesoModelSupplier implements ModelSupplier {
    private static final Identifier PARENT = Identifier.of("special-model-loader", "builtin/obj");

    private static double getMultiplier(NesoSize size) {
      return switch (size) {
        case SMALL -> 1;
        case MEDIUM -> 2;
        case LARGE -> 4;
      };
    }

    private static ImmutableMap<String, ?> ground(NesoSize size) {
      int[] rotation = new int[] { 0, 0, 0 };
      double[] translation = new double[] { 0, 2, -1 };
      double[] scale = new double[] { 0.24, 0.24, 0.24 };
      double multiplier = getMultiplier(size);
      for (int i = 0; i < scale.length; i++) {
        scale[i] *= multiplier;
      }
      for (int i = 0; i < translation.length; i++) {
        translation[i] *= multiplier;
      }
      return ImmutableMap.of("rotation", rotation, "translation", translation, "scale", scale);
    }

    private static ImmutableMap<String, ?> thirdPerson(NesoSize size, boolean left) {
      int[] rotation = new int[] { 100, size == NesoSize.LARGE ? 180 : 195, 0 };
      double[] translation = new double[] { 0, 0, 0 };
      double[] scale = new double[] { 0.18, 0.18, 0.18 };
      double multiplier = getMultiplier(size);
      for (int i = 0; i < scale.length; i++) {
        scale[i] *= multiplier;
      }
      for (int i = 0; i < translation.length; i++) {
        translation[i] *= multiplier;
      }
      return ImmutableMap.of("rotation", rotation, "translation", translation, "scale", scale);
    }

    private static ImmutableMap<String, ?> firstPerson(NesoSize size, boolean left) {
      int[] rotation = new int[] {
          size == NesoSize.LARGE ? 30 : 45,
          size == NesoSize.LARGE ? 180 : 195,
          0 };
      double[] translation = new double[] { 1, size == NesoSize.LARGE ? -0.25 : 0, 0 };
      double[] scale = new double[] { 0.18, 0.18, 0.18 };
      double multiplier = getMultiplier(size);
      for (int i = 0; i < scale.length; i++) {
        scale[i] *= multiplier;
      }
      for (int i = 0; i < translation.length; i++) {
        translation[i] *= multiplier;
      }
      return ImmutableMap.of("rotation", rotation, "translation", translation, "scale", scale);
    }

    private static ImmutableMap<String, Map<String, ?>> display(NesoSize size) {
      return ImmutableMap.of(
          "ground", ground(size),
          "firstperson_lefthand", firstPerson(size, true),
          "firstperson_righthand", firstPerson(size, false),
          "thirdperson_lefthand", thirdPerson(size, true),
          "thirdperson_righthand", thirdPerson(size, false));
    }

    private final NesoItem item;

    public NesoModelSupplier(NesoItem item) {
      this.item = item;
    }

    public JsonElement get() {
      String charaKey = item.getCharaKey();
      Identifier modelId = Hasugoods.id("models/item/neso/" + charaKey + "/model.obj");
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("parent", PARENT.toString());
      jsonObject.addProperty("model", modelId.toString());
      jsonObject.add("display", Hasugoods.GSON.toJsonTree(display(item.getNesoSize())));
      return jsonObject;
    }
  }

  private static class ParticleModelSupplier implements ModelSupplier {
    private final String charaKey;

    public ParticleModelSupplier(String charaKey) {
      this.charaKey = charaKey;
    }

    public JsonElement get() {
      JsonObject jsonObject = new JsonObject();
      JsonArray textures = new JsonArray();
      textures.add(Hasugoods.id(charaKey + "_icon").toString());
      jsonObject.add("textures", textures);
      return jsonObject;
    }
  }

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
      itemModelGenerator.register(item, Models.GENERATED);
    }
    for (var neso : NesoItem.getAllNesos()) {
      Identifier inhandModelId = ModelIds.getItemSubModelId(neso, "_in_hand");
      Identifier modelId = ModelIds.getItemModelId(BadgeItem.getBadgeItem(neso.getCharaKey()).get());
      itemModelGenerator.modelCollector.accept(inhandModelId, new NesoModelSupplier(neso));
      // Register hand model
      ItemModel.Unbaked guiModel = ItemModels.basic(modelId);
      ItemModel.Unbaked handModel = ItemModels.basic(inhandModelId);
      ItemModel.Unbaked model = ItemModels.condition(new CustomModelDataFlagProperty(0), handModel, guiModel);
      itemModelGenerator.output.accept(neso, ItemModelGenerator.createModelWithInHandVariant(model, handModel));
    }
  }

}
