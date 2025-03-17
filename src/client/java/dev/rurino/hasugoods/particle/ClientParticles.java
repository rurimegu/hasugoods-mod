package dev.rurino.hasugoods.particle;

import dev.rurino.hasugoods.util.CharaUtils;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;

public class ClientParticles {
  @SuppressWarnings("unchecked")
  private static ParticleType<HasuParticleEffect> castToHasuParticleEffect(
      ParticleType<? extends ParticleEffect> type) {
    return (ParticleType<HasuParticleEffect>) type;
  }

  public static void initialize() {
    ParticleFactoryRegistry particleFactoryRegistry = ParticleFactoryRegistry.getInstance();
    particleFactoryRegistry.register(ModParticles.NOTE_PARTICLE_EFFECT, HasuParticleFactory::new);
    particleFactoryRegistry.register(ModParticles.QUESTION_MARK_PARTICLE_EFFECT,
        HasuParticleFactory::new);
    for (String charaKey : CharaUtils.ALL_CHARA_KEYS) {
      ParticleType<HasuParticleEffect> type = castToHasuParticleEffect(ModParticles.getCharaIcon(charaKey));
      particleFactoryRegistry.register(type, HasuParticleFactory::new);
    }
  }
}
