package dev.rurino.hasugoods.util.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * HasuConfig object.
 * <p>
 * This class represents a configuration object in the HasuConfig system.
 * It corresponds to a JSON object and allows for nested configuration
 * structures.
 */
public class HcObj extends HcBase {

  protected final Map<String, HcBase> children = new HashMap<>();

  protected HcObj(String path, JsonElement obj) {
    super(path, obj == null ? new JsonObject() : obj);
  }

  @Override
  protected void clearDirty() {
    super.clearDirty();
    for (HcBase child : children.values()) {
      child.clearDirty();
    }
  }

  private void putChild(String key, HcBase child) {
    children.put(key, child);
    obj.getAsJsonObject().add(key, child.obj);
    this.isDirty = true;
  }

  private HcObj get(String child) {
    if (children.containsKey(child)) {
      var ret = children.get(child);
      isDirty |= ret.isDirty;
      if (!(ret instanceof HcObj retConfig)) {
        throw new IllegalStateException(
            String.format("Duplicate config path: %s of different types", path + "." + child));
      }
      return retConfig;
    }
    JsonElement element = this.obj.getAsJsonObject().get(child);
    HcObj childConfig = new HcObj(path + "." + child, element);
    putChild(child, childConfig);
    return childConfig;
  }

  private <T extends HcVal<?>> T getValue(Class<T> type, String child,
      HcVal.Factory<T> defaultFactory) {
    String childPath = path + "." + child;
    if (children.containsKey(child)) {
      var ret = children.get(child);
      isDirty |= ret.isDirty;
      if (!type.isInstance(ret)) {
        throw new IllegalStateException(String.format("Duplicate config path: %s of different types", childPath));
      }
      return type.cast(ret);
    }
    JsonElement element = this.obj.getAsJsonObject().get(child);
    T childConfig = defaultFactory.create(childPath, element);
    putChild(child, childConfig);
    return childConfig;
  }

  private <T extends HcVal<?>> T readValue(Class<T> type, String child) {
    String childPath = path + "." + child;
    if (!children.containsKey(child)) {
      return null;
    }
    var ret = children.get(child);
    if (!type.isInstance(ret)) {
      throw new IllegalStateException(String.format("Duplicate config path: %s of different types", childPath));
    }
    return type.cast(ret);
  }

  private HcObj child(String[] parts, boolean excludeLast) {
    HcObj config = this;
    int lastIndex = parts.length - (excludeLast ? 1 : 0);
    for (int i = 0; i < lastIndex; i++) {
      config = config.get(parts[i]);
    }
    return config;
  }

  private HcObj readChild(String[] parts, boolean excludeLast) {
    HcObj config = this;
    int lastIndex = parts.length - (excludeLast ? 1 : 0);
    for (int i = 0; i < lastIndex; i++) {
      if (!config.children.containsKey(parts[i])) {
        return null;
      }
    }
    return config;
  }

  public HcObj child(String path) {
    String[] parts = StringUtils.split(path, ".");
    return child(parts, false);
  }

  public HcVal.Int getInt(String path, int defaultVal) {
    String[] parts = StringUtils.split(path, ".");
    HcObj config = child(parts, true);
    return config.getValue(HcVal.Int.class, parts[parts.length - 1],
        (p, el) -> new HcVal.Int(p, el, defaultVal));
  }

  public HcVal.Int readInt(String path) {
    String[] parts = StringUtils.split(path, ".");
    HcObj config = readChild(parts, true);
    return config.readValue(HcVal.Int.class, parts[parts.length - 1]);
  }

  public HcVal.Long getLong(String path, long defaultVal) {
    String[] parts = StringUtils.split(path, ".");
    HcObj config = child(parts, true);
    return config.getValue(HcVal.Long.class, parts[parts.length - 1],
        (p, el) -> new HcVal.Long(p, el, defaultVal));
  }

  public HcVal.Long readLong(String path) {
    String[] parts = StringUtils.split(path, ".");
    HcObj config = readChild(parts, true);
    return config.readValue(HcVal.Long.class, parts[parts.length - 1]);
  }

  public HcVal.Bool getBoolean(String path, boolean defaultVal) {
    String[] parts = StringUtils.split(path, ".");
    HcObj config = child(parts, true);
    return config.getValue(HcVal.Bool.class, parts[parts.length - 1],
        (p, el) -> new HcVal.Bool(p, el, defaultVal));
  }

  public HcVal.Bool readBoolean(String path) {
    String[] parts = StringUtils.split(path, ".");
    HcObj config = readChild(parts, true);
    return config.readValue(HcVal.Bool.class, parts[parts.length - 1]);
  }

  public HcVal.Float getFloat(String path, float defaultVal) {
    String[] parts = StringUtils.split(path, ".");
    HcObj config = child(parts, true);
    return config.getValue(HcVal.Float.class, parts[parts.length - 1],
        (p, el) -> new HcVal.Float(p, el, defaultVal));
  }

  public HcVal.Float readFloat(String path) {
    String[] parts = StringUtils.split(path, ".");
    HcObj config = readChild(parts, true);
    return config.readValue(HcVal.Float.class, parts[parts.length - 1]);
  }

  public HcVal.Double getDouble(String path, double defaultVal) {
    String[] parts = StringUtils.split(path, ".");
    HcObj config = child(parts, true);
    return config.getValue(HcVal.Double.class, parts[parts.length - 1],
        (p, el) -> new HcVal.Double(p, el, defaultVal));
  }

  public HcVal.Double readDouble(String path) {
    String[] parts = StringUtils.split(path, ".");
    HcObj config = readChild(parts, true);
    return config.readValue(HcVal.Double.class, parts[parts.length - 1]);
  }

  public HcVal.Str getString(String path, String defaultVal) {
    String[] parts = StringUtils.split(path, ".");
    HcObj config = child(parts, true);
    return config.getValue(HcVal.Str.class, parts[parts.length - 1],
        (p, el) -> new HcVal.Str(p, el, defaultVal));
  }

  public HcVal.Str readString(String path) {
    String[] parts = StringUtils.split(path, ".");
    HcObj config = readChild(parts, true);
    return config.readValue(HcVal.Str.class, parts[parts.length - 1]);
  }

}
