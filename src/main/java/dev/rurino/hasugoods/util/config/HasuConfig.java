package dev.rurino.hasugoods.util.config;

import java.io.File;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.loader.api.FabricLoader;

public abstract class HasuConfig {
  protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  protected final JsonElement obj;
  protected final String path;
  protected boolean isDirty = false;

  protected HasuConfig(String path, JsonElement obj) {
    if (obj == null) {
      throw new IllegalArgumentException("obj cannot be null in HasuConfig constructor");
    }
    this.path = path;
    this.obj = obj;
  }

  public static HasuConfigRoot createAndLoad(String configName) {
    File configFile = FabricLoader.getInstance().getConfigDir()
        .resolve(configName + ".json")
        .toFile();
    JsonObject json = null;
    if (configFile.exists()) {
      json = HasuConfigRoot.readConfig(configFile);
    }
    return new HasuConfigRoot(configFile, json);
  }
}
