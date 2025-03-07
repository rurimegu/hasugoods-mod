package dev.rurino.hasugoods.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;

public class NesoEntityRenderer extends LivingEntityRenderer<NesoEntity, LivingEntityRenderState, NesoEntityModel> {

  public NesoEntityRenderer(EntityRendererFactory.Context context, NesoEntityModel model) {
    super(context, model, model.getShadowRadius());
  }

  @Override
  public LivingEntityRenderState createRenderState() {
    return new LivingEntityRenderState();
  }

  @Override
  protected boolean hasLabel(NesoEntity livingEntity, double d) {
    return super.hasLabel(livingEntity, d) && (livingEntity.shouldRenderName()
        || livingEntity.hasCustomName() && livingEntity == this.dispatcher.targetedEntity);
  }

  @Override
  public void render(LivingEntityRenderState state, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers,
      int light) {
    matrixStack.push();

    ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
    float g = state.baseScale;
    matrixStack.scale(g, g, g);
    this.setupTransforms(state, matrixStack, state.bodyYaw, g);

    itemRenderer.renderItem(
        this.model.getItemStack(),
        ModelTransformationMode.GROUND,
        light,
        OverlayTexture.DEFAULT_UV,
        matrixStack,
        vertexConsumers,
        null,
        0);

    matrixStack.pop();

    if (state.displayName != null) {
      this.renderLabelIfPresent(state, state.displayName, matrixStack, vertexConsumers, light);
    }
  }

  @Override
  public Identifier getTexture(LivingEntityRenderState state) {
    return null;
  }
}
