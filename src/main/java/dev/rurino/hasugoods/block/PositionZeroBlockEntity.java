package dev.rurino.hasugoods.block;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.function.Predicate;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.network.FinishNesoMergePayload;
import dev.rurino.hasugoods.network.PlayAnimPayload;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.ParticleUtils;
import dev.rurino.hasugoods.util.Timer;
import dev.rurino.hasugoods.util.CharaUtils.HasuUnit;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import dev.rurino.hasugoods.util.ParticleUtils.Emitter;
import dev.rurino.hasugoods.util.animation.Animation;
import dev.rurino.hasugoods.util.animation.Interpolator;
import dev.rurino.hasugoods.util.animation.KeyFrame;
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
import net.neoforged.neoforge.common.ModConfigSpec.LongValue;

public class PositionZeroBlockEntity extends AbstractNesoBaseBlockEntity {
  private static final int CHECK_STRUCTURE_INTERVAL = 8;
  private static final float LINK_PARTICLE_VELOCITY = 0.1f;
  private static final String NBT_NESOBASES = "nesobases";
  private static final LongValue CHARGE_AMOUNT_PER_TICK = Hasugoods.CONFIG.neso.pos0ChargeAmountPerTick;

  static void initialize() {
    Hasugoods.LOGGER.debug("Position zero block entity initialized");
  }

  private boolean linkedNesoBases = false;

