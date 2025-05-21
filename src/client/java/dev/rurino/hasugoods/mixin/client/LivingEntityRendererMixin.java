package dev.rurino.hasugoods.mixin.client;

import java.util.Optional;

import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.rurino.hasugoods.item.IDisplayIconInHand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>>
    extends EntityRenderer<T> implements FeatureRendererContext<T, M> {

  private static final float MAX_ICON_DISPLAY_DISTANCE = 1000f;

  protected LivingEntityRendererMixin(Context context) {
    super(context);
  }

  private Optional<Identifier> shouldRenderOshiIcon(LivingEntity entity) {
    ClientPlayerEntity player = MinecraftClient.getInstance().player;
    Entity camera = MinecraftClient.getInstance().cameraEntity;
    double squaredDistanceToCamera = entity.squaredDistanceTo(camera);
    if (entity.isInvisible() || entity.isInvisibleTo(player) || squaredDistanceToCamera > MAX_ICON_DISPLAY_DISTANCE) {
      return Optional.empty();
    }
    ClientWorld world = MinecraftClient.getInstance().world;
    if (world == null || player == null || player.isSpectator() || player.isInvisible()) {
      return Optional.empty();
    }
    for (ItemStack stack : player.getHandItems()) {
      if (stack.getItem() instanceof IDisplayIconInHand iconDisplayItem) {
        var iconOptional = iconDisplayItem.getEntityDisplayIconInHand(entity);
        if (iconOptional.isPresent()) {
          return iconOptional;
        }
      }
    }
    return Optional.empty();
  }

  // Render oshi icon at the end
  @Inject(method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("RETURN"), require = 0, allow = 1)
  private void renderOshiIcon(T livingEntity, float yaw, float tickDelta, MatrixStack matrixStack,
      VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
    var oshiIconId = shouldRenderOshiIcon(livingEntity);
    if (oshiIconId.isEmpty()) {
      return;
    }

    matrixStack.push();

    float size = 0.5f;
    float scale = 1f;

    // Render a textured quad facing the camera (billboard)
    float entityHeight = livingEntity.getHeight();
    Vec3d pos = new Vec3d(0, entityHeight + 0.1f, 0);
    matrixStack.translate(pos.x, pos.y, pos.z);
    matrixStack.multiply(MinecraftClient.getInstance().getEntityRenderDispatcher().getRotation());
    matrixStack.scale(scale, scale, scale);
    VertexConsumer vc = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(oshiIconId.get()));
    Matrix4f posMatrix = matrixStack.peek().getPositionMatrix();

    vc.vertex(posMatrix, -size, size * 2, 0).color(255, 255, 255, 255).texture(0, 0).overlay(OverlayTexture.DEFAULT_UV)
        .light(light).normal(matrixStack.peek(), 0, 1, 0);
    vc.vertex(posMatrix, size, size * 2, 0).color(255, 255, 255, 255).texture(1, 0).overlay(OverlayTexture.DEFAULT_UV)
        .light(light).normal(matrixStack.peek(), 0, 1, 0);
    vc.vertex(posMatrix, size, 0, 0).color(255, 255, 255, 255).texture(1, 1).overlay(OverlayTexture.DEFAULT_UV)
        .light(light).normal(matrixStack.peek(), 0, 1, 0);
    vc.vertex(posMatrix, -size, 0, 0).color(255, 255, 255, 255).texture(0, 1).overlay(OverlayTexture.DEFAULT_UV)
        .light(light).normal(matrixStack.peek(), 0, 1, 0);

    matrixStack.pop();
  }
}
