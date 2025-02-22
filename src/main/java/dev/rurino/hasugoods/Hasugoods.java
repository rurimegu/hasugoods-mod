package dev.rurino.hasugoods;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.rurino.hasugoods.damage.ModDamageTypes;
import dev.rurino.hasugoods.effect.ModEffects;
import dev.rurino.hasugoods.item.ModItems;

public class Hasugoods implements ModInitializer {
  public static final String MOD_ID = "hasugoods";

  // This logger is used to write text to the console and the log file.
  // It is considered best practice to use your mod id as the logger's name.
  // That way, it's clear which mod wrote info, warnings, and errors.
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  public static Identifier id(String path) {
    return Identifier.of(MOD_ID, path);
  }

  @Override
  public void onInitialize() {
    // This code runs as soon as Minecraft is in a mod-load-ready state.
    // However, some things (like resources) may still be uninitialized.
    // Proceed with mild caution.

    LOGGER.info("Initializing Hasugoods");

    ModItems.initialize();
    ModDamageTypes.initialize();
    ModEffects.initialize();
  }
}
