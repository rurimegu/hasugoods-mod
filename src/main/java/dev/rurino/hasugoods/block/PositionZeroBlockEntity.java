package dev.rurino.hasugoods.block;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.OshiUtils;
import dev.rurino.hasugoods.util.OshiUtils.HasuUnit;
import dev.rurino.hasugoods.util.OshiUtils.NesoSize;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PositionZeroBlockEntity extends AbstractNesoBaseBlockEntity {
  protected static final int CHECK_STRUCTURE_INTERVAL = 8;
  protected static final float LINK_PARTICLE_VELOCITY = 0.1f;
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

  protected enum NesoBaseState {
    NONE,
    ERROR,
    OK
  }

  protected void unlinkNesoBases(NesoBaseBlockEntity[] nesobases) {
    Hasugoods.LOGGER.info("Position zero {}: Unlinked nesobases", pos);
    for (NesoBaseBlockEntity e : nesobases) {
      if (pos.equals(e.getPos0())) {
        e.setPos0(null);
      }
    }
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

  protected NesoBaseBlockEntity[] getPossiblyLinkedNesoBases() {
    return NESO_BASE_OFFSETS.stream()
        .map(pos::add)
        .map(world::getBlockEntity)
        .filter(e -> e instanceof NesoBaseBlockEntity)
        .map(e -> (NesoBaseBlockEntity) e)
        .filter(e -> e.getPos0() == null || e.getPos0().equals(pos))
        .toArray(NesoBaseBlockEntity[]::new);
  }

  protected NesoBaseBlockEntity[] getLinkedNesoBases() {
    var ret = Arrays.stream(getPossiblyLinkedNesoBases())
        .filter(e -> pos.equals(e.getPos0()))
        .toArray(NesoBaseBlockEntity[]::new);
    if (ret.length != NESO_BASE_OFFSETS.size()) {
      return new NesoBaseBlockEntity[0];
    }
    return ret;
  }

  protected static <T> boolean[] checkIsolatedComponents(T[] groupByIdx, Predicate<T> isEmpty) {
    int n = groupByIdx.length;
    boolean[] isolated = new boolean[n];

    int[] componentNo = new int[n];
    int nextComponentNo = 1;
    componentNo[n - 1] = nextComponentNo++;
    for (int i = 0; i < n - 1; i++) {
      if (isEmpty.test(groupByIdx[i]))
        continue;
      if (groupByIdx[i].equals(groupByIdx[n - 1])) {
        componentNo[i] = componentNo[n - 1];
      } else if (componentNo[i] == 0) {
        componentNo[i] = nextComponentNo++;
      }
      if (i == 0 && groupByIdx[i].equals(groupByIdx[n - 2])) {
        componentNo[n - 2] = componentNo[i];
      }
      if (i != n - 2 && groupByIdx[i].equals(groupByIdx[i + 1])) {
        componentNo[i + 1] = componentNo[i];
      }
    }
    for (int i = 0; i < n - 1; i++) {
      if (isEmpty.test(groupByIdx[i]))
        continue;
      for (int j = i + 1; j < n; j++) {
        if (isEmpty.test(groupByIdx[j]))
          continue;
        if (groupByIdx[i].equals(groupByIdx[j]) && componentNo[i] != componentNo[j]) {
          isolated[i] = true;
          isolated[j] = true;
        }
      }
    }

    return isolated;
  }

  protected NesoBaseState[] checkNesoBaseState(NesoBaseBlockEntity[] nesobases) {
    int n = nesobases.length;
    NesoBaseState[] ret = new NesoBaseState[n];
    // Center item
    ItemStack centerItemStack = getItemStack();
    if (centerItemStack.isEmpty()) {
      Arrays.fill(ret, NesoBaseState.NONE);
      return ret;
    }
    if (!(centerItemStack.getItem() instanceof NesoItem centerNesoItem)) {
      Arrays.fill(ret, NesoBaseState.NONE);
      return ret;
    }
    if (centerNesoItem.getNesoSize() != NesoSize.MEDIUM) {
      Arrays.fill(ret, NesoBaseState.NONE);
      return ret;
    }

    Arrays.fill(ret, NesoBaseState.OK);
    // No duplicates
    Map<String, Integer> appeared = new HashMap<>();
    String[] nodes = new String[n + 1];
    nodes[n] = centerNesoItem.getOshiKey();
    for (int i = 0; i < n; i++) {
      ItemStack itemStack = nesobases[i].getItemStack();
      if (itemStack.isEmpty()) {
        ret[i] = NesoBaseState.NONE;
        continue;
      }
      if (!(itemStack.getItem() instanceof NesoItem nesoItem)) {
        ret[i] = NesoBaseState.NONE;
        continue;
      }
      String oshiKey = nesoItem.getOshiKey();
      nodes[i] = oshiKey;
      if (appeared.containsKey(oshiKey)) {
        ret[i] = NesoBaseState.ERROR;
        ret[appeared.get(oshiKey)] = NesoBaseState.ERROR;
      } else {
        appeared.put(oshiKey, i);
      }
      // Must be medium size
      if (nesoItem.getNesoSize() != NesoSize.MEDIUM) {
        ret[i] = NesoBaseState.ERROR;
      }
    }
    // All units must be adjacent
    HasuUnit[] units = Arrays.stream(nodes)
        .map(OshiUtils::getUnit)
        .toArray(HasuUnit[]::new);
    boolean[] isolated = checkIsolatedComponents(units, u -> u == HasuUnit.NONE);
    for (int i = 0; i < n; i++) {
      if (isolated[i]) {
        ret[i] = NesoBaseState.ERROR;
      }
    }
    // All grades must be adjacent
    Integer[] grades = Arrays.stream(nodes)
        .map(OshiUtils::getGrade)
        .toArray(Integer[]::new);
    isolated = checkIsolatedComponents(grades, g -> g < 0);
    for (int i = 0; i < n; i++) {
      if (isolated[i]) {
        ret[i] = NesoBaseState.ERROR;
      }
    }
    return ret;
  }

  protected void syncStructure() {
    NesoBaseBlockEntity[] nesobases = getPossiblyLinkedNesoBases();
    if (nesobases.length != NESO_BASE_OFFSETS.size()) {
      unlinkNesoBases(nesobases);
      return;
    }
    for (NesoBaseBlockEntity e : nesobases) {
      if (e.getPos0() == null) {
        e.setPos0(pos);
      }
    }
    if (!this.linkedNesoBases) {
      Hasugoods.LOGGER.info("Position zero {}: Linked nesobases", pos);
      this.linkedNesoBases = true;
      markDirty();
      sync();
    }
  }

  // #endregion Multi-block structure

  protected static void createLineParticle(ParticleEffect effect, World world, BlockPos from, BlockPos to) {
    Vec3d fromVec = new Vec3d(from.getX() + 0.5, from.getY() + 1.5, from.getZ() + 0.5);
    Vec3d toVec = new Vec3d(to.getX() + 0.5, to.getY() + 1.5, to.getZ() + 0.5);
    Vec3d velocity = toVec.subtract(fromVec).normalize().multiply(LINK_PARTICLE_VELOCITY);
    createParticle(effect, world, fromVec, velocity);
  }

  protected void maybeCreateLinkParticles(World world, BlockPos blockPos, BlockState blockState) {
    if (!this.hasLinkedNesoBases())
      return;
    NesoBaseBlockEntity[] nesobases = getLinkedNesoBases();
    NesoBaseState[] states = checkNesoBaseState(nesobases);
    for (int i = 0; i < nesobases.length; i++) {
      if (states[i] == NesoBaseState.OK) {
        createLineParticle(nesobases[i].noteParticleEffect, world, nesobases[i].getPos(), blockPos);
      } else if (states[i] == NesoBaseState.ERROR) {
        createLineParticle(QUESTION_MARK_PARTICLE_EFFECT, world, nesobases[i].getPos(), blockPos);
      }
    }
  }

  @Override
  protected void tick(World world, BlockPos blockPos, BlockState blockState) {
    super.tick(world, blockPos, blockState);
    if (!world.isClient && curTick % CHECK_STRUCTURE_INTERVAL == 0) {
      syncStructure();
    }
  }

  @Override
  protected void clientTick(World world, BlockPos blockPos, BlockState blockState) {
    super.clientTick(world, blockPos, blockState);
    if (curTick % CHECK_STRUCTURE_INTERVAL == 0) {
      maybeCreateLinkParticles(world, blockPos, blockState);
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
    } else {
      this.linkedNesoBases = false;
    }
  }

  // #endregion Serialization

}
