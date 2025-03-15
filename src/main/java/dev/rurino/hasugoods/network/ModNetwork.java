package dev.rurino.hasugoods.network;

import dev.rurino.hasugoods.network.BlockPosPayload.PlayNesoMergeAnim;
import dev.rurino.hasugoods.network.BlockPosPayload.StopNesoMergeAnim;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModNetwork {
  public static void initialize() {
    PayloadTypeRegistry.playS2C().register(PlayNesoMergeAnim.ID, PlayNesoMergeAnim.CODEC);
    PayloadTypeRegistry.playS2C().register(StopNesoMergeAnim.ID, StopNesoMergeAnim.CODEC);
  }
}
