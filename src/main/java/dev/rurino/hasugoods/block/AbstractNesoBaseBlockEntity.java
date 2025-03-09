package dev.rurino.hasugoods.block;

import java.util.Map;
import java.util.stream.Collectors;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.OshiItem;
import dev.rurino.hasugoods.particle.NoteParticleEffect;
import dev.rurino.hasugoods.util.CollectionUtils;
import dev.rurino.hasugoods.util.OshiUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public abstract class AbstractNesoBaseBlockEntity extends BlockEntity {
  // #region Static members
  protected static final String NESO_ITEM_STACK_KEY = "nesoItemStack";
  protected static final int TICK_PER_SIDE = 6;
  protected static final int TICK_PER_WAVE = TICK_PER_SIDE << 2;
  protected static final Vec3d SPIRAL_VELOCITY = new Vec3d(0, 0.02, 0);
  protected static final Vec3d WAVE_VELOCITY = new Vec3d(0, 0.04, 0);
  protected static final float RANDOM_PARTICLE_PROB = 0.2f;

  protected static final Map<String, NoteParticleEffect> OSHI_KEY_TO_PARTICLE_EFFECT = OshiUtils.OSHI_COLOR_MAP
      .entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, e -> new NoteParticleEffect(e.getValue())));
  protected static final NoteParticleEffect DEFAULT_NOTE_PARTICLE_EFFECT = new NoteParticleEffect(
      OshiUtils.DEFAULT_OSHI_COLOR);

  protected static enum ParticleState {
    RANDOM,
    SPIRAL,
    WAVE
  }

  protected static Vec3d getSidePos(int tick, BlockPos blockPos) {
    int side = tick / TICK_PER_SIDE;
    double sideProgress = (tick % TICK_PER_SIDE) / (double) TICK_PER_SIDE;
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
    if (world.isClient) {
      entity.clientTick(world, blockPos, blockState);
    }
  }

  // #endregion Static members

  protected ItemStack nesoItemStack = ItemStack.EMPTY;
  protected NoteParticleEffect noteParticleEffect = DEFAULT_NOTE_PARTICLE_EFFECT;
  protected int displayTick = 0;

  public AbstractNesoBaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  public ItemStack getItemStack() {
    return nesoItemStack;
  }

  public void setItemStack(ItemStack itemStack) {
    nesoItemStack = itemStack;
    if (itemStack.getItem() instanceof OshiItem oshiItem) {
      noteParticleEffect = OSHI_KEY_TO_PARTICLE_EFFECT.getOrDefault(oshiItem.getOshiKey(),
          DEFAULT_NOTE_PARTICLE_EFFECT);
    } else {
      if (!itemStack.isEmpty()) {
        Hasugoods.LOGGER.warn("Unexpected item stack inserted into NesoBaseBlock: {}", itemStack);
      }
      noteParticleEffect = DEFAULT_NOTE_PARTICLE_EFFECT;
    }
    markDirty();
    sync();
  }

  public int getItemColor() {
    if (getItemStack().isEmpty() || !(getItemStack().getItem() instanceof OshiItem oshiItem))
      return OshiUtils.DEFAULT_OSHI_COLOR;
    return OshiUtils.OSHI_COLOR_MAP.get(oshiItem.getOshiKey());
  }

  protected void createNoteParticle(NoteParticleEffect effect, World world, Vec3d pos, Vec3d velocity) {
    world.addParticle(effect, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
  }

  protected void createNoteParticle(World world, Vec3d pos, Vec3d velocity) {
    createNoteParticle(noteParticleEffect, world, pos, velocity);
  }

  protected void createSpiralNoteParticles(World world, BlockPos blockPos, BlockState blockState) {
    Vec3d pos = getSidePos(displayTick, blockPos);
    createNoteParticle(world, pos, SPIRAL_VELOCITY);
  }

  protected void createWaveNoteParticles(World world, BlockPos blockPos, BlockState blockState) {
    if (displayTick != 0)
      return;
    for (int i = 0; i < TICK_PER_WAVE; i++) {
      Vec3d pos = getSidePos(i, blockPos);
      createNoteParticle(world, pos, WAVE_VELOCITY);
    }
  }

  protected void createRandomNoteParticles(World world, BlockPos blockPos, BlockState blockState) {
    Random random = world.getRandom();
    if (random.nextFloat() >= RANDOM_PARTICLE_PROB)
      return;
    double x = blockPos.getX() + random.nextDouble();
    double y = blockPos.getY() + 0.9;
    double z = blockPos.getZ() + random.nextDouble();
    Vec3d pos = new Vec3d(x, y, z);
    NoteParticleEffect effect = CollectionUtils.getRandomElement(
        OSHI_KEY_TO_PARTICLE_EFFECT.values().stream().toList(),
        random);
    Vec3d velocity = new Vec3d(0, MathHelper.nextDouble(random, 0.015, 0.025), 0);
    createNoteParticle(effect, world, pos, velocity);
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

  protected void clientTick(World world, BlockPos blockPos, BlockState blockState) {
    displayTick++;
    displayTick %= TICK_PER_WAVE;
    maybeCreateNoteParticles(world, blockPos, blockState);
  }

  @Override
  protected void writeNbt(NbtCompound nbt, WrapperLookup registries) {
    super.writeNbt(nbt, registries);
    nbt.put(NESO_ITEM_STACK_KEY, getItemStack().toNbtAllowEmpty(registries));
  }

  @Override
  protected void readNbt(NbtCompound nbt, WrapperLookup registries) {
    super.readNbt(nbt, registries);
    setItemStack(ItemStack.fromNbtOrEmpty(registries, nbt.getCompound(NESO_ITEM_STACK_KEY)));
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

}
