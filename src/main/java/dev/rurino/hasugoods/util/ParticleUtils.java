package dev.rurino.hasugoods.util;

import java.util.Collection;
import java.util.function.Function;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ParticleUtils {

  public static Vec3d getSidePos(int particleNo, BlockPos blockPos, int particlePerSide) {
    int side = (particleNo / particlePerSide) % 4;
    double sideProgress = (particleNo % particlePerSide) / (double) particlePerSide;
    double x;
    double z;
    switch (side) {
      case 0:
        x = sideProgress;
        z = 0;
        break;
      case 1:
        x = 1;
        z = sideProgress;
        break;
      case 2:
        x = 1 - sideProgress;
        z = 1;
        break;
      case 3:
        x = 0;
        z = 1 - sideProgress;
        break;
      default:
        throw new IllegalStateException("Unexpected side value: " + side);
    }
    x += blockPos.getX();
    double y = blockPos.getY() + 0.9;
    z += blockPos.getZ();
    return new Vec3d(x, y, z);
  }

  public static void createParticle(ParticleEffect effect, World world, Vec3d pos, Vec3d velocity) {
    world.addParticle(effect, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
  }

  public static abstract class Emitter {
    private int particleTick = 0;
    private final int triggerInterval;

    protected Emitter(int triggerInterval) {
      this.triggerInterval = triggerInterval;
    }

    protected Emitter() {
      this(1);
    }

    abstract protected void clientTick(World world, BlockPos pos);

    protected int getTick() {
      return particleTick;
    }

    protected int getTickInterval() {
      return triggerInterval;
    }

    protected int getTriggerCount() {
      return particleTick / triggerInterval;
    }

    public void tick(World world, BlockPos pos) {
      if (!world.isClient) {
        Hasugoods.LOGGER.warn("Emitter ticked on server side, skipped");
        return;
      }
      particleTick++;
      if (particleTick % triggerInterval == 0) {
        clientTick(world, pos);
      }
    }

    public static class RandomUp extends Emitter {
      private final float randomParticleProb;
      private final ParticleEffect[] effects;

      public RandomUp(Collection<? extends ParticleEffect> effects, int triggerInterval, float randomParticleProb) {
        super(triggerInterval);
        this.effects = effects.toArray(new ParticleEffect[0]);
        this.randomParticleProb = randomParticleProb;
      }

      @Override
      protected void clientTick(World world, BlockPos blockPos) {
        Random random = world.getRandom();
        if (random.nextFloat() >= randomParticleProb)
          return;
        double x = blockPos.getX() + random.nextDouble();
        double y = blockPos.getY() + 0.9;
        double z = blockPos.getZ() + random.nextDouble();
        Vec3d pos = new Vec3d(x, y, z);
        Vec3d velocity = new Vec3d(0, MathHelper.nextDouble(random, 0.015, 0.025), 0);
        ParticleEffect effect = CollectionUtils.getRandomElement(effects, random);
        createParticle(effect, world, pos, velocity);
      }
    }

    public static class Spiral extends Emitter {
      private final int particlePerSide;
      private final Vec3d velocity;
      private final Function<Random, ParticleEffect> effectSupplier;

      public Spiral(Function<Random, ParticleEffect> effectSupplier, int triggerInterval, int particlePerSide,
          Vec3d velocity) {
        super(triggerInterval);
        this.effectSupplier = effectSupplier;
        this.particlePerSide = particlePerSide;
        this.velocity = velocity;
      }

      @Override
      protected void clientTick(World world, BlockPos blockPos) {
        Vec3d pos = ParticleUtils.getSidePos(getTriggerCount(), blockPos, particlePerSide);
        ParticleEffect effect = effectSupplier.apply(world.getRandom());
        createParticle(effect, world, pos, velocity);
      }
    }

    public static class Wave extends Emitter {
      private final int particlePerSide;
      private final Vec3d velocity;
      private final Function<Random, ParticleEffect> effectSupplier;

      public Wave(Function<Random, ParticleEffect> effectSupplier, int triggerInterval, int particlePerSide,
          Vec3d velocity) {
        super(triggerInterval);
        this.effectSupplier = effectSupplier;
        this.particlePerSide = particlePerSide;
        this.velocity = velocity;
      }

      @Override
      protected void clientTick(World world, BlockPos blockPos) {
        for (int i = 0; i < particlePerSide * 4; i++) {
          Vec3d pos = ParticleUtils.getSidePos(i, blockPos, particlePerSide);
          ParticleEffect effect = effectSupplier.apply(world.getRandom());
          createParticle(effect, world, pos, velocity);
        }
      }
    }
  }
}
