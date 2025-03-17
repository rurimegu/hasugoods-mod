package dev.rurino.hasugoods.particle;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class ClientParticles {

  public static void initialize() {
    ParticleFactoryRegistry.getInstance().register(ModParticles.NOTE_PARTICLE_EFFECT, HasuParticleFactory::new);
    ParticleFactoryRegistry.getInstance().register(ModParticles.QUESTION_MARK_PARTICLE_EFFECT,
        HasuParticleFactory::new);
  }
}
