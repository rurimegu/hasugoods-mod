package dev.rurino.hasugoods;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import dev.rurino.hasugoods.block.ModBlockEntities;
import dev.rurino.hasugoods.block.ModBlocks;
import dev.rurino.hasugoods.config.ModConfig;
import dev.rurino.hasugoods.damage.ModDamageTypes;
import dev.rurino.hasugoods.effect.ModEffects;
import dev.rurino.hasugoods.entity.ModEntities;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.network.ModNetwork;
import dev.rurino.hasugoods.particle.ModParticles;

public class Hasugoods implements ModInitializer {
  public static final String MOD_ID = "hasugoods";
  public static final Gson GSON = new Gson();
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public static final ModConfig CONFIG = ModConfig.V;

  public static Identifier id(String path) {
    return Identifier.of(MOD_ID, path);
  }

  @Override
  public void onInitialize() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    LOGGER.info("Initializing Hasugoods");

    ModParticles.initialize();
    ModItems.initialize();
    ModBlocks.initialize();
    ModBlockEntities.initialize();
    ModDamageTypes.initialize();
    ModEffects.initialize();
    ModEntities.initialize();
    ModNetwork.initialize();
  }
}
