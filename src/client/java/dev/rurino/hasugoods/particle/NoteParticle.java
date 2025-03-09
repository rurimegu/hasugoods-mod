package dev.rurino.hasugoods.particle;

import org.joml.Vector3f;

import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;

public class NoteParticle extends SpriteBillboardParticle {
  private static final int MAX_AGE = 40;
  private static final float SCALE = 0.1F;

  protected final SpriteProvider spriteProvider;

  public NoteParticle(ClientWorld world,
      double x, double y, double z,
      double velocityX, double velocityY, double velocityZ,
      Vector3f color,
      SpriteProvider spriteProvider) {
    super(world, x, y, z, velocityX, velocityY, velocityZ);
    this.setVelocity(velocityX, velocityY, velocityZ);
    this.scale = SCALE;
    this.maxAge = MAX_AGE;
    this.red = color.x;
    this.green = color.y;
    this.blue = color.z;
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
      this.scale = SCALE * MathHelper.easeInOutSine(1 - this.age / (float) this.maxAge);
    }
    super.tick();
  }
}
