package dev.rurino.hasugoods.entity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.component.INesoComponent;
import dev.rurino.hasugoods.component.ModComponents;
import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.MathUtils;
import dev.rurino.hasugoods.util.Timer;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class RurinoNesoEntity extends NesoEntity {
  private static final int CHECK_BOX_INTERVAL = 8;

  private final NesoConfig.Rurino config;
  private final Timer checkBoxTimer;
  private boolean isInBox = false;
  private float chargeBoost = 0;

  public RurinoNesoEntity(EntityType<? extends LivingEntity> type, World world, NesoSize size) {
    super(type, world, CharaUtils.RURINO_KEY, size);
    config = NesoConfig.getConfig(NesoConfig.Rurino.class, CharaUtils.RURINO_KEY, size);
    checkBoxTimer = Timer.loop(CHECK_BOX_INTERVAL, this::checkIfInBox);
  }

  private void checkIfInBox() {
    BlockPos pos = getBlockPos();
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
          .getEntitiesByClass(NesoEntity.class, box, e -> e.getCharaKey() == CharaUtils.MEGUMI_KEY)
          .stream().findFirst();
      if (meguOptional.isPresent()) {
        var megu = meguOptional.get();
        meguChargeBoost = Math.max(meguChargeBoost,
            NesoConfig.getConfig(NesoConfig.Megumi.class, megu.getCharaKey(), megu.getNesoSize()).rurinoChargeBoost());
      }

      for (BlockPos delta : MathUtils.NEIGHBORS) {
        BlockPos neighbor = current.add(delta);
        if (visited.contains(neighbor))
          continue;
        visited.add(neighbor);
        BlockState state = world.getBlockState(neighbor);
        if (!state.isSolidBlock(world, pos)) {
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
    amount *= (1 + chargeBoost);
    charge(amount);
  }

  @Override
  public void tick() {
    super.tick();
    if (getWorld().isClient)
      return;

    checkBoxTimer.tick();
    selfCharge();
  }

}
