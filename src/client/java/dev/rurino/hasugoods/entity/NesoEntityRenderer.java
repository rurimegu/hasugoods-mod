package dev.rurino.hasugoods.entity;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class NesoEntityRenderer extends LivingEntityRenderer<NesoEntity, NesoEntityModel> {

  public NesoEntityRenderer(EntityRendererFactory.Context context, NesoEntityModel model) {
    super(context, model, model.getShadowRadius());
  }

  @Override
  protected boolean hasLabel(NesoEntity livingEntity) {
    return super.hasLabel(livingEntity) && (livingEntity.shouldRenderName()
        || livingEntity.hasCustomName() && livingEntity == this.dispatcher.targetedEntity);
  }

  @Override
  public void render(NesoEntity livingEntity, float f, float g, MatrixStack matrixStack,
      VertexConsumerProvider vertexConsumerProvider, int i) {
    model.setVertexConsumerProvider(vertexConsumerProvider);
    super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
  }

  @Override
  public Identifier getTexture(NesoEntity entity) {
    // Unused texture
    return Hasugoods.id("textures/particle/note.png");
  }
}
