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
import net.minecraft.world.WorldAccess;

public class ParticleUtils {

  public static Vec3d getSidePos(int particleNo, Vec3d pos, int particlePerSide) {
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
    return pos.add(x - 0.5, 0, z - 0.5);
  }

  public static void createParticle(ParticleEffect effect, WorldAccess world, Vec3d pos, Vec3d velocity) {
    world.addParticle(effect, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
  }

  public static abstract class Emitter {
    private int triggerCount = 0;

    abstract protected void clientEmit(World world, Vec3d pos);

    protected int getTriggerCount() {
      return triggerCount;
    }

    public void emit(World world, Vec3d pos) {
      if (!world.isClient) {
        Hasugoods.LOGGER.error("Emitter ticked on server side, skipped");
        return;
      }
      this.triggerCount++;
      clientEmit(world, pos);
    }

    public void emit(World world, BlockPos pos) {
      emit(world, pos.toCenterPos());
    }

    public Timed repeat(int triggerInterval, int repeat) {
      return new Timed(this, triggerInterval, repeat);
    }

    public Timed repeat(int triggerInterval) {
      return new Timed(this, triggerInterval);
    }

    public Timed repeat() {
      return new Timed(this, 1);
    }

    public static class Timed {
      public final Timer timer;
      public final Emitter emitter;

      private boolean shouldTrigger = false;

      private void enableTrigger() {
        this.shouldTrigger = true;
      }

      private Timed(Emitter emitter, int triggerInterval, int repeat) {
        this.timer = new Timer(triggerInterval, this::enableTrigger, repeat);
        this.emitter = emitter;
      }

      private Timed(Emitter emitter, int triggerInterval) {
        this.timer = Timer.loop(triggerInterval, this::enableTrigger);
        this.emitter = emitter;
      }

      public int getTriggerCount() {
        return emitter.getTriggerCount();
      }

      public void tick(World world, BlockPos blockPos, int tick) {
        this.timer.tick(tick);
        if (this.shouldTrigger) {
          this.shouldTrigger = false;
          emitter.emit(world, blockPos);
        }
      }

      public void tick(World world, BlockPos blockPos) {
        tick(world, blockPos, 1);
      }

      public void tick(World world, Vec3d pos, int tick) {
        this.timer.tick(tick);
        if (this.shouldTrigger) {
          this.shouldTrigger = false;
          emitter.emit(world, pos);
        }
      }

      public void tick(World world, Vec3d pos) {
        tick(world, pos, 1);
      }

    }

    public static class RandomUp extends Emitter {
      private final float randomParticleProb;
      private final ParticleEffect[] effects;

      public RandomUp(Collection<? extends ParticleEffect> effects, float randomParticleProb) {
        this.effects = effects.toArray(new ParticleEffect[0]);
        this.randomParticleProb = randomParticleProb;
      }

      @Override
      protected void clientEmit(World world, Vec3d pos) {
        Random random = world.getRandom();
        if (random.nextFloat() >= randomParticleProb)
          return;
        pos = pos.add(random.nextDouble() - 0.5, random.nextDouble() - 0.1, random.nextDouble() - 0.5);
        Vec3d velocity = new Vec3d(0, MathHelper.nextDouble(random, 0.015, 0.025), 0);
        ParticleEffect effect = CollectionUtils.getRandomElement(effects, random);
        createParticle(effect, world, pos, velocity);
      }
    }

    public static class Spiral extends Emitter {
      private final int particlePerSide;
      private final Vec3d velocity;
      private final Function<Random, ParticleEffect> effectSupplier;

      public Spiral(Function<Random, ParticleEffect> effectSupplier, int particlePerSide,
          Vec3d velocity) {
        this.effectSupplier = effectSupplier;
        this.particlePerSide = particlePerSide;
        this.velocity = velocity;
      }

      @Override
      protected void clientEmit(World world, Vec3d pos) {
        pos = ParticleUtils.getSidePos(getTriggerCount(), pos.add(0, 0.4, 0), particlePerSide);
        ParticleEffect effect = effectSupplier.apply(world.getRandom());
        createParticle(effect, world, pos, velocity);
      }
    }

    public static class Wave extends Emitter {
      private final int particlePerSide;
      private final Vec3d velocity;
      private final Function<Random, ParticleEffect> effectSupplier;

      public Wave(Function<Random, ParticleEffect> effectSupplier, int particlePerSide, Vec3d velocity) {
        this.effectSupplier = effectSupplier;
        this.particlePerSide = particlePerSide;
        this.velocity = velocity;
      }

      @Override
      protected void clientEmit(World world, Vec3d pos) {
        pos = pos.add(0, 0.9, 0);
        for (int i = 0; i < particlePerSide * 4; i++) {
          Vec3d emitPos = ParticleUtils.getSidePos(i, pos, particlePerSide);
          ParticleEffect effect = effectSupplier.apply(world.getRandom());
          createParticle(effect, world, emitPos, velocity);
        }
      }
    }
  }
}
