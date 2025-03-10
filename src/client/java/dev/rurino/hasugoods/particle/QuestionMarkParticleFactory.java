package dev.rurino.hasugoods.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class QuestionMarkParticleFactory implements ParticleFactory<QuestionMarkParticleEffect> {
  protected final SpriteProvider spriteProvider;

  public QuestionMarkParticleFactory(SpriteProvider spriteProvider) {
    this.spriteProvider = spriteProvider;
  }

  @Override
  public Particle createParticle(QuestionMarkParticleEffect type, ClientWorld world, double x, double y, double z,
      double dx,
      double dy, double dz) {
    return new QuestionMarkParticle(world, x, y, z, dx, dy, dz, type.getColorVec(), spriteProvider);
  }

}
