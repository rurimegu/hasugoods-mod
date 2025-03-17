package dev.rurino.hasugoods.particle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.CharaUtils;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
  public static final String NAME_NOTE_PARTICLE = "note_particle";
  public static final ParticleType<HasuParticleEffect> NOTE_PARTICLE_EFFECT = FabricParticleTypes.complex(
      HasuParticleEffect.CODEC,
      HasuParticleEffect.PACKET_CODEC);

  public static final String NAME_QUESTION_MARK_PARTICLE = "question_mark_particle";
  public static final ParticleType<HasuParticleEffect> QUESTION_MARK_PARTICLE_EFFECT = FabricParticleTypes.complex(
      HasuParticleEffect.CODEC,
      HasuParticleEffect.PACKET_CODEC);

  private static final Map<String, ParticleType<? extends ParticleEffect>> PARTICLES = new HashMap<>();

  public static void register(String name, ParticleType<? extends ParticleEffect> particleType) {
    Identifier id = Hasugoods.id(name);
    Hasugoods.LOGGER.info("Register particle: {}", id);
    PARTICLES.put(name, Registry.register(Registries.PARTICLE_TYPE, id, particleType));
  }

  public static ParticleType<? extends ParticleEffect> get(String name) {
    return PARTICLES.get(name);
  }

  public static String getCharaIconName(String charaKey) {
    return charaKey + "_icon_particle";
  }

  public static ParticleType<? extends ParticleEffect> getCharaIcon(String charaKey) {
    return get(getCharaIconName(charaKey));
  }

  public static Collection<ParticleType<?>> all() {
    return PARTICLES.values();
  }

  public static void initialize() {
    register(NAME_NOTE_PARTICLE, NOTE_PARTICLE_EFFECT);
    register(NAME_QUESTION_MARK_PARTICLE, QUESTION_MARK_PARTICLE_EFFECT);
    for (String charaKey : CharaUtils.ALL_CHARA_KEYS) {
      register(charaKey + "_icon_particle", FabricParticleTypes.complex(
          HasuParticleEffect.CODEC,
          HasuParticleEffect.PACKET_CODEC));
    }
  }
}
