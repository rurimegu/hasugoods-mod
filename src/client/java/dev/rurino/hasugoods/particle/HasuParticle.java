package dev.rurino.hasugoods.particle;

import org.joml.Vector3f;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

public class HasuParticle extends SpriteBillboardParticle {
  private static final int MAX_AGE = 40;

  private final SpriteProvider spriteProvider;
  private final HasuParticleEffect effect;

  public HasuParticle(ClientWorld world,
      double x, double y, double z,
      double velocityX, double velocityY, double velocityZ,
      HasuParticleEffect effect,
      SpriteProvider spriteProvider) {
    super(world, x, y, z, velocityX, velocityY, velocityZ);
    this.setVelocity(velocityX, velocityY, velocityZ);
    this.effect = effect;
    Vector3f color = effect.getColorVec();
    this.maxAge = MAX_AGE;
    this.red = color.x;
    this.green = color.y;
    this.blue = color.z;
    this.scale = effect.getInitialScale();
    this.spriteProvider = spriteProvider;
    this.setSpriteForAge(spriteProvider);
  }

  @Override
  public ParticleTextureSheet getType() {
    return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
  }

  @Override
  public void tick() {
    this.setSpriteForAge(spriteProvider);
    if (this.isAlive()) {
      float t = MathHelper.easeInOutSine(this.age / (float) this.maxAge);
      this.scale = MathHelper.lerp(t, effect.getInitialScale(), effect.getFinalScale());
    }
    super.tick();
  }
}
