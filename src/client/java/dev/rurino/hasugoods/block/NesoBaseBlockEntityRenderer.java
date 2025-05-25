package dev.rurino.hasugoods.block;

import java.util.Optional;

import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.ClientAnimation;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

public class NesoBaseBlockEntityRenderer implements BlockEntityRenderer<AbstractNesoBaseBlockEntity> {
  private static final Identifier BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/beacon_beam.png");
  private static final int BEAM_MAX_Y = 256;

  private final ItemRenderer itemRenderer;

  public NesoBaseBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    itemRenderer = context.getItemRenderer();
  }

  private void renderPositionZero(
      PositionZeroBlockEntity entity,
      float tickDelta,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      int overlay,
      Direction direction) {
    Optional<NesoItem> nesoItemOptional = entity.getNesoItem();
    if (nesoItemOptional.isEmpty())
      return;
    long time = entity.getWorld().getTime();
    int color = entity.getItemColor();
    NesoSize size = nesoItemOptional.get().getNesoSize();
    float radius = switch (size) {
      case SMALL -> 0.1f;
      case MEDIUM -> 0.2f;
      case LARGE -> 0.4f;
    };
    float yOffset = switch (size) {
      case SMALL -> 1.5f;
      case MEDIUM -> 1.7f;
      case LARGE -> 2.1f;
    };
    Vec3d translation = new Vec3d(0, yOffset, 0).add(
        new Vec3d(direction.getUnitVector().mul(radius / 2)));

    matrices.push();
    matrices.translate(translation.x, translation.y, translation.z);
    BeaconBlockEntityRenderer.renderBeam(
        matrices,
        vertexConsumers,
        BEAM_TEXTURE,
        tickDelta,
        1.0f,
        time,
        0,
        BEAM_MAX_Y,
        color,
        radius,
        radius * 1.5f);
    matrices.pop();
  }

  @Override
  public void render(AbstractNesoBaseBlockEntity entity,
      float tickDelta,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      int overlay) {
    ItemStack stack = entity.getItemStack();
    BlockState blockState = entity.getCachedState();
    if (stack.isEmpty() || !entity.isTopAir())
      return;

    Direction direction = blockState.get(AbstractNesoBaseBlock.FACING);

    if (entity instanceof PositionZeroBlockEntity pos0Entity) {
      renderPositionZero(pos0Entity, tickDelta, matrices, vertexConsumers, light, overlay, direction);
    }

    matrices.push();
    matrices.translate(0.5, 1, 0.5);

    ClientAnimation.apply(entity.getStateMachine().getFrame(tickDelta), matrices);

    matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(direction.getHorizontal()));

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
