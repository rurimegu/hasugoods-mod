package dev.rurino.hasugoods.network;

import java.util.ArrayList;
import java.util.Collection;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.animation.IWithStateMachine;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

abstract class AbstractPlayAnimPayload implements CustomPayload {
  public record Transit(BlockPos blockPos, int state, int transitTick) {
    public static final PacketCodec<RegistryByteBuf, Transit> CODEC = PacketCodec.tuple(
        BlockPos.PACKET_CODEC,
        Transit::blockPos,
        PacketCodecs.INTEGER,
        Transit::state,
        PacketCodecs.INTEGER,
        Transit::transitTick,
        Transit::new);
  }

  private final Collection<Transit> anims;

  public AbstractPlayAnimPayload(Collection<Transit> anims) {
    this.anims = anims;
  }

  public AbstractPlayAnimPayload() {
    this(new ArrayList<>());
  }

  protected AbstractPlayAnimPayload add(Transit anim) {
    anims.add(anim);
    return this;
  }

  public Collection<Transit> anims() {
    return anims;
  }

  private void apply(World world, Transit anim) {
    if (!(world.getBlockEntity(anim.blockPos()) instanceof IWithStateMachine withStateMachine)) {
      Hasugoods.LOGGER.info("BlockEntity at {} does not have a state machine, skipped", anim.blockPos());
      return;
    }
    withStateMachine.getStateMachine().transit(anim.state(), anim.transitTick());
  }

  public void apply(World world) {
    if (!world.isClient) {
      Hasugoods.LOGGER.warn("Should not receive PlayAnimPayload on server, skipped");
      return;
    }
    for (Transit anim : anims) {
      apply(world, anim);
    }
  }
}

public class PlayAnimPayload extends AbstractPlayAnimPayload {
  public static final CustomPayload.Id<PlayAnimPayload> ID = new CustomPayload.Id<>(
      Hasugoods.id("play_anim"));
  public static final PacketCodec<RegistryByteBuf, PlayAnimPayload> CODEC = PacketCodec.tuple(
      PacketCodecs.collection(ArrayList::new, Transit.CODEC),
      PlayAnimPayload::anims,
      PlayAnimPayload::new);

  public PlayAnimPayload(Collection<Transit> anims) {
    super(anims);
  }

  public PlayAnimPayload() {
    super();
  }

  @Override
  public Id<PlayAnimPayload> getId() {
    return ID;
  }

  @Override
  public PlayAnimPayload add(Transit anim) {
    super.add(anim);
    return this;
  }

  public PlayAnimPayload add(BlockPos blockPos, int state, int transitTick) {
    return this.add(new Transit(blockPos, state, transitTick));
  }
}
