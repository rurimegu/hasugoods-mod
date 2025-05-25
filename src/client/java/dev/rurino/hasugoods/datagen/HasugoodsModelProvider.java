package dev.rurino.hasugoods.datagen;

import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.HasugoodsDataGenerator;
import dev.rurino.hasugoods.item.NesoItemRenderer;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.util.Identifier;

public class HasugoodsModelProvider extends FabricModelProvider {
  private static class NesoModelSupplier implements Supplier<JsonElement> {
    private static final Identifier PARENT = Identifier.of("special-model-loader", "builtin/obj");

    private static <T> T pick(NesoSize size, T small, T medium, T large) {
      return switch (size) {
        case SMALL -> small;
        case MEDIUM -> medium;
        case LARGE -> large;
      };
    }

    private static double getMultiplier(NesoSize size) {
      return pick(size, 1, 2, 4);
    }

    private static ImmutableMap<String, ?> ground(NesoSize size) {
      int[] rotation = new int[] { 0, 0, 0 };
      double[] translation = new double[] { 0, 0, -1 };
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
      double multiplier = getMultiplier(size);
      int[] rotation = new int[] { 100, size == NesoSize.LARGE ? 180 : 195, 0 };
      double[] translation = new double[] {
          8,
          pick(size, 7.5, 8.5, 9.5),
          pick(size, 8, 9, 10) };
      double[] scale = new double[] { 0.18, 0.18, 0.18 };
      for (int i = 0; i < scale.length; i++) {
        scale[i] *= multiplier;
      }
      return ImmutableMap.of("rotation", rotation, "translation", translation, "scale", scale);
    }

    private static ImmutableMap<String, ?> firstPerson(NesoSize size, boolean left) {
      int[] rotation = new int[] { 30, 180, 0 };
      double[] translation = new double[] { left ? 0 : 16, 4, 0 };
      double[] scale = new double[] { 0.18, 0.18, 0.18 };
      double multiplier = getMultiplier(size);
      for (int i = 0; i < scale.length; i++) {
        scale[i] *= multiplier;
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

  private static class NesoGuiModelSupplier implements Supplier<JsonElement> {
    private static final Identifier PARENT = Identifier.of("minecraft", "item/generated");

    private final NesoItem item;
    private int layerId;

    public NesoGuiModelSupplier(NesoItem item, Identifier id3dModel) {
      this.item = item;
    }

    private void beginLayers() {
      layerId = 0;
    }

    private String nextLayer() {
      return "layer" + layerId++;
    }

    private JsonObject getTextures() {
      JsonObject ret = new JsonObject();
      Identifier id = Hasugoods.id("item/neso/" + item.getCharaKey() + "_icon");
      beginLayers();
      if (HasugoodsDataGenerator.V.resourceExists(id.withPrefixedPath("textures/").withSuffixedPath(".png"))) {
        ret.addProperty(nextLayer(), id.toString());
      } else {
        Hasugoods.LOGGER.warn("Neso icon not found: {}", id);
        id = ModelIds.getItemModelId(BadgeItem.getBadgeItem(item.getCharaKey()).get());
        ret.addProperty(nextLayer(), id.toString());
        ret.addProperty(nextLayer(), Hasugoods.id("item/neso/wip_icon").toString());
      }
      ret.addProperty(nextLayer(), switch (item.getNesoSize()) {
        case SMALL -> Hasugoods.id("item/neso/small").toString();
        case MEDIUM -> Hasugoods.id("item/neso/medium").toString();
        case LARGE -> Hasugoods.id("item/neso/large").toString();
      });
      return ret;
    }

    @Override
    public JsonElement get() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("parent", PARENT.toString());
      jsonObject.add("textures", getTextures());
      jsonObject.addProperty("gui_light", "front");
      return jsonObject;
    }

  }

  private static class BuiltinModelSupplier implements Supplier<JsonElement> {

    @Override
    public JsonElement get() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("parent", "builtin/entity");
      jsonObject.addProperty("gui_light", "front");
      return jsonObject;
    }
  };

  private static class ParticleModelSupplier implements Supplier<JsonElement> {
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
      Identifier modelId = NesoItemRenderer.builtinModelId(neso);
      Identifier inhandModelId = NesoItemRenderer.inHandModelId(neso);
      Identifier guiModelId = NesoItemRenderer.guiModelId(neso);
      itemModelGenerator.writer.accept(inhandModelId, new NesoModelSupplier(neso));
      itemModelGenerator.writer.accept(guiModelId, new NesoGuiModelSupplier(neso, inhandModelId));
      itemModelGenerator.writer.accept(modelId, new BuiltinModelSupplier());
    }
  }

}
