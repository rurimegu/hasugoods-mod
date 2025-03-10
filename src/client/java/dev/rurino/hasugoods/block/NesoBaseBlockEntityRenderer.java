package dev.rurino.hasugoods.block;

import dev.rurino.hasugoods.entity.NesoEntityModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class NesoBaseBlockEntityRenderer implements BlockEntityRenderer<AbstractNesoBaseBlockEntity> {

  protected final ItemRenderer itemRenderer;

  public NesoBaseBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    itemRenderer = context.getItemRenderer();
  }

  @Override
  public void render(AbstractNesoBaseBlockEntity entity,
      float tickDelta,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      int overlay) {
    ItemStack stack = entity.getItemStack().copy();
    BlockState blockState = entity.getCachedState();
    if (stack.isEmpty() || !entity.isTopAir())
      return;
    stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, NesoEntityModel.NESO_3D_CUSTOM_MODEL_DATA);
    matrices.push();
    matrices.translate(0.5, 1.2, 0.5);

    Direction direction = blockState.get(AbstractNesoBaseBlock.FACING);
    matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(direction.getPositiveHorizontalDegrees()));

    itemRenderer.renderItem(
        stack,
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
