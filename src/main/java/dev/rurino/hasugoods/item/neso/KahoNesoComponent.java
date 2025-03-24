package dev.rurino.hasugoods.item.neso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.rurino.hasugoods.particle.HasuParticleEffect;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CollectionUtils;
import dev.rurino.hasugoods.util.ParticleUtils.Emitter;
import dev.rurino.hasugoods.util.config.HcVal;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.TallFlowerBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class KahoNesoComponent {
  // #region Static fields
  private static final HcVal.Int MAX_SPREAD_DELTA_Y = KahoNesoItem.HC_KAHO
      .getInt("maxSpreadDeltaY", 3)
      .nonnegative()
      .max(128);
  private static final HcVal.Int NUM_PARTICLES = KahoNesoItem.HC_KAHO
      .getInt("numParticles", 8)
      .nonnegative()
      .max(128);

  private static final int[] SPREAD_DX = { -1, 0, 1, 0 };
  private static final int[] SPREAD_DZ = { 0, -1, 0, 1 };
  private static final List<Block> FLOWER_BLOCKS = List.of(
      Blocks.DANDELION,
      Blocks.POPPY,
      Blocks.BLUE_ORCHID,
      Blocks.ALLIUM,
      Blocks.AZURE_BLUET,
      Blocks.RED_TULIP,
      Blocks.ORANGE_TULIP,
      Blocks.WHITE_TULIP,
      Blocks.PINK_TULIP,
      Blocks.OXEYE_DAISY,
      Blocks.CORNFLOWER,
      Blocks.LILY_OF_THE_VALLEY,
      Blocks.TORCHFLOWER,
      Blocks.SUNFLOWER,
      Blocks.LILAC,
      Blocks.ROSE_BUSH,
      Blocks.PEONY);

  public static final KahoNesoComponent EMPTY = new KahoNesoComponent();

  public static final Codec<KahoNesoComponent> CODEC = RecordCodecBuilder.create(builder -> {
    return builder.group(
        Codec.list(BlockPos.CODEC).fieldOf("lastSpread").forGetter(KahoNesoComponent::getLastSpread),
        Codec.list(BlockPos.CODEC).fieldOf("allSpread").forGetter(KahoNesoComponent::getAllSpread))
        .apply(builder, KahoNesoComponent::new);
  });

  public static final PacketCodec<RegistryByteBuf, KahoNesoComponent> PACKET_CODEC = PacketCodec.tuple(
      BlockPos.PACKET_CODEC.collect(PacketCodecs.toList()), KahoNesoComponent::getLastSpread,
      BlockPos.PACKET_CODEC.collect(PacketCodecs.toList()), KahoNesoComponent::getAllSpread,
      KahoNesoComponent::new);

  static record TickContext(
      KahoNesoItem item,
      KahoNesoItem.Config config,
      ItemStack stack,
      ServerWorld world,
      int usedTicks,
      Random random) {
    boolean tryUseEnergy(long energy) {
      return item.tryUseEnergy(stack, energy);
    }

    boolean tryAction() {
      return tryUseEnergy(config.energyPerAction());
    }

    boolean tryReplace() {
      return tryUseEnergy(config.energyPerReplace());
    }

    boolean hasEnoughEnergy() {
      return item.getStoredEnergy(stack) >= config.energyPerAction();
    }

    int actionCount() {
      return usedTicks / config.intervalTicks();
    }

    boolean shouldSpread() {
      return actionCount() <= config.radius();
    }
  }

  private static final Emitter EMITTER = new Emitter.RandomUp(
      List.of(HasuParticleEffect.charaIcon(CharaUtils.KAHO_KEY)), 1f);

  private static void emitParticles(ServerWorld world, BlockPos pos) {
    int count = NUM_PARTICLES.val();
    // for (int i = 0; i < count; i++) {
    // EMITTER.emit(world, pos);
    // }
  }

  private static boolean validForReplace(World world, BlockState state, BlockPos pos) {
    return state.isSolidBlock(world, pos) && !state.hasBlockEntity();
  }

  public static KahoNesoComponent fromPlayerPos(TickContext context, BlockPos pos) {
    var ret = new KahoNesoComponent();
    return ret.trySpreadDown(context, pos) ? ret : EMPTY;
  }
  // #endregion Static fields

  private final List<BlockPos> lastSpread = new ArrayList<>();
  private final Set<BlockPos> allSpread = new HashSet<>();

  public KahoNesoComponent(List<BlockPos> lastSpread, List<BlockPos> allSpread) {
    this.lastSpread.addAll(lastSpread);
    this.allSpread.addAll(allSpread);
  }

  private KahoNesoComponent() {
  }

  public List<BlockPos> getLastSpread() {
    return lastSpread;
  }

  public List<BlockPos> getAllSpread() {
    return allSpread.stream().toList();
  }

  private boolean trySpreatAt(TickContext context, BlockState state, BlockPos pos) {
    if (allSpread.contains(pos))
      return false;
    boolean canSpread = false;
    if (state.isOf(Blocks.DIRT) || state.isOf(Blocks.GRASS_BLOCK)) {
      canSpread = context.tryAction();
    } else if (context.config().canReplace() && validForReplace(context.world(), state, pos)) {
      canSpread = context.tryReplace();
    }
    if (!canSpread)
      return false;

    allSpread.add(pos);
    lastSpread.add(pos);
    emitParticles(context.world(), pos);
    return true;
  }

  private boolean trySpreadDown(TickContext context, BlockPos pos) {
    World world = context.world();
    BlockState state = world.getBlockState(pos);
    int maxDy = MAX_SPREAD_DELTA_Y.val();
    if (state.isReplaceable()) {
      for (int dy = -1; dy >= -maxDy; dy--) {
        BlockPos newPos = pos.add(0, dy, 0);
        BlockState newState = world.getBlockState(newPos);
        if (newState.isReplaceable())
          continue;
        return trySpreatAt(context, newState, newPos);
      }
    }
    return false;
  }

  private boolean trySpreadUp(TickContext context, BlockPos pos) {
    World world = context.world();
    int maxDy = MAX_SPREAD_DELTA_Y.val();
    for (int dy = 0; dy <= maxDy; dy++) {
      BlockPos newPos = pos.add(0, dy, 0);
      if (!world.getBlockState(newPos.up()).isReplaceable())
        continue;
      BlockState newState = world.getBlockState(newPos);
      if (trySpreatAt(context, newState, newPos))
        return true;
    }
    return false;
  }

  private boolean trySpread(TickContext context, BlockPos pos) {
    return trySpreadDown(context, pos) || trySpreadUp(context, pos);
  }

  private void spreadNeighbors(TickContext context, BlockPos pos) {
    for (int i = 0; i < SPREAD_DX.length; i++) {
      int dx = SPREAD_DX[i];
      int dz = SPREAD_DZ[i];
      BlockPos newPos = pos.add(dx, 0, dz);
      trySpread(context, newPos);
    }
  }

  private void grow(TickContext context, BlockPos pos) {
    ServerWorld world = context.world();
    BlockPos upPos = pos.up();
    BlockState upState = world.getBlockState(upPos);
    Random random = context.random();
    // Check if there are flowers on the block already
    if (upState.getBlock() instanceof FlowerBlock) {
      // If there are flowers, grow them
      if (upState.getBlock() instanceof TallFlowerBlock tallFlowerBlock) {
        if (tallFlowerBlock.canGrow(world, random, upPos, upState) && context.tryAction()) {
          tallFlowerBlock.grow(world, random, upPos, upState);
          emitParticles(world, pos);
        }
      }
      return;
    }
    if (!upState.isReplaceable())
      return;

    var state = world.getBlockState(pos);
    if (state.isOf(Blocks.DIRT)) {
      // Replace dirt with grass block
      if (context.tryAction()) {
        world.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState());
        emitParticles(world, pos);
      }
      return;
    }
    if (state.isOf(Blocks.GRASS_BLOCK)) {
      // Grow grass block with flowers
      if (context.tryAction()) {
        Block flowerBlock = CollectionUtils.getRandomElement(FLOWER_BLOCKS, random);
        world.setBlockState(upPos, flowerBlock.getDefaultState());
        emitParticles(world, pos);
      }
      return;
    }
  }

  /**
   * Spread flowers around the given position.
   * 
   * @param context
   * @return Whether to stop spreading after this tick (due to low energy, etc.).
   */
  public boolean tick(TickContext context) {
    // Spread flowers
    int count = (int) (allSpread.size() * context.config().flowerRatio());
    var flowerPos = CollectionUtils.pickRandom(allSpread, count, context.random());
    for (BlockPos pos : flowerPos) {
      grow(context, pos);
    }
    // Spread neighbors
    if (context.shouldSpread()) {
      BlockPos[] source = lastSpread.toArray(BlockPos[]::new);
      lastSpread.clear();
      for (BlockPos pos : source) {
        spreadNeighbors(context, pos);
      }
    } else if (!lastSpread.isEmpty()) {
      lastSpread.clear();
    }
    return !context.hasEnoughEnergy();
  }

  public boolean isEmpty() {
    return lastSpread.isEmpty() && allSpread.isEmpty();
  }

  @Override
  public final boolean equals(Object rhs) {
    if (this == rhs) {
      return true;
    }
    if (rhs == null || this.getClass() != rhs.getClass()) {
      return false;
    }
    KahoNesoComponent other = (KahoNesoComponent) rhs;
    return this.lastSpread.equals(other.lastSpread) && this.allSpread.equals(other.allSpread);
  }

  @Override
  public final int hashCode() {
    int result = 17;
    result = 31 * result + lastSpread.hashCode();
    result = 31 * result + allSpread.hashCode();
    return result;
  }
}
