package dev.rurino.hasugoods.entity;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.component.IOshiComponent;
import dev.rurino.hasugoods.component.ModComponents;
import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.network.EmitParticlesPayload;
import dev.rurino.hasugoods.network.ModNetwork;
import dev.rurino.hasugoods.particle.HasuParticleEffect;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import dev.rurino.hasugoods.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

public class MegumiNesoEntity extends NesoEntity {

  private static final float PARTICLE_SCALE = 0.2f;
  private static final int PARTICLE_MAX_AGE = 40;

  private static void emitOshiHenParticles(Entity entity) {
    HasuParticleEffect.Builder builder = HasuParticleEffect.Builder.charaIcon(CharaUtils.MEGUMI_KEY)
        .initialScale(PARTICLE_SCALE * entity.getWidth())
        .maxAge(PARTICLE_MAX_AGE);
    EmitParticlesPayload payload = new EmitParticlesPayload(
        EmitParticlesPayload.TYPE_UP,
        builder.build(),
        entity.getEyePos());

    ModNetwork.sendPacketToPlayersTracking(entity, payload);
  }

  private final NesoConfig.Megumi config;
  private final Timer oshiHenTimer;

  public MegumiNesoEntity(EntityType<? extends LivingEntity> type, World world, NesoSize size) {
    super(type, world, CharaUtils.MEGUMI_KEY, size);
    config = Hasugoods.CONFIG.neso.getConfig(NesoConfig.Megumi.class, CharaUtils.MEGUMI_KEY, size);
    oshiHenTimer = Timer.loop(config.oshiHenInterval(), this::slowTick);
  }

  @Override
  public NesoConfig.Megumi getConfig() {
    return config;
  }

  private void tickOshiHen(Collection<LivingEntity> entities) {
    int increased = 0;
    for (var entity : entities) {
      IOshiComponent oshiComponent = ModComponents.OSHI.get(entity);
      if (!config.shouldOshiHen(entity)) {
        continue;
      }
      if (!oshiComponent.getOshiKey().equals(getCharaKey()) && useEnergy(config.oshiHenEnergy())) {
        oshiComponent.setOshiKey(getCharaKey());
        increased++;
        emitOshiHenParticles(entity);
      }
    }
    if (increased > 0) {
      Hasugoods.LOGGER.info("Megutou increased by {}!", increased);
    }
  }

  private void tickOshi(Collection<LivingEntity> entities) {
    entities.stream()
        .filter(e -> e instanceof MobEntity && ModComponents.OSHI.get(e).getOshiKey().equals(getCharaKey()))
        .map(e -> (MobEntity) e)
        .forEach(e -> {
          var target = e.getTarget();
          if (target == this) {
            if (!useEnergy(config.energyPerAction())) {
              e.setTarget(null);
            }
          } else if (target == null || target instanceof PlayerEntity) {
            if (useEnergy(config.energyPerAction())) {
              e.setTarget(this);
            }
          }
        });
  }

  private void slowTick() {
    Box box = new Box(getBlockPos()).expand(config.oshiHenRadius());
    List<LivingEntity> entities = getWorld()
        .getEntitiesByClass(LivingEntity.class, box, e -> {
          if (e instanceof PlayerEntity || e instanceof INesoEntity)
            return false;
          return ModComponents.OSHI.maybeGet(e).isPresent();
        });
    tickOshiHen(entities);
    tickOshi(entities);
  }

  @Override
  public void tick() {
    super.tick();
    if (getWorld().isClient())
      return;

    oshiHenTimer.tick();
  }
}
