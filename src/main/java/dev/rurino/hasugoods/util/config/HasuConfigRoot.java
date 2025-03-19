package dev.rurino.hasugoods.util.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dev.rurino.hasugoods.Hasugoods;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class HasuConfigRoot extends HasuConfigObject {
  private final File configFile;
  private boolean createConfig = false;
  private boolean overwriteConfig = false;

  protected HasuConfigRoot(File configFile, JsonObject obj) {
    super("", obj);
    this.configFile = configFile;
    ServerWorldEvents.LOAD.register((server, world) -> maybeWriteConfig());
    ServerWorldEvents.UNLOAD.register((server, world) -> maybeWriteConfig());
  }

  private void writeConfig() {
    Hasugoods.LOGGER.info("Writing Config file: {}", configFile.getAbsolutePath());
    try (FileWriter writer = new FileWriter(configFile)) {
      writer.write(HasuConfig.GSON.toJson(serialize()));
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

  public HasuConfigRoot autoCreateConfig() {
    this.createConfig = true;
    return this;
  }

  public HasuConfigRoot autoOverwriteConfig() {
    this.overwriteConfig = true;
    return this;
  }
}
