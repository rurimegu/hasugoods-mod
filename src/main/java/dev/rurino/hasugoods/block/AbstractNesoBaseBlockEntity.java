package dev.rurino.hasugoods.block;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.entity.NesoEntity;
import dev.rurino.hasugoods.item.CharaItem;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.particle.HasuParticleEffect;
import dev.rurino.hasugoods.util.Easing;
import dev.rurino.hasugoods.util.ParticleUtils.Emitter;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.animation.Animation;
import dev.rurino.hasugoods.util.animation.Frame;
import dev.rurino.hasugoods.util.animation.IWithStateMachine;
import dev.rurino.hasugoods.util.animation.Animation.LoopType;
import dev.rurino.hasugoods.util.animation.Interpolator;
import dev.rurino.hasugoods.util.animation.KeyFrame;
import dev.rurino.hasugoods.util.animation.StateMachine;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

public abstract class AbstractNesoBaseBlockEntity extends BlockEntity implements IWithStateMachine {
  // #region Static members
  protected static final String NBT_NESO_ITEM_STACK = "nesoItemStack";
  protected static final String NBT_LOCK_ITEM_STACK = "lockItemStack";

  protected static final Map<String, HasuParticleEffect> CHARA_KEY_TO_NOTE_PARTICLE_EFFECT = CharaUtils.CHARA_COLOR_MAP
      .entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, entry -> HasuParticleEffect.note(entry.getValue())));
  protected static final HasuParticleEffect DEFAULT_NOTE_PARTICLE_EFFECT = HasuParticleEffect.note(
      CharaUtils.DEFAULT_CHARA_COLOR);

  protected static final HasuParticleEffect QUESTION_MARK_PARTICLE_EFFECT = HasuParticleEffect.questionMark(
      0xFF0000);

  protected static enum ParticleState {
    NONE,
    RANDOM,
    SPIRAL,
    WAVE
  }

  public static <T extends AbstractNesoBaseBlockEntity> void tick(
      World world, BlockPos blockPos, BlockState blockState, T entity) {
    entity.tick(world, blockPos, blockState);
    if (world.isClient) {
      entity.clientTick(world, blockPos, blockState);
    }
    entity.maybeSync();
  }

  // #endregion Static members

  private ItemStack nesoItemStack = ItemStack.EMPTY;

  // #region Animation
  protected static final ImmutableList<BlockPos> NESO_BASE_OFFSETS = ImmutableList.of(
      new BlockPos(3, 0, 0),
      new BlockPos(2, 0, -2),
      new BlockPos(0, 0, -3),
      new BlockPos(-2, 0, -2),
      new BlockPos(-3, 0, 0),
      new BlockPos(-2, 0, 2),
      new BlockPos(0, 0, 3),
      new BlockPos(2, 0, 2));

  protected static final int ANIM_STATE_IDLE = 217;
  protected static final int ANIM_STATE_MERGE_0 = 201;
  protected static final StateMachine STATE_MACHINE = new StateMachine(ANIM_STATE_IDLE);

  static {
    // Build idle animationAnimation
    KeyFrame.Translate firstT = new KeyFrame.Translate(0, new Vec3d(0, 0.2, 0));
    var animIdle = new Animation(LoopType.PING_PONG)
        .addTranslation(firstT)
        .addTranslation(
            new KeyFrame.Translate(40, new Vec3d(0, 0.3, 0)),
            new Interpolator.Translate(Easing::easeInOutSine));
    STATE_MACHINE.set(ANIM_STATE_IDLE, animIdle);
    // Build neso base animations
    KeyFrame.Translate secondT = new KeyFrame.Translate(40, new Vec3d(0, 2, 0));
    for (int i = 0; i < NESO_BASE_OFFSETS.size(); i++) {
      Vec3d offset = new Vec3d(NESO_BASE_OFFSETS.get(i).multiply(-1));
      KeyFrame.Translate thirdT = new KeyFrame.Translate(80, secondT.value().add(offset.multiply(0.9)));
      KeyFrame.Scale secondS = new KeyFrame.Scale(60, new Vec3d(1, 1, 1));
      KeyFrame.Scale thirdS = new KeyFrame.Scale(80, Vec3d.ZERO);
      Animation anim = new Animation()
          .addTranslation(firstT)
          .addTranslation(secondT, Interpolator.Translate.EASE_OUT_CUBIC)
          .addTranslation(thirdT, Interpolator.Translate.EASE_OUT_CUBIC)
          .addScale(secondS)
          .addScale(thirdS);
      STATE_MACHINE.set(i, anim);
    }
    // Build merge animation for position 0
    STATE_MACHINE.set(ANIM_STATE_MERGE_0, new Animation()
        .addTranslation(firstT)
        .addTranslation(secondT, Interpolator.Translate.EASE_OUT_CUBIC)
        .addScale(new KeyFrame.Scale(60, new Vec3d(1, 1, 1)))
        .addScale(new KeyFrame.Scale(80, new Vec3d(2, 2, 2)), Interpolator.Scale.OUT_BOUNCE));
  }

  private final StateMachine stateMachine;

  public StateMachine getStateMachine() {
    return stateMachine;
  }

  public Vec3d getAnimatedEntityPos() {
    Frame frame = stateMachine.get();
    Vec3d translation = frame.translate();
    Vec3d scale = frame.scale();
    Vec3d ret = getPos().toBottomCenterPos().add(0, 1.0, 0).add(translation);
    if (this.getItemStack().isEmpty() || !(this.getItemStack().getItem() instanceof NesoItem nesoItem))
      return ret;
    var entityTypeOptional = NesoEntity.getNesoEntityType(nesoItem.getCharaKey(), nesoItem.getNesoSize());
    if (!entityTypeOptional.isPresent()) {
      Hasugoods.LOGGER.warn("Neso entity type not found for chara key: {}, size {}", nesoItem.getCharaKey(),
          nesoItem.getNesoSize());
      return ret;
    }
    float eyeHeight = entityTypeOptional.get().getDimensions().eyeHeight();
    return ret.add(0, scale.y * eyeHeight, 0);
  }
  // #endregion Animation

  // #region Constructor

  public AbstractNesoBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    stateMachine = STATE_MACHINE.copy();
  }

  // #endregion Constructor

  // #region Item stack

  private boolean itemStackLocked = false;

  public boolean isItemStackLocked() {
    return itemStackLocked;
  }

  public void lockItemStack() {
    itemStackLocked = true;
    markDirty();
    sync();
  }

  protected void unlockItemStackNoSync() {
    itemStackLocked = false;
    markDirty();
  }

  public void unlockItemStack() {
    unlockItemStackNoSync();
    sync();
  }

  public ItemStack getItemStack() {
    return nesoItemStack;
  }

  protected boolean setItemStackNoSync(ItemStack itemStack) {
    if (isItemStackLocked())
      return false;
    if (itemStack == null) {
      Hasugoods.LOGGER.warn("Unexpected null item stack inserted into NesoBaseBlock at {}", pos);
      itemStack = ItemStack.EMPTY;
    }
    nesoItemStack = itemStack;
    if (itemStack.getItem() instanceof CharaItem charaItem) {
      noteParticleEffect = CHARA_KEY_TO_NOTE_PARTICLE_EFFECT.getOrDefault(charaItem.getCharaKey(),
          DEFAULT_NOTE_PARTICLE_EFFECT);
    } else {
      if (!itemStack.isEmpty()) {
        Hasugoods.LOGGER.warn("Unexpected item stack inserted into NesoBaseBlock: {}", itemStack);
      }
      noteParticleEffect = DEFAULT_NOTE_PARTICLE_EFFECT;
    }
    markDirty();
    return true;
  }

  public boolean setItemStack(ItemStack itemStack) {
    if (!setItemStackNoSync(itemStack)) {
      return false;
    }
    sync();
    return true;
  }

  public int getItemColor() {
    if (getItemStack().isEmpty() || !(getItemStack().getItem() instanceof CharaItem charaItem))
      return CharaUtils.DEFAULT_CHARA_COLOR;
    return CharaUtils.CHARA_COLOR_MAP.get(charaItem.getCharaKey());
  }

  // #endregion Item stack

  public boolean isTopAir() {
    return world.getBlockState(pos.up()).isAir();
  }

  // #region Particles
  protected static final int PARTICLE_PER_SIDE = 4;
  protected static final int TICK_PER_PARTICLE = 2;
  protected static final int PARTICLE_PER_WAVE = PARTICLE_PER_SIDE << 2;
  protected static final Vec3d SPIRAL_VELOCITY = new Vec3d(0, 0.02, 0);
  protected static final Vec3d WAVE_VELOCITY = new Vec3d(0, 0.04, 0);
  protected static final float RANDOM_PARTICLE_PROB = 0.1f;

  protected HasuParticleEffect noteParticleEffect = DEFAULT_NOTE_PARTICLE_EFFECT;

  private final Function<Random, ParticleEffect> noteParticleEffectSupplier = (random) -> noteParticleEffect;
  private final Emitter.Timed waveEmitter = new Emitter.Wave(noteParticleEffectSupplier, PARTICLE_PER_SIDE,
      WAVE_VELOCITY).repeat(PARTICLE_PER_WAVE * TICK_PER_PARTICLE);
  private final Emitter.Timed randomEmitter = new Emitter.RandomUp(
      CHARA_KEY_TO_NOTE_PARTICLE_EFFECT.values(),
      RANDOM_PARTICLE_PROB)
      .repeat(TICK_PER_PARTICLE);
  private final Emitter.Timed spiralEmitter = new Emitter.Spiral(noteParticleEffectSupplier,
      PARTICLE_PER_SIDE, SPIRAL_VELOCITY).repeat(TICK_PER_PARTICLE);

  protected Emitter.Timed getParticleEmitter() {
    return switch (getParticleState()) {
      case SPIRAL -> spiralEmitter;
      case WAVE -> waveEmitter;
      case RANDOM -> randomEmitter;
      default -> null;
    };
  }

  protected ParticleState getParticleState() {
    return isTopAir() ? ParticleState.RANDOM : ParticleState.NONE;
  }

  // #endregion Particles

  // #region Ticking

  private boolean shouldSync = false;
  private BlockApiCache<EnergyStorage, Direction> energyStorageAbove = null;

  @Override
  public void markDirty() {
    super.markDirty();
    shouldSync = true;
  }

  protected void maybeSync() {
    if (shouldSync) {
      sync();
      shouldSync = false;
    }
  }

  protected void clientTick(World world, BlockPos blockPos, BlockState blockState) {
    stateMachine.tick();
    var emitter = getParticleEmitter();
    if (emitter != null)
      emitter.tick(world, blockPos);
  }

  protected long chargeAmountPerTick(World world, BlockPos blockPos, BlockState blockState) {
    return 0;
  }

  protected void tick(World world, BlockPos blockPos, BlockState blockState) {
    if (!world.isClient) {
      if (energyStorageAbove == null) {
        energyStorageAbove = BlockApiCache.create(
            EnergyStorage.SIDED, (ServerWorld) world, pos.offset(Direction.UP));
      }
      if (isTopAir()) {
        if (getItemStack().getItem() instanceof NesoItem item) {
          item.chargeEnergy(getItemStack(), chargeAmountPerTick(world, blockPos, blockState));
          markDirty();
        }
      } else {
        EnergyStorage storage = energyStorageAbove.find(Direction.DOWN);
        if (storage != null && storage.supportsInsertion()) {
          try (Transaction transaction = Transaction.openOuter()) {
            storage.insert(
                chargeAmountPerTick(world, blockPos, blockState),
                transaction);
            transaction.commit();
          }
        }
      }
    }
  }

  // #endregion Ticking

  // #region Serialization

  @Override
  protected void writeNbt(NbtCompound nbt, WrapperLookup registries) {
    super.writeNbt(nbt, registries);
    nbt.put(NBT_NESO_ITEM_STACK, getItemStack().toNbtAllowEmpty(registries));
    nbt.putBoolean(NBT_LOCK_ITEM_STACK, isItemStackLocked());
  }

  @Override
  protected void readNbt(NbtCompound nbt, WrapperLookup registries) {
    super.readNbt(nbt, registries);
    itemStackLocked = false;
    setItemStackNoSync(ItemStack.fromNbtOrEmpty(registries, nbt.getCompound(NBT_NESO_ITEM_STACK)));
    if (nbt.contains(NBT_LOCK_ITEM_STACK) && nbt.getBoolean(NBT_LOCK_ITEM_STACK)) {
      itemStackLocked = true;
    }
  }

  @Override
  public Packet<ClientPlayPacketListener> toUpdatePacket() {
    return BlockEntityUpdateS2CPacket.create(this);
  }

  @Override
  public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
    return createNbt(registryLookup);
  }

  protected void sync() {
    if (world != null) {
      BlockState blockState = world.getBlockState(pos);
      world.updateListeners(pos, blockState, blockState, 0);
    }
  }

  // #endregion Serialization
}
