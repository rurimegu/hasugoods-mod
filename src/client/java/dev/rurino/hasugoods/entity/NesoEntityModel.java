package dev.rurino.hasugoods.entity;

import org.joml.Quaternionf;

import dev.rurino.hasugoods.item.NesoItemRenderer;
import dev.rurino.hasugoods.item.neso.NesoItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class NesoEntityModel extends EntityModel<NesoEntity> {

  private final NesoItem item;
  private final ItemStack stack;
  private final Identifier inHandModelId;
  private VertexConsumerProvider vertexConsumerProvider;
  private boolean isFromPatchouli = false;

  protected NesoEntityModel(NesoItem item) {
    super();
    this.item = item;
    this.stack = new ItemStack(item);
    inHandModelId = NesoItemRenderer.inHandModelId(item);
  }

  public NesoItem getItem() {
    return item;
  }

  public ItemStack getItemStack() {
    return stack;
  }

  public float getShadowRadius() {
    return switch (item.getNesoSize()) {
      case SMALL -> 0.2F;
      case MEDIUM -> 0.4F;
      case LARGE -> 0.8F;
    };
  }

  private float getYTranslation() {
    return switch (item.getNesoSize()) {
      case SMALL -> -1.38F;
      case MEDIUM -> -1.27F;
      case LARGE -> -1.05F;
    };
  }

  @Override
  public void setAngles(NesoEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
      float headPitch) {
    // Ignore angles
    isFromPatchouli = entity.isFromPatchouli();
  }

  public void setVertexConsumerProvider(VertexConsumerProvider vertexConsumerProvider) {
    this.vertexConsumerProvider = vertexConsumerProvider;
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
    matrices.push();

    MinecraftClient client = MinecraftClient.getInstance();
    ItemRenderer itemRenderer = client.getItemRenderer();
    BakedModel model = client.getBakedModelManager().getModel(inHandModelId);

    matrices.scale(isFromPatchouli ? 1.0f : -1.0F, -1.0F, 1.0F);
    matrices.translate(0F, getYTranslation(), 0F);
    matrices.multiply(new Quaternionf().rotateY((float) Math.PI));

    itemRenderer.renderItem(getItemStack(), ModelTransformationMode.GROUND, false, matrices,
        vertexConsumerProvider, light, overlay, model);

    matrices.pop();
  }

}
