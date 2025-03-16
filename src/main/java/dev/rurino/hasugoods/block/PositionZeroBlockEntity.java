package dev.rurino.hasugoods.block;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.network.PlayAnimPayload;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.Timer;
import dev.rurino.hasugoods.util.CharaUtils.HasuUnit;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PositionZeroBlockEntity extends AbstractNesoBaseBlockEntity {
  private static final int CHECK_STRUCTURE_INTERVAL = 8;
  private static final float LINK_PARTICLE_VELOCITY = 0.1f;
  private static final String NBT_NESOBASES = "nesobases";

  private boolean linkedNesoBases = false;

  public PositionZeroBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.POSITION_ZERO_BLOCK_ENTITY_TYPE, pos, state);
  }

  @Override
  protected ParticleState getParticleState() {
    if (getItemStack().isEmpty())
      return ParticleState.RANDOM;
    return ParticleState.WAVE;
  }

  // #region Server Animation

  private static int MERGE_ANIM_DURATION = (int) STATE_MACHINE.getAnimation(ANIM_STATE_MERGE_0).duration() + 10;

  private Collection<ServerPlayerEntity> playersMergeAnim = null;
  private Timer mergeAnimTimer = null;

  private boolean isPlayingMergeAnim() {
    return mergeAnimTimer != null;
  }

  private void serverPlayMergeAnim(ServerWorld world, NesoBaseBlockEntity[] nesobases) {
    if (isPlayingMergeAnim())
      return;

    Hasugoods.LOGGER.info("Position zero {}: Playing merge animation", pos);
    PlayAnimPayload payload = new PlayAnimPayload();
    for (int i = 0; i < nesobases.length; i++) {
      nesobases[i].lockItemStack();
      payload.add(nesobases[i].getPos(), i, 10);
    }
    lockItemStack();
    payload.add(getPos(), ANIM_STATE_MERGE_0, 10);
    playersMergeAnim = PlayerLookup.tracking(world, pos);
    playersMergeAnim.forEach(p -> ServerPlayNetworking.send(p, payload));
    mergeAnimTimer = new Timer(MERGE_ANIM_DURATION + 10, () -> {
      serverStopMergeAnim();
    });
  }

  private void serverStopMergeAnim() {
    if (!isPlayingMergeAnim()) {
      Hasugoods.LOGGER.warn("Position zero {}: Cannot stop merge animation without playing first", pos);
      return;
    }
    mergeAnimTimer = null;
    var nesobases = getPossiblyLinkedNesoBases();
    Hasugoods.LOGGER.info("Position zero {}: Stopping merge animation, total {} bases", pos, nesobases.length);

    // Construct a stop packet
    PlayAnimPayload payload = new PlayAnimPayload();
    Arrays.stream(nesobases)
        .filter(b -> pos.equals(b.getPos0()))
        .forEach(b -> {
          b.unlockItemStack();
          payload.add(b.getPos(), ANIM_STATE_IDLE, 10);
        });
    unlockItemStack();
    payload.add(getPos(), ANIM_STATE_IDLE, 10);
    playersMergeAnim.forEach(p -> ServerPlayNetworking.send(p, payload));
    playersMergeAnim = null;
  }
  // #endregion Server Animation

  // #region Multi-block structure

  private enum NesoBaseState {
    NONE,
    ERROR,
    OK
  }

  private void unlinkNesoBases(NesoBaseBlockEntity[] nesobases) {
    if (!hasLinkedNesoBases())
      return;
    Hasugoods.LOGGER.info("Position zero {}: Unlinked nesobases", pos);
    if (isPlayingMergeAnim()) {
      serverStopMergeAnim();
    }
    for (NesoBaseBlockEntity e : nesobases) {
      if (pos.equals(e.getPos0())) {
        e.setPos0(null);
      } else {
        Hasugoods.LOGGER.warn("Position zero {}: Cannot unlink nesobase {} with invalid pos0 {}", pos, e.getPos(),
            e.getPos0());
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

  private NesoBaseBlockEntity[] getPossiblyLinkedNesoBases() {
    return NESO_BASE_OFFSETS.stream()
        .map(pos::add)
        .map(world::getBlockEntity)
        .filter(e -> e instanceof NesoBaseBlockEntity)
        .map(e -> (NesoBaseBlockEntity) e)
        .filter(e -> e.getPos0() == null || e.getPos0().equals(pos))
        .toArray(NesoBaseBlockEntity[]::new);
  }

  private NesoBaseBlockEntity[] getLinkedNesoBases() {
    var ret = Arrays.stream(getPossiblyLinkedNesoBases())
        .filter(e -> pos.equals(e.getPos0()))
        .toArray(NesoBaseBlockEntity[]::new);
    if (ret.length != NESO_BASE_OFFSETS.size()) {
      return new NesoBaseBlockEntity[0];
    }
    return ret;
  }

  private static <T> boolean[] checkIsolatedComponents(T[] groupByIdx, Predicate<T> isEmpty) {
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

  private NesoBaseState[] checkNesoBaseState(NesoBaseBlockEntity[] nesobases) {
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
    nodes[n] = centerNesoItem.getCharaKey();
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
      String charaKey = nesoItem.getCharaKey();
      nodes[i] = charaKey;
      if (appeared.containsKey(charaKey)) {
        ret[i] = NesoBaseState.ERROR;
        ret[appeared.get(charaKey)] = NesoBaseState.ERROR;
      } else {
        appeared.put(charaKey, i);
      }
      // Must be medium size
      if (nesoItem.getNesoSize() != NesoSize.MEDIUM) {
        ret[i] = NesoBaseState.ERROR;
      }
    }
    // All units must be adjacent
    HasuUnit[] units = Arrays.stream(nodes)
        .map(CharaUtils::getUnit)
        .toArray(HasuUnit[]::new);
    boolean[] isolated = checkIsolatedComponents(units, u -> u == HasuUnit.NONE);
    for (int i = 0; i < n; i++) {
      if (isolated[i]) {
        ret[i] = NesoBaseState.ERROR;
      }
    }
    // All grades must be adjacent
    Integer[] grades = Arrays.stream(nodes)
        .map(CharaUtils::getGrade)
        .toArray(Integer[]::new);
    isolated = checkIsolatedComponents(grades, g -> g < 0);
    for (int i = 0; i < n; i++) {
      if (isolated[i]) {
        ret[i] = NesoBaseState.ERROR;
      }
    }
    return ret;
  }

  private void syncStructure(ServerWorld world) {
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

    boolean shouldMerge = Arrays.stream(checkNesoBaseState(nesobases)).allMatch(s -> s == NesoBaseState.OK);
    if (isPlayingMergeAnim()) {
      if (!shouldMerge) {
        serverStopMergeAnim();
      }
    } else if (shouldMerge) {
      serverPlayMergeAnim(world, nesobases);
    }
  }

  // #endregion Multi-block structure

  protected static void createLineParticle(ParticleEffect effect, World world, Vec3d from, Vec3d to) {
    Vec3d velocity = to.subtract(from).normalize().multiply(LINK_PARTICLE_VELOCITY);
    createParticle(effect, world, from, velocity);
  }

  protected void maybeCreateLinkParticles(World world, BlockPos blockPos, BlockState blockState) {
    if (!this.hasLinkedNesoBases())
      return;
    NesoBaseBlockEntity[] nesobases = getLinkedNesoBases();
    NesoBaseState[] states = checkNesoBaseState(nesobases);
    Vec3d pos = getAnimatedEntityPos();
    for (int i = 0; i < nesobases.length; i++) {
      if (states[i] == NesoBaseState.NONE)
        continue;
      ParticleEffect effect = states[i] == NesoBaseState.OK ? nesobases[i].noteParticleEffect
          : QUESTION_MARK_PARTICLE_EFFECT;
      createLineParticle(effect, world, nesobases[i].getAnimatedEntityPos(), pos);
    }
  }

  @Override
  protected void tick(World world, BlockPos blockPos, BlockState blockState) {
    super.tick(world, blockPos, blockState);
    if (!world.isClient) {
      if (isPlayingMergeAnim()) {
        mergeAnimTimer.tick();
      }
      if (curTick % CHECK_STRUCTURE_INTERVAL == 0) {
        syncStructure((ServerWorld) world);
      }
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
