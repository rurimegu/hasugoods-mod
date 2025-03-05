package dev.rurino.hasugoods.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;

public class NesoEntityRenderer extends EntityRenderer<NesoEntity, EntityRenderState> {

  private final ItemStack stack;

  public NesoEntityRenderer(EntityRendererFactory.Context context, ItemStack stack) {
    super(context);
    this.stack = stack;
  }

  @Override
  public EntityRenderState createRenderState() {
    return new EntityRenderState();
  }

  @Override
  public void render(EntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
    super.render(state, matrices, vertexConsumers, light);
    ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    matrices.push();

    matrices.scale(2, 2, 2);
    itemRenderer.renderItem(stack, ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrices,
        vertexConsumers, null, 0);

    matrices.pop();
  }
}
