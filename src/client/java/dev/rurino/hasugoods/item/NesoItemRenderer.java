package dev.rurino.hasugoods.item;

import java.util.List;

import dev.rurino.hasugoods.item.neso.NesoItem;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.data.client.ModelIds;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class NesoItemRenderer implements DynamicItemRenderer {
  public static final List<ModelTransformationMode> GUI_MODES = List.of(
      ModelTransformationMode.GUI, ModelTransformationMode.GROUND, ModelTransformationMode.FIXED);
  private final Identifier guiModelId;
  private final Identifier inHandModelId;

  public static Identifier builtinModelId(NesoItem item) {
    return ModelIds.getItemModelId(item);
  }

  public static Identifier guiModelId(Item item) {
    return ModelIds.getItemSubModelId(item, "_gui");
  }

  public static Identifier inHandModelId(Item item) {
    return ModelIds.getItemSubModelId(item, "_in_hand");
  }

  public NesoItemRenderer(NesoItem item) {
    this.guiModelId = guiModelId(item);
    this.inHandModelId = inHandModelId(item);
  }

  @Override
  public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices,
      VertexConsumerProvider vertexConsumers, int light, int overlay) {
    MinecraftClient client = MinecraftClient.getInstance();
    ItemRenderer itemRenderer = client.getItemRenderer();
    matrices.push();
    BakedModel model;
    if (GUI_MODES.contains(mode)) {
      model = client.getBakedModelManager().getModel(guiModelId);
      matrices.translate(0.5F, 0.5F, 0.5F);
    } else {
      model = client.getBakedModelManager().getModel(inHandModelId);
    }
    itemRenderer.renderItem(stack, mode, false, matrices,
        vertexConsumers, light, overlay, model);
    matrices.pop();
  }

}
