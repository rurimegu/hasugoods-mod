package dev.rurino.hasugoods.block;

import dev.rurino.hasugoods.entity.NesoEntityModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class NesoBaseBlockEntityRenderer implements BlockEntityRenderer<AbstractNesoBaseBlockEntity> {
  protected static final float ANIM_DURATION_TICK = 80;
  protected static final float Y_TRANSLATION = 0.1f;

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
    entity.animProgress += tickDelta;
    entity.animProgress %= ANIM_DURATION_TICK;
    float progress = entity.animProgress / ANIM_DURATION_TICK;
    if (progress > 0.5)
      progress = (1 - progress) * 2;
    else
      progress *= 2;

    float translateY = MathHelper.easeInOutSine(progress) * Y_TRANSLATION;

    stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, NesoEntityModel.NESO_3D_CUSTOM_MODEL_DATA);
    matrices.push();
    matrices.translate(0.5, 1.2 + translateY, 0.5);

    Direction direction = blockState.get(AbstractNesoBaseBlock.FACING);
    matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(direction.getPositiveHorizontalDegrees()));

    itemRenderer.renderItem(
        stack,
        ModelTransformationMode.GROUND,
        light,
        overlay,
        matrices,
        // Do not show crumbling animation
        renderLayer -> vertexConsumers.getBuffer(NoCrumblingRenderLayer.get(renderLayer)),
        null,
        0);

    matrices.pop();
  }

}
