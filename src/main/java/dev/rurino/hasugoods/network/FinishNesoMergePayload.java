package dev.rurino.hasugoods.network;

import java.util.ArrayList;
import java.util.Collection;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;

public class FinishNesoMergePayload extends AbstractPlayAnimPayload {

  public static final CustomPayload.Id<FinishNesoMergePayload> ID = new CustomPayload.Id<>(
      Hasugoods.id("finish_neso_merge"));
  public static final PacketCodec<RegistryByteBuf, FinishNesoMergePayload> CODEC = PacketCodec.tuple(
      PacketCodecs.collection(ArrayList::new, Transit.CODEC),
      FinishNesoMergePayload::anims,
      PacketCodecs.BOOL,
      FinishNesoMergePayload::isSuccess,
      FinishNesoMergePayload::new);

  private final boolean success;

  public FinishNesoMergePayload(Collection<Transit> anims, boolean success) {
    super(anims);
    this.success = success;
  }

  public FinishNesoMergePayload(boolean success) {
    this(new ArrayList<>(), success);
  }

  public boolean isSuccess() {
    return success;
  }

  @Override
  public Id<FinishNesoMergePayload> getId() {
    return ID;
  }

  @Override
  public FinishNesoMergePayload add(Transit anim) {
    super.add(anim);
    return this;
  }

  public FinishNesoMergePayload add(BlockPos blockPos, int state, int transitTick) {
    return add(new Transit(blockPos, state, transitTick));
  }

  public FinishNesoMergePayload add(BlockPos blockPos, int state) {
    return add(blockPos, state, 0);
  }

}
