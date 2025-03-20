package dev.rurino.hasugoods.util.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * Base class for HasuConfig objects.
 * <p>
 * This abstract class provides common functionality for all HasuConfig objects.
 */
public abstract class HcBase {
  protected static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  protected final JsonElement obj;
  protected final String path;
  protected boolean isDirty = false;

  protected HcBase(String path, JsonElement obj) {
    if (obj == null) {
      throw new IllegalArgumentException("obj cannot be null in HasuConfig constructor");
    }
    this.path = path;
    this.obj = obj;
  }

  protected void clearDirty() {
    isDirty = false;
  }
}
