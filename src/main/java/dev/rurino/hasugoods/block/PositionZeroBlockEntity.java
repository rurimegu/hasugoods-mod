package dev.rurino.hasugoods.block;

import com.google.common.collect.ImmutableList;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PositionZeroBlockEntity extends AbstractNesoBaseBlockEntity {
  protected static final int CHECK_STRUCTURE_INTERVAL = 8;
  protected static final String NBT_NESOBASES = "nesobases";
  protected static final ImmutableList<BlockPos> NESO_BASE_OFFSETS = ImmutableList.of(
      new BlockPos(3, 0, 0),
      new BlockPos(2, 0, -2),
      new BlockPos(0, 0, -3),
      new BlockPos(-2, 0, -2),
      new BlockPos(-3, 0, 0),
      new BlockPos(-2, 0, 2),
      new BlockPos(0, 0, 3),
      new BlockPos(2, 0, 2));

  protected boolean linkedNesoBases = false;

  public PositionZeroBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.POSITION_ZERO_BLOCK_ENTITY_TYPE, pos, state);
  }

  @Override
  protected ParticleState getParticleState() {
    if (getItemStack().isEmpty())
      return ParticleState.RANDOM;
    return ParticleState.WAVE;
  }

  // #region Multi-block structure

  protected void unlinkNesoBases(ImmutableList<NesoBaseBlockEntity> nesobases) {
    Hasugoods.LOGGER.info("Position zero {}: Unlinked nesobases", pos);
    nesobases.forEach(e -> {
      if (e.getPos0().equals(pos)) {
        e.setPos0(null);
      }
    });

    this.linkedNesoBases = false;
    markDirty();
    sync();
  }

  public void unlinkNesoBases() {
    if (!hasLinkedNesoBases())
      return;
    unlinkNesoBases(getPossiblyLinkedNesoBases());
  }

  public boolean hasLinkedNesoBases() {
    return linkedNesoBases;
  }

  protected ImmutableList<NesoBaseBlockEntity> getPossiblyLinkedNesoBases() {
    return NESO_BASE_OFFSETS.stream()
        .map(pos::add)
        .map(world::getBlockEntity)
        .filter(e -> e instanceof NesoBaseBlockEntity)
        .map(e -> (NesoBaseBlockEntity) e)
        .filter(e -> e.getPos0() == null || e.getPos0().equals(pos))
        .collect(ImmutableList.toImmutableList());
  }

  protected void syncStructure() {
    ImmutableList<NesoBaseBlockEntity> nesobases = getPossiblyLinkedNesoBases();
    if (nesobases.size() != NESO_BASE_OFFSETS.size()) {
      unlinkNesoBases(nesobases);
      return;
    }
    for (NesoBaseBlockEntity e : nesobases) {
      if (e.getPos0() == null) {
        e.setPos0(pos);
      }
    }
    Hasugoods.LOGGER.info("Position zero {}: Linked nesobases", pos);
    this.linkedNesoBases = true;
    markDirty();
    sync();
  }

  // #endregion Multi-block structure

  @Override
  protected void tick(World world, BlockPos blockPos, BlockState blockState) {
    super.tick(world, blockPos, blockState);
    if (!world.isClient && curTick % CHECK_STRUCTURE_INTERVAL == 0) {
      syncStructure();
    }
  }

  // #region Serialization

  @Override
  protected void writeNbt(NbtCompound nbt, WrapperLookup registries) {
    super.writeNbt(nbt, registries);
    if (hasLinkedNesoBases()) {
      nbt.putBoolean(NBT_NESOBASES, true);
    }
  }

  @Override
  protected void readNbt(NbtCompound nbt, WrapperLookup registries) {
    super.readNbt(nbt, registries);
    if (nbt.contains(NBT_NESOBASES)) {
      this.linkedNesoBases = nbt.getBoolean(NBT_NESOBASES);
    }
  }

  // #endregion Serialization

}
