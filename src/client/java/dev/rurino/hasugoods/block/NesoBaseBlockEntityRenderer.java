package dev.rurino.hasugoods.block;

import java.util.Optional;

import dev.rurino.hasugoods.item.NesoItemRenderer;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.ClientAnimation;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.data.client.ModelIds;
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
      case SMALL -> 1.4f;
      case MEDIUM -> 1.57f;
      case LARGE -> 2.0f;
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

    Identifier modelId;
    float yOffset = 1F;
    if (stack.getItem() instanceof NesoItem nesoItem) {
      modelId = NesoItemRenderer.inHandModelId(nesoItem);
      yOffset += switch (nesoItem.getNesoSize()) {
        case SMALL -> 0.1F;
        case MEDIUM -> 0.2F;
        case LARGE -> 0.4F;
      };
    } else {
      modelId = ModelIds.getItemModelId(stack.getItem());
    }
    BakedModel model = MinecraftClient.getInstance().getBakedModelManager().getModel(modelId);
    Direction direction = blockState.get(AbstractNesoBaseBlock.FACING);

    if (entity instanceof PositionZeroBlockEntity pos0Entity
        && pos0Entity.getStateMachine().getState() == PositionZeroBlockEntity.ANIM_STATE_IDLE) {
      renderPositionZero(pos0Entity, tickDelta, matrices, vertexConsumers, light, overlay, direction);
    }

    matrices.push();
    matrices.translate(0.5, yOffset, 0.5);

    ClientAnimation.apply(entity.getStateMachine().getFrame(tickDelta), matrices);

    matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(direction.asRotation()));

    itemRenderer.renderItem(stack,
        ModelTransformationMode.GROUND, false, matrices,
        // Do not show crumbling animation
        renderLayer -> vertexConsumers.getBuffer(NoCrumblingRenderLayer.get(renderLayer)),
        light, overlay, model);

    matrices.pop();
  }

}
