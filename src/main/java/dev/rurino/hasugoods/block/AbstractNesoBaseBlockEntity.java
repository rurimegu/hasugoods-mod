package dev.rurino.hasugoods.block;

import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.CharaItem;
import dev.rurino.hasugoods.particle.NoteParticleEffect;
import dev.rurino.hasugoods.particle.QuestionMarkParticleEffect;
import dev.rurino.hasugoods.util.CollectionUtils;
import dev.rurino.hasugoods.util.Easing;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.animation.Animation;
import dev.rurino.hasugoods.util.animation.Frame;
import dev.rurino.hasugoods.util.animation.IWithStateMachine;
import dev.rurino.hasugoods.util.animation.Animation.LoopType;
import dev.rurino.hasugoods.util.animation.Interpolator;
import dev.rurino.hasugoods.util.animation.KeyFrame;
import dev.rurino.hasugoods.util.animation.StateMachine;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class AbstractNesoBaseBlockEntity extends BlockEntity implements IWithStateMachine {
  // #region Static members
  protected static final String NBT_NESO_ITEM_STACK = "nesoItemStack";
  protected static final String NBT_LOCK_ITEM_STACK = "lockItemStack";
  protected static final int PARTICLE_PER_SIDE = 4;
  protected static final int TICK_PER_PARTICLE = 2;
  protected static final int PARTICLE_PER_WAVE = PARTICLE_PER_SIDE << 2;
  protected static final Vec3d SPIRAL_VELOCITY = new Vec3d(0, 0.02, 0);
  protected static final Vec3d WAVE_VELOCITY = new Vec3d(0, 0.04, 0);
  protected static final float RANDOM_PARTICLE_PROB = 0.1f;

  protected static final Map<String, NoteParticleEffect> CHARA_KEY_TO_NOTE_PARTICLE_EFFECT = CharaUtils.CHARA_COLOR_MAP
      .entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, e -> new NoteParticleEffect(e.getValue())));
  protected static final NoteParticleEffect DEFAULT_NOTE_PARTICLE_EFFECT = new NoteParticleEffect(
      CharaUtils.DEFAULT_CHARA_COLOR);

  protected static final QuestionMarkParticleEffect QUESTION_MARK_PARTICLE_EFFECT = new QuestionMarkParticleEffect(
      0xFF0000);

  protected static enum ParticleState {
    RANDOM,
    SPIRAL,
    WAVE
  }

  protected static Vec3d getSidePos(int particleNo, BlockPos blockPos) {
    int side = particleNo / PARTICLE_PER_SIDE;
    double sideProgress = (particleNo % PARTICLE_PER_SIDE) / (double) PARTICLE_PER_SIDE;
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
    x += blockPos.getX();
    double y = blockPos.getY() + 0.9;
    z += blockPos.getZ();
    return new Vec3d(x, y, z);
  }

  public static <T extends AbstractNesoBaseBlockEntity> void tick(
      World world, BlockPos blockPos, BlockState blockState, T entity) {
    entity.tick(world, blockPos, blockState);
    if (world.isClient) {
      entity.clientTick(world, blockPos, blockState);
    }
  }

  // #endregion Static members

  private ItemStack nesoItemStack = ItemStack.EMPTY;
  protected NoteParticleEffect noteParticleEffect = DEFAULT_NOTE_PARTICLE_EFFECT;
  protected int curTick = 0;

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

  public static final int ANIM_STATE_IDLE = 217;
  public static final int ANIM_STATE_MERGE_0 = 201;
  protected static final StateMachine STATE_MACHINE = new StateMachine(ANIM_STATE_IDLE);

  static {
    // Build idle animationAnimation
    KeyFrame.Translate firstT = new KeyFrame.Translate(0, new Vec3d(0, 0.2, 0));
    var animIdle = new Animation(LoopType.PING_PONG)
        .addTranslation(firstT)
        .addTranslation(
            new KeyFrame.Translate(80, new Vec3d(0, 0.3, 0)),
            new Interpolator.Translate(Easing::easeInOutSine));
    STATE_MACHINE.set(ANIM_STATE_IDLE, animIdle);
    // Build neso base animations
    KeyFrame.Translate secondT = new KeyFrame.Translate(60, new Vec3d(0, 2, 0));
    for (int i = 0; i < NESO_BASE_OFFSETS.size(); i++) {
      Vec3d offset = new Vec3d(NESO_BASE_OFFSETS.get(i).multiply(-1));
      KeyFrame.Translate thirdT = new KeyFrame.Translate(160, secondT.value().add(offset));
      KeyFrame.Scale secondS = new KeyFrame.Scale(120, new Vec3d(1, 1, 1));
      KeyFrame.Scale thirdS = new KeyFrame.Scale(160, Vec3d.ZERO);
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
        .addScale(new KeyFrame.Scale(120, new Vec3d(1, 1, 1)))
        .addScale(new KeyFrame.Scale(180, new Vec3d(2, 2, 2))));
  }

  private final StateMachine stateMachine;

  public StateMachine getStateMachine() {
    return stateMachine;
  }

  public Vec3d getAnimatedEntityPos() {
    Frame frame = stateMachine.get();
    Vec3d translation = frame.translate();
    Vec3d scale = frame.scale();
    return getPos().toBottomCenterPos().add(new Vec3d(0, 1.0 + scale.y * 0.3, 0)).add(translation);
  }
  // #endregion Animation

  public AbstractNesoBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
    stateMachine = STATE_MACHINE.copy();
  }

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

  public void unlockItemStack() {
    itemStackLocked = false;
    markDirty();
    sync();
  }

  public ItemStack getItemStack() {
    return nesoItemStack;
  }

  protected boolean setItemStackNoSync(ItemStack itemStack) {
    if (isItemStackLocked())
      return false;
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
    return true;
  }

  public boolean setItemStack(ItemStack itemStack) {
    if (!setItemStackNoSync(itemStack)) {
      return false;
    }
    markDirty();
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

  protected int getParticleTick() {
    return curTick % (PARTICLE_PER_WAVE * TICK_PER_PARTICLE);
  }

  protected static void createParticle(ParticleEffect effect, World world, Vec3d pos, Vec3d velocity) {
    world.addParticle(effect, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
  }

  protected void createNoteParticle(World world, Vec3d pos, Vec3d velocity) {
    createParticle(noteParticleEffect, world, pos, velocity);
  }

  protected void createSpiralNoteParticles(World world, BlockPos blockPos, BlockState blockState) {
    int particleTick = getParticleTick();
    if (particleTick % TICK_PER_PARTICLE != 0)
      return;
    Vec3d pos = getSidePos(particleTick / TICK_PER_PARTICLE, blockPos);
    createNoteParticle(world, pos, SPIRAL_VELOCITY);
  }

  protected void createWaveNoteParticles(World world, BlockPos blockPos, BlockState blockState) {
    if (getParticleTick() != 0)
      return;
    for (int i = 0; i < PARTICLE_PER_WAVE; i++) {
      Vec3d pos = getSidePos(i, blockPos);
      createNoteParticle(world, pos, WAVE_VELOCITY);
    }
  }

  protected void createRandomNoteParticles(World world, BlockPos blockPos, BlockState blockState) {
    if (getParticleTick() % TICK_PER_PARTICLE != 0)
      return;
    Random random = world.getRandom();
    if (random.nextFloat() >= RANDOM_PARTICLE_PROB)
      return;
    double x = blockPos.getX() + random.nextDouble();
    double y = blockPos.getY() + 0.9;
    double z = blockPos.getZ() + random.nextDouble();
    Vec3d pos = new Vec3d(x, y, z);
    NoteParticleEffect effect = CollectionUtils.getRandomElement(
        CHARA_KEY_TO_NOTE_PARTICLE_EFFECT.values().stream().toList(),
        random);
    Vec3d velocity = new Vec3d(0, MathHelper.nextDouble(random, 0.015, 0.025), 0);
    createParticle(effect, world, pos, velocity);
  }

  abstract protected ParticleState getParticleState();

  protected void maybeCreateNoteParticles(World world, BlockPos blockPos, BlockState blockState) {
    switch (getParticleState()) {
      case SPIRAL:
        createSpiralNoteParticles(world, blockPos, blockState);
        break;
      case WAVE:
        createWaveNoteParticles(world, blockPos, blockState);
        break;
      case RANDOM:
        createRandomNoteParticles(world, blockPos, blockState);
        break;
      default:
        throw new IllegalStateException("Unexpected particle state: " + getParticleState());
    }
  }

  // #endregion Particles

  public int getCurTick() {
    return curTick;
  }

  protected void clientTick(World world, BlockPos blockPos, BlockState blockState) {
    maybeCreateNoteParticles(world, blockPos, blockState);
  }

  protected void tick(World world, BlockPos blockPos, BlockState blockState) {
    curTick++;
  }

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
