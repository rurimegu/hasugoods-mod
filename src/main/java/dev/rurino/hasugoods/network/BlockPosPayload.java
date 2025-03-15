package dev.rurino.hasugoods.network;

import java.util.function.Function;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public abstract class BlockPosPayload implements CustomPayload {

  private final BlockPos blockPos;

  public static <T extends BlockPosPayload> PacketCodec<RegistryByteBuf, T> createCodec(Function<BlockPos, T> factory) {
    return PacketCodec.tuple(BlockPos.PACKET_CODEC, T::blockPos, factory);
  }

  public BlockPosPayload(BlockPos blockPos) {
    this.blockPos = blockPos;
  }

  @Override
  abstract public Id<? extends CustomPayload> getId();

  public BlockPos blockPos() {
    return blockPos;
  }

  public static class PlayNesoMergeAnim extends BlockPosPayload {
    public static final CustomPayload.Id<BlockPosPayload> ID = new CustomPayload.Id<>(
        Hasugoods.id("anim_nesobase_merge"));
    public static final PacketCodec<RegistryByteBuf, BlockPosPayload> CODEC = createCodec(PlayNesoMergeAnim::new);

    public PlayNesoMergeAnim(BlockPos blockPos) {
      super(blockPos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
      return ID;
    }
  }

  public static class StopNesoMergeAnim extends BlockPosPayload {
    public static final CustomPayload.Id<BlockPosPayload> ID = new CustomPayload.Id<>(
        Hasugoods.id("anim_nesobase_stop"));
    public static final PacketCodec<RegistryByteBuf, BlockPosPayload> CODEC = createCodec(StopNesoMergeAnim::new);

    public StopNesoMergeAnim(BlockPos blockPos) {
      super(blockPos);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
      return ID;
    }
  }
}
