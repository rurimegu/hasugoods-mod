package dev.rurino.hasugoods.entity;

import dev.rurino.hasugoods.HasugoodsClient;
import dev.rurino.hasugoods.item.neso.NesoItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;

public class NesoEntityModel extends EntityModel<NesoEntity> {

  public static final CustomModelDataComponent NESO_3D_CUSTOM_MODEL_DATA = new CustomModelDataComponent(
      HasugoodsClient.CUSTOM_DATA_NESO_3D);

  private final NesoItem item;
  private final ItemStack stack;
  private VertexConsumerProvider vertexConsumers;

  protected NesoEntityModel(NesoItem item) {
    super();
    this.item = item;
    this.stack = new ItemStack(item);
    this.stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, NESO_3D_CUSTOM_MODEL_DATA);
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

  @Override
  public void setAngles(NesoEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw,
      float headPitch) {
    // Ignore angles
  }

  public void setVertexConsumers(VertexConsumerProvider vertexConsumers) {
    this.vertexConsumers = vertexConsumers;
  }

  @Override
  public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {

    matrices.push();

    ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

    itemRenderer.renderItem(
        this.getItemStack(),
        ModelTransformationMode.GROUND,
        light,
        OverlayTexture.DEFAULT_UV,
        matrices,
        vertexConsumers,
        null,
        0);

    matrices.pop();
  }

}
