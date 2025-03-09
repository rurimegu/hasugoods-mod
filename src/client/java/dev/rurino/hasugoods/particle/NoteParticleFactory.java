package dev.rurino.hasugoods.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;

public class NoteParticleFactory implements ParticleFactory<NoteParticleEffect> {
  protected final SpriteProvider spriteProvider;

  public NoteParticleFactory(SpriteProvider spriteProvider) {
    this.spriteProvider = spriteProvider;
  }

  @Override
  public Particle createParticle(NoteParticleEffect type, ClientWorld world, double x, double y, double z, double dx,
      double dy, double dz) {
    return new NoteParticle(world, x, y, z, dx, dy, dz, type.getColorVec(), spriteProvider);
  }

}
