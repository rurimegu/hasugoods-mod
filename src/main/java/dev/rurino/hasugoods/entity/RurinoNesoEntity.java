package dev.rurino.hasugoods.entity;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.component.INesoComponent;
import dev.rurino.hasugoods.component.ModComponents;
import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.network.EmitParticlesPayload;
import dev.rurino.hasugoods.network.ModNetwork;
import dev.rurino.hasugoods.particle.HasuParticleEffect;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import dev.rurino.hasugoods.util.MathUtils;
import dev.rurino.hasugoods.util.Timer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class RurinoNesoEntity extends NesoEntity {
  private static final int CHECK_BOX_INTERVAL = 8;
  private static final int EMIT_PARTICLES_INTERVAL = 40;
  private static final int MAX_LIGHT_LEVEL = 3;
  private static final int PARTICLE_MAX_AGE = 60;

  private static boolean isBoxBoundary(World world, BlockPos pos) {
    BlockState state = world.getBlockState(pos);
    return state.isSolidBlock(world, pos);
  }

  private final NesoConfig.Rurino config;
  private final Timer checkBoxTimer;
  private final Timer emitParticlesTimer;
  private boolean isInBox = false;
  private float chargeBoost = 0;

  public RurinoNesoEntity(EntityType<? extends LivingEntity> type, World world, NesoSize size) {
    super(type, world, CharaUtils.RURINO_KEY, size);
    config = NesoConfig.getConfig(NesoConfig.Rurino.class, CharaUtils.RURINO_KEY, size);
    checkBoxTimer = Timer.loop(CHECK_BOX_INTERVAL, this::checkIfInBox);
    emitParticlesTimer = Timer.loop(EMIT_PARTICLES_INTERVAL, this::maybeEmitParticles);
  }

  @Override
  public NesoConfig.Rurino getConfig() {
    return config;
  }

  private Optional<BlockPos> getBoundaryInDir(World world, Direction direction) {
    BlockPos pos = getBlockPos();
    for (int i = 0; i < config.maxBoxSize(); i++) {
      if (isBoxBoundary(world, pos)) {
        return Optional.of(pos);
      }
      pos = pos.offset(direction);
    }
    return Optional.empty();
  }

  private float getParticleSize() {
    return switch (getNesoSize()) {
      case SMALL -> 0.1f;
      case MEDIUM -> 0.2f;
      case LARGE -> 0.3f;
    } * (chargeBoost > 0 ? 1.5f : 1f);
  }

  private void maybeEmitParticles() {
    if (!isInBox)
      return;
    ServerWorld world = (ServerWorld) getWorld();
    boolean hasMeguBoost = chargeBoost > 0;
    Optional<BlockPos> topBoundaryOptional = getBoundaryInDir(world, Direction.UP);
    if (topBoundaryOptional.isEmpty()) {
      return;
    }

    BlockPos blockPos = topBoundaryOptional.get();
    float scale = getParticleSize();
    Vec3d pos = blockPos.toCenterPos().add(0, 0.4f, 0);
    Vec3d posDelta = hasMeguBoost ? new Vec3d(scale, 0, 0) : Vec3d.ZERO;

    // Rurino icon
    HasuParticleEffect.Builder builder = HasuParticleEffect.Builder.charaIcon(getCharaKey())
        .initialScale(scale)
        .maxAge(PARTICLE_MAX_AGE);
    EmitParticlesPayload payload = new EmitParticlesPayload(
        EmitParticlesPayload.TYPE_RURINO_CHARGE,
        builder.build(),
        pos.add(posDelta));

    ModNetwork.sendPacketToNearbyPlayers(world, blockPos, payload);

    if (hasMeguBoost) {
      // Megumi icon
      builder = HasuParticleEffect.Builder.charaIcon(CharaUtils.MEGUMI_KEY)
          .initialScale(scale)
          .maxAge(PARTICLE_MAX_AGE);
      payload = new EmitParticlesPayload(
          EmitParticlesPayload.TYPE_RURINO_CHARGE,
          builder.build(),
          pos.add(posDelta.negate()));
      ModNetwork.sendPacketToNearbyPlayers(world, blockPos, payload);
    }
  }

  private void checkIfInBox() {
    BlockPos pos = getBlockPos();
    if (getWorld().getLightLevel(pos) > MAX_LIGHT_LEVEL) {
      // Too bright, not in box
      isInBox = false;
      chargeBoost = 0;
      return;
    }
    Queue<BlockPos> queue = new LinkedList<>();
    HashSet<BlockPos> visited = new HashSet<>();
    int boxSize = 0;
    queue.add(pos);
    float meguChargeBoost = 0;
    World world = getWorld();
    while (!queue.isEmpty()) {
      BlockPos current = queue.poll();
      if (++boxSize > config.maxBoxSize())
        break;
      Box box = new Box(current);
      var meguOptional = world
          .getNonSpectatingEntities(MegumiNesoEntity.class, box)
          .stream()
          .max(Comparator.comparing(NesoEntity::getNesoSize));
      if (meguOptional.isPresent()) {
        var megu = meguOptional.get();
        meguChargeBoost = Math.max(meguChargeBoost, megu.getConfig().rurinoChargeBoost());
      }

      for (BlockPos delta : MathUtils.NEIGHBORS) {
        BlockPos neighbor = current.add(delta);
        if (visited.contains(neighbor))
          continue;
        visited.add(neighbor);
        if (!isBoxBoundary(world, neighbor)) {
          queue.add(neighbor);
        }
      }
    }
    if (boxSize > config.maxBoxSize()) {
      isInBox = false;
      meguChargeBoost = 0;
    } else {
      isInBox = true;
    }
    chargeBoost = meguChargeBoost;
  }

  private void charge(long amount) {
    if (amount <= 0)
      return;
    var nesoComponentOptional = ModComponents.NESO.maybeGet(this);
    if (nesoComponentOptional.isPresent()) {
      INesoComponent nesoComponent = nesoComponentOptional.get();
      long storedEnergy = nesoComponent.getStoredEnergy();
      amount = config.chargeAmount(storedEnergy, amount);
      if (amount > 0) {
        nesoComponent.setStoredEnergy(storedEnergy + amount);
      }
    } else {
      Hasugoods.LOGGER.warn("NesoComponent not found for NesoEntity: {} when charging {}", this, amount);
    }
  }

  private void selfCharge() {
    long amount = 0;
    if (isInBox) {
      amount += config.energyChargeInBoxPerTick();
    }
    amount += (long) (amount * chargeBoost);
    charge(amount);
  }

  @Override
  public void tick() {
    super.tick();
    if (getWorld().isClient)
      return;

    checkBoxTimer.tick();
    emitParticlesTimer.tick();
    selfCharge();
  }

}
