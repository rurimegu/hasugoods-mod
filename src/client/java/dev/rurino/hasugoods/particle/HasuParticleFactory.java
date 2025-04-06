package dev.rurino.hasugoods.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class HasuParticleFactory<T extends HasuParticleEffect> implements ParticleFactory<T> {
  protected final SpriteProvider spriteProvider;

  public HasuParticleFactory(SpriteProvider spriteProvider) {
    this.spriteProvider = spriteProvider;
  }

  @Override
  public Particle createParticle(T type, ClientWorld world, double x, double y, double z, double dx,
      double dy, double dz) {
    return new HasuParticle(world,
        x, y, z,
        dx, dy, dz,
        type,
        spriteProvider);
  }

}
