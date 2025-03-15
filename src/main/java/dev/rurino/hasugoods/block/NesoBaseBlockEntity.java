package dev.rurino.hasugoods.block;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NesoBaseBlockEntity extends AbstractNesoBaseBlockEntity {
  protected static final int CHECK_POS_0_INTERVAL = 8;
  protected static final String NBT_POS_0 = "pos0";

  protected BlockPos pos0 = null;

  public NesoBaseBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.NESO_BASE_BLOCK_ENTITY_TYPE, pos, state);
  }

  public void setPos0(BlockPos pos0) {
    this.pos0 = pos0;
    getStateMachine().transit(ANIM_STATE_IDLE, 10);
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

  protected void syncPos0() {
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
    if (!world.isClient && curTick % CHECK_POS_0_INTERVAL == 0) {
      syncPos0();
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
