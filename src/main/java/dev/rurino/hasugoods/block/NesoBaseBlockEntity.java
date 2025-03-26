package dev.rurino.hasugoods.block;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.Timer;
import dev.rurino.hasugoods.util.config.HcVal;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NesoBaseBlockEntity extends AbstractNesoBaseBlockEntity {
  private static final int CHECK_POS_0_INTERVAL = 8;
  private static final String NBT_POS_0 = "pos0";
  private static final HcVal.Long CHARGE_AMOUNT_PER_TICK = NesoConfig.NESO
      .getLong("nesoBaseChargeAmountPerTick", 8);

  static void initialize() {
    Hasugoods.LOGGER.debug("Neso base block entity initialized");
  }

  private BlockPos pos0 = null;
  private final Timer timer = Timer.loop(CHECK_POS_0_INTERVAL, this::syncPos0);

  public NesoBaseBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.NESO_BASE_BLOCK_ENTITY_TYPE, pos, state);
  }

  public void setPos0(BlockPos pos0) {
    this.pos0 = pos0;
    this.unlockItemStack();
    this.markDirty();
    this.sync();
  }

  public BlockPos getPos0() {
    return pos0;
  }

  public PositionZeroBlockEntity getPos0BlockEntity() {
    BlockPos pos = getPos0();
    if (pos == null || !(world.getBlockEntity(pos) instanceof PositionZeroBlockEntity blockEntity))
      return null;
    return blockEntity;
  }

  @Override
  protected ParticleState getParticleState() {
    if (getItemStack().isEmpty())
      return ParticleState.RANDOM;
    return ParticleState.SPIRAL;
  }

  private void syncPos0() {
    if (getPos0() != null) {
      PositionZeroBlockEntity pos0BlockEntity = getPos0BlockEntity();
      if (pos0BlockEntity == null) {
        Hasugoods.LOGGER.warn("Neso base {}: Pos0 block entity not found at {}, disconnceted", pos, getPos0());
        setPos0(null);
      }
      return;
    }
  }

  @Override
  protected void tick(World world, BlockPos blockPos, BlockState blockState) {
    super.tick(world, blockPos, blockState);
    if (!world.isClient) {
      if (getItemStack().getItem() instanceof NesoItem item) {
        item.chargeEnergy(getItemStack(), CHARGE_AMOUNT_PER_TICK.val());
        markDirty();
        sync();
      }
      timer.tick();
    }
  }

  // #region Serialization

  @Override
  protected void writeNbt(NbtCompound nbt, WrapperLookup registries) {
    super.writeNbt(nbt, registries);
    if (pos0 != null) {
      nbt.putLong(NBT_POS_0, pos0.asLong());
    }
  }

  @Override
  protected void readNbt(NbtCompound nbt, WrapperLookup registries) {
    super.readNbt(nbt, registries);
    if (nbt.contains(NBT_POS_0)) {
      pos0 = BlockPos.fromLong(nbt.getLong(NBT_POS_0));
    } else {
      pos0 = null;
    }
  }

  // #endregion Serialization
}
