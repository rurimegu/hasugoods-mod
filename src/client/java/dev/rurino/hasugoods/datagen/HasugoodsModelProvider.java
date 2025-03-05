package dev.rurino.hasugoods.datagen;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import dev.rurino.hasugoods.item.neso.NesoItem;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.ModelSupplier;
import net.minecraft.client.data.Models;
import net.minecraft.util.Identifier;

public class HasugoodsModelProvider extends FabricModelProvider {
  private static class NesoModelSupplier implements ModelSupplier {
    private static final Identifier PARENT = Identifier.of("special-model-loader", "builtin/obj");
    private static final ImmutableMap<String, Map<String, ?>> DISPLAY_SMALL = ImmutableMap.of(
        "gui", ImmutableMap.of(
            "rotation", new int[] { 30, 225, 0 },
            "translation", new double[] { 0, 0, 0 },
            "scale", new double[] { 1, 1, 1 }),
        "ground", ImmutableMap.of(
            "rotation", new int[] { 0, 0, 0 },
            "translation", new double[] { 0, 1, 0 },
            "scale", new double[] { 0.12, 0.12, 0.12 }),
        "fixed", ImmutableMap.of(
            "rotation", new int[] { 0, 0, 0 },
            "translation", new double[] { 0, 0, 0 },
            "scale", new double[] { 0.5, 0.5, 0.5 }),
        "thirdperson_righthand", ImmutableMap.of(
            "rotation", new int[] { 75, 45, 0 },
            "translation", new double[] { 0, 2.5, 0 },
            "scale", new double[] { 0.375, 0.375, 0.375 }),
        "firstperson_righthand", ImmutableMap.of(
            "rotation", new int[] { 0, 45, 0 },
            "translation", new double[] { 0, 0, 0 },
            "scale", new double[] { 0.40, 0.40, 0.40 }),
        "firstperson_lefthand", ImmutableMap.of(
            "rotation", new int[] { 0, 225, 0 },
            "translation", new double[] { 0, 0, 0 },
            "scale", new double[] { 0.40, 0.40, 0.40 }));
    private static final ImmutableMap<String, Map<String, ?>> DISPLAY_MEDIUM = ImmutableMap.of(
        "gui", ImmutableMap.of(
            "rotation", new int[] { 30, 225, 0 },
            "translation", new double[] { 0, 0, 0 },
            "scale", new double[] { 1, 1, 1 }),
        "ground", ImmutableMap.of(
            "rotation", new int[] { 0, 0, 0 },
            "translation", new double[] { 0, 2, 0 },
            "scale", new double[] { 0.24, 0.24, 0.24 }),
        "fixed", ImmutableMap.of(
            "rotation", new int[] { 0, 0, 0 },
            "translation", new double[] { 0, 0, 0 },
            "scale", new double[] { 0.5, 0.5, 0.5 }),
        "thirdperson_righthand", ImmutableMap.of(
            "rotation", new int[] { 75, 45, 0 },
            "translation", new double[] { 0, 2.5, 0 },
            "scale", new double[] { 0.375, 0.375, 0.375 }),
        "firstperson_righthand", ImmutableMap.of(
            "rotation", new int[] { 0, 45, 0 },
            "translation", new double[] { 0, 0, 0 },
            "scale", new double[] { 0.40, 0.40, 0.40 }),
        "firstperson_lefthand", ImmutableMap.of(
            "rotation", new int[] { 0, 225, 0 },
            "translation", new double[] { 0, 0, 0 },
            "scale", new double[] { 0.40, 0.40, 0.40 }));
    private static final ImmutableMap<String, Map<String, ?>> DISPLAY_LARGE = ImmutableMap.of(
        "gui", ImmutableMap.of(
            "rotation", new int[] { 30, 225, 0 },
            "translation", new double[] { 0, 0, 0 },
            "scale", new double[] { 1, 1, 1 }),
        "ground", ImmutableMap.of(
            "rotation", new int[] { 0, 0, 0 },
            "translation", new double[] { 0, 4, 0 },
            "scale", new double[] { 0.48, 0.48, 0.48 }),
        "fixed", ImmutableMap.of(
            "rotation", new int[] { 0, 0, 0 },
            "translation", new double[] { 0, 0, 0 },
            "scale", new double[] { 0.5, 0.5, 0.5 }),
        "thirdperson_righthand", ImmutableMap.of(
            "rotation", new int[] { 75, 45, 0 },
            "translation", new double[] { 0, 2.5, 0 },
            "scale", new double[] { 0.375, 0.375, 0.375 }),
        "firstperson_righthand", ImmutableMap.of(
            "rotation", new int[] { 0, 45, 0 },
            "translation", new double[] { 0, 0, 0 },
            "scale", new double[] { 0.40, 0.40, 0.40 }),
        "firstperson_lefthand", ImmutableMap.of(
            "rotation", new int[] { 0, 225, 0 },
            "translation", new double[] { 0, 0, 0 },
            "scale", new double[] { 0.40, 0.40, 0.40 }));
    private final NesoItem item;

    public NesoModelSupplier(NesoItem item) {
      this.item = item;
    }

    public JsonElement get() {
      String oshiKey = item.getOshiKey();
      Identifier modelId = Hasugoods.id("models/item/neso/" + oshiKey + "/model.obj");
      JsonObject jsonObject = new JsonObject();
      var DISPLAY = switch (item.getNesoSize()) {
        case SMALL -> DISPLAY_SMALL;
        case MEDIUM -> DISPLAY_MEDIUM;
        case LARGE -> DISPLAY_LARGE;
      };
      jsonObject.addProperty("parent", PARENT.toString());
      jsonObject.addProperty("model", modelId.toString());
      jsonObject.add("display", Hasugoods.GSON.toJsonTree(DISPLAY));
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
      String oshiKey = neso.getOshiKey();
      Identifier modelId = Hasugoods.id("item/" + NesoItem.nesoKey(oshiKey, neso.getNesoSize()));
      itemModelGenerator.modelCollector.accept(modelId, new NesoModelSupplier(neso));
      itemModelGenerator.register(neso);
    }
  }

}