  public PositionZeroBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.POSITION_ZERO_BLOCK_ENTITY_TYPE, pos, state);
    getStateMachine().set(ANIM_STATE_AFTER_MERGE, ANIM_AFTER_MERGE);
  }

  @Override
  protected ParticleState getParticleState() {
    if (getItemStack().isEmpty())
      return super.getParticleState();
    return ParticleState.WAVE;
  }

  // #region Server Animation
  private static final int ANIM_STATE_MERGE_0 = 201;
  private static int ANIM_STATE_AFTER_MERGE = 147;
  private static int MERGE_ANIM_DURATION = (int) STATE_MACHINE.getAnimation(ANIM_STATE_MERGE_0).duration() + 10;
  private static Animation ANIM_AFTER_MERGE = new Animation()
      .addTranslation(new KeyFrame.Translate(0,
          STATE_MACHINE.getAnimation(ANIM_STATE_MERGE_0).getFrame(MERGE_ANIM_DURATION).translate()))
      .addTranslation(new KeyFrame.Translate(20, STATE_MACHINE.getAnimation(ANIM_STATE_IDLE).getFrame(0).translate()),
          Interpolator.Translate.EASE_OUT_CUBIC)
      .exitAt(ANIM_STATE_IDLE);

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
      serverStopMergeAnim(true);
    });
  }

  private void serverStopMergeAnim(boolean success) {
    if (!isPlayingMergeAnim()) {
      Hasugoods.LOGGER.warn("Position zero {}: Cannot stop merge animation without playing first", pos);
      return;
    }
    mergeAnimTimer = null;
    var nesobases = getPossiblyLinkedNesoBases();
    Hasugoods.LOGGER.info("Position zero {}: Stopping merge animation, total {} bases", pos, nesobases.length);
    if (success) {
      if (nesobases.length != NESO_BASE_OFFSETS.size()) {
        Hasugoods.LOGGER.warn("Position zero {}: Cannot merge nesobases with invalid count {}", pos, nesobases.length);
        success = false;
      }
      if (!Arrays.stream(checkNesoBaseState(nesobases)).allMatch(s -> s == NesoBaseState.OK)) {
        Hasugoods.LOGGER.warn("Position zero {}: Cannot merge nesobases with invalid state", pos);
        success = false;
      }
    }

    // Construct a stop packet
    var payload = new FinishNesoMergePayload(success);
    for (var b : nesobases) {
      if (!pos.equals(b.getPos0()))
        continue;
      b.unlockItemStackNoSync();
      if (success) {
        b.setItemStackNoSync(ItemStack.EMPTY);
      }
      payload.add(b.getPos(), ANIM_STATE_IDLE, 10);
    }
    unlockItemStackNoSync();
    if (success) {
      setItemStackNoSync(getUpgradedNeso());
      payload.add(getPos(), ANIM_STATE_AFTER_MERGE, 0);
    } else {
      payload.add(getPos(), ANIM_STATE_IDLE, 10);
    }
    playersMergeAnim.forEach(p -> ServerPlayNetworking.send(p, payload));
    playersMergeAnim = null;
  }

  public ItemStack getUpgradedNeso() {
    ItemStack stack = getItemStack();
    return getNesoItem().map(
        nesoItem -> {
          if (nesoItem.getNesoSize() != NesoSize.MEDIUM) {
            Hasugoods.LOGGER.warn("Position zero {}: Cannot upgrade neso with invalid item {}", pos, getItemStack());
            return stack;
          }
          NesoItem upgradedNesoItem = NesoItem.getNesoItem(nesoItem.getCharaKey(), NesoSize.LARGE).get();
          ItemStack newStack = stack.copyComponentsToNewStack(upgradedNesoItem, 1);
          upgradedNesoItem.setFullEnergy(newStack);
          return newStack;
        })
        .orElse(stack);
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
      serverStopMergeAnim(false);
    }
    for (NesoBaseBlockEntity e : nesobases) {
      if (pos.equals(e.getPos0())) {
        e.resetPos0();
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

  private static boolean[] checkConsecutiveValues(Integer[] valueByIdx, Predicate<Integer> isEmpty) {
    HashSet<Integer> appeared = Arrays.stream(valueByIdx)
        .filter(v -> !isEmpty.test(v))
        .collect(HashSet::new, HashSet::add, HashSet::addAll);
    int center = valueByIdx[valueByIdx.length - 1];
    for (int i = center + 1; appeared.contains(i); i++) {
      appeared.remove(i);
    }
    for (int i = center; appeared.contains(i); i--) {
      appeared.remove(i);
    }
    boolean[] ret = new boolean[valueByIdx.length];
    for (int i = 0; i < valueByIdx.length; i++) {
      if (!isEmpty.test(valueByIdx[i]) && appeared.contains(valueByIdx[i])) {
        ret[i] = true;
      }
    }
    return ret;
  }

  private NesoBaseState[] checkNesoBaseState(NesoBaseBlockEntity[] nesobases) {
    int n = nesobases.length;
    NesoBaseState[] ret = new NesoBaseState[n];
    // Center item
    var centerNesoItemOptional = getNesoItem();
    if (centerNesoItemOptional.isEmpty() || centerNesoItemOptional.get().getNesoSize() != NesoSize.MEDIUM
        || !isTopAir()) {
      Arrays.fill(ret, NesoBaseState.NONE);
      return ret;
    }

    Arrays.fill(ret, NesoBaseState.OK);
    // No duplicates
    Map<String, Integer> appeared = new HashMap<>();
    String[] nodes = new String[n + 1];
    nodes[n] = centerNesoItemOptional.get().getCharaKey();
    for (int i = 0; i < n; i++) {
      var nesobase = nesobases[i];
      var nesoItemOptional = nesobase.getNesoItem();
      if (nesoItemOptional.isEmpty() || !nesobase.isTopAir()) {
        ret[i] = NesoBaseState.NONE;
        continue;
      }
      String charaKey = nesoItemOptional.get().getCharaKey();
      nodes[i] = charaKey;
      if (appeared.containsKey(charaKey)) {
        ret[i] = NesoBaseState.ERROR;
        ret[appeared.get(charaKey)] = NesoBaseState.ERROR;
      } else {
        appeared.put(charaKey, i);
      }
      // Must be medium size
      if (nesoItemOptional.get().getNesoSize() != NesoSize.MEDIUM) {
        ret[i] = NesoBaseState.ERROR;
      }
    }
    // All units must be adjacent
    HasuUnit[] units = Arrays.stream(nodes)
        .map(CharaUtils::getUnit)
        .toArray(HasuUnit[]::new);
    boolean[] failed = checkIsolatedComponents(units, u -> u == HasuUnit.NONE);
    for (int i = 0; i < n; i++) {
      if (failed[i]) {
        ret[i] = NesoBaseState.ERROR;
      }
    }
    // All grades must be adjacent
    Integer[] grades = Arrays.stream(nodes)
        .map(CharaUtils::getGrade)
        .toArray(Integer[]::new);
    failed = checkIsolatedComponents(grades, g -> g < 0);
    for (int i = 0; i < n; i++) {
      if (failed[i]) {
        ret[i] = NesoBaseState.ERROR;
      }
    }
    // Grade values must be consecutive
    failed = checkConsecutiveValues(grades, g -> g < 0);
    for (int i = 0; i < n; i++) {
      if (failed[i]) {
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
        e.setPos0(world, this);
      }
    }

    if (!this.linkedNesoBases) {
      Hasugoods.LOGGER.info("Position zero {}: Linked nesobases", pos);
      this.linkedNesoBases = true;
      markDirty();
    }

    boolean shouldMerge = Arrays.stream(checkNesoBaseState(nesobases)).allMatch(s -> s == NesoBaseState.OK);
    if (isPlayingMergeAnim()) {
      if (!shouldMerge) {
        serverStopMergeAnim(false);
      }
    } else if (shouldMerge) {
      serverPlayMergeAnim(world, nesobases);
    }
  }

  // #endregion Multi-block structure

  // #region Particles

  private final Emitter.Timed lineParticleEmitter = (new Emitter() {
    @Override
    protected void clientEmit(World world, Vec3d pos) {
      maybeCreateLinkParticles(world);
    }
  }).repeat(CHECK_STRUCTURE_INTERVAL);

  private static void createLineParticle(ParticleEffect effect, World world, Vec3d from, Vec3d to) {
    Vec3d velocity = to.subtract(from).normalize().multiply(LINK_PARTICLE_VELOCITY);
    ParticleUtils.createParticle(effect, world, from, velocity);
  }

  private void maybeCreateLinkParticles(World world) {
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

  // #endregion Particles

  // #region Ticking
  private int curTick = 0;

  @Override
  protected long chargeAmountPerTick(World world, BlockPos blockPos, BlockState blockState) {
    return CHARGE_AMOUNT_PER_TICK.get();
  }

  @Override
  protected void tick(World world, BlockPos blockPos, BlockState blockState) {
    super.tick(world, blockPos, blockState);
    if (!world.isClient) {
      if (isPlayingMergeAnim()) {
        mergeAnimTimer.tick();
      }
      if (++curTick % CHECK_STRUCTURE_INTERVAL == 0) {
        syncStructure((ServerWorld) world);
      }
    }
  }

  @Override
  protected void clientTick(World world, BlockPos blockPos, BlockState blockState) {
    super.clientTick(world, blockPos, blockState);
    lineParticleEmitter.tick(world, blockPos);
  }

  // #endregion Ticking

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
