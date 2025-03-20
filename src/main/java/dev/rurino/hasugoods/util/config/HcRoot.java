package dev.rurino.hasugoods.util.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.config.HcVal.Bool;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;

public class HcRoot extends HcObj {
  private final File configFile;
  private boolean createConfig = false;
  private boolean overwriteConfig = false;
  private boolean initialized = false;

  public static HcRoot createAndLoad(String configName) {
    File configFile = FabricLoader.getInstance().getConfigDir()
        .resolve(configName + ".json")
        .toFile();
    JsonObject json = null;
    if (configFile.exists()) {
      json = HcRoot.readConfig(configFile);
    }
    return new HcRoot(configFile, json);
  }

  protected HcRoot(File configFile, JsonObject obj) {
    super("", obj);
    this.isDirty = true;
    this.configFile = configFile;
    ServerWorldEvents.LOAD.register((server, world) -> maybeWriteConfig());
    ServerWorldEvents.UNLOAD.register((server, world) -> maybeWriteConfig());
  }

  // #region Validate initialization

  private void validateInitialized(String childPath) {
    if (initialized && isDirty) {
      Hasugoods.LOGGER.warn(
          "Config path {} registered after initialization. Consider initializing it earlier.",
          childPath);
    }
  }

  @Override
  public HcObj child(String childPath) {
    var ret = super.child(childPath);
    validateInitialized(childPath);
    return ret;
  }

  @Override
  public Bool getBoolean(String childPath, boolean defaultValue) {
    var ret = super.getBoolean(childPath, defaultValue);
    validateInitialized(childPath);
    return ret;
  }

  @Override
  public HcVal.Int getInt(String childPath, int defaultValue) {
    var ret = super.getInt(childPath, defaultValue);
    validateInitialized(childPath);
    return ret;
  }

  @Override
  public HcVal.Long getLong(String childPath, long defaultValue) {
    var ret = super.getLong(childPath, defaultValue);
    validateInitialized(childPath);
    return ret;
  }

  @Override
  public HcVal.Float getFloat(String childPath, float defaultValue) {
    var ret = super.getFloat(childPath, defaultValue);
    validateInitialized(childPath);
    return ret;
  }

  @Override
  public HcVal.Double getDouble(String childPath, double defaultValue) {
    var ret = super.getDouble(childPath, defaultValue);
    validateInitialized(childPath);
    return ret;
  }

  @Override
  public HcVal.Str getString(String childPath, String defaultValue) {
    var ret = super.getString(childPath, defaultValue);
    validateInitialized(childPath);
    return ret;
  }

  // #endregion Validate initialization

  private void writeConfig() {
    if (!isDirty)
      return;
    isDirty = false;
    Hasugoods.LOGGER.info("Writing Config file: {}", configFile.getAbsolutePath());
    try (FileWriter writer = new FileWriter(configFile)) {
      writer.write(HcBase.GSON.toJson(obj));
    } catch (IOException e) {
      Hasugoods.LOGGER.error("Failed to write config file: {}\n{}", configFile.getAbsolutePath(), e);
    }
  }

  protected static JsonObject readConfig(File configFile) {
    Hasugoods.LOGGER.info("Reading Config file: {}", configFile.getAbsolutePath());
    try (FileReader reader = new FileReader(configFile)) {
      return JsonParser.parseReader(reader).getAsJsonObject();
    } catch (Exception e) {
      Hasugoods.LOGGER.error("Failed to read config file: {}\n{}", configFile.getAbsolutePath(), e);
      return null;
    }
  }

  /**
   * Creates the config file if it does not exist.
   * 
   * @return true if the file was created or overwritten, false if it already
   *         existed
   */
  private boolean maybeWriteConfig() {
    if (configFile.exists()) {
      if (overwriteConfig) {
        writeConfig();
        return true;
      }
      return false;
    }

    if (!createConfig) {
      return false;
    }

    configFile.getParentFile().mkdirs();
    try {
      configFile.createNewFile();
    } catch (IOException e) {
      Hasugoods.LOGGER.error("Failed to create config file: {}\n{}", configFile.getAbsolutePath(), e);
      return false;
    }
    Hasugoods.LOGGER.info("Created config file: {}", configFile.getAbsolutePath());
    writeConfig();
    return true;
  }

  public HcRoot autoCreateConfig() {
    this.createConfig = true;
    return this;
  }

  public HcRoot autoOverwriteConfig() {
    this.overwriteConfig = true;
    return this;
  }

  public HcRoot onInitializateComplete() {
    maybeWriteConfig();
    initialized = true;
    return this;
  }
}
