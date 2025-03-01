package dev.rurino.hasugoods.entity;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;

public class NesoEntityRenderer extends EntityRenderer<NesoEntity, EntityRenderState> {

  public NesoEntityRenderer(EntityRendererFactory.Context context) {
    super(context);
  }

  @Override
  public EntityRenderState createRenderState() {
    return new EntityRenderState();
  }

}
