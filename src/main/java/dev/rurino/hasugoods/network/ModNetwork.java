package dev.rurino.hasugoods.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModNetwork {
  public static void initialize() {
    PayloadTypeRegistry.playS2C().register(PlayAnimPayload.ID, PlayAnimPayload.CODEC);
  }
}
