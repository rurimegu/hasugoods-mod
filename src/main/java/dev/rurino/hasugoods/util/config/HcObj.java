package dev.rurino.hasugoods.util.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HcObj extends HcBase {

  private final Map<String, HcBase> children = new HashMap<>();

  protected HcObj(String path, JsonElement obj) {
    super(path, obj == null ? new JsonObject() : obj);
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

  private HcObj child(String[] parts, boolean excludeLast) {
    HcObj config = this;
    int lastIndex = parts.length - (excludeLast ? 1 : 0);
    for (int i = 0; i < lastIndex; i++) {
      config = config.get(parts[i]);
    }
    return config;
  }

  public HcObj child(String childPath) {
    String[] parts = StringUtils.split(childPath, ".");
    return child(parts, false);
  }

  public HcVal.Int getInt(String childPath, int defaultValue) {
    String[] parts = StringUtils.split(childPath, ".");
    HcObj config = child(parts, true);
    return config.getValue(HcVal.Int.class, parts[parts.length - 1],
        (path, el) -> new HcVal.Int(path, el, defaultValue));
  }

  public HcVal.Long getLong(String childPath, long defaultValue) {
    String[] parts = StringUtils.split(childPath, ".");
    HcObj config = child(parts, true);
    return config.getValue(HcVal.Long.class, parts[parts.length - 1],
        (path, el) -> new HcVal.Long(path, el, defaultValue));
  }

  public HcVal.Bool getBoolean(String childPath, boolean defaultValue) {
    String[] parts = StringUtils.split(childPath, ".");
    HcObj config = child(parts, true);
    return config.getValue(HcVal.Bool.class, parts[parts.length - 1],
        (path, el) -> new HcVal.Bool(path, el, defaultValue));
  }

  public HcVal.Float getFloat(String childPath, float defaultValue) {
    String[] parts = StringUtils.split(childPath, ".");
    HcObj config = child(parts, true);
    return config.getValue(HcVal.Float.class, parts[parts.length - 1],
        (path, el) -> new HcVal.Float(path, el, defaultValue));
  }

  public HcVal.Double getDouble(String childPath, double defaultValue) {
    String[] parts = StringUtils.split(childPath, ".");
    HcObj config = child(parts, true);
    return config.getValue(HcVal.Double.class, parts[parts.length - 1],
        (path, el) -> new HcVal.Double(path, el, defaultValue));
  }

  public HcVal.Str getString(String childPath, String defaultValue) {
    String[] parts = StringUtils.split(childPath, ".");
    HcObj config = child(parts, true);
    return config.getValue(HcVal.Str.class, parts[parts.length - 1],
        (path, el) -> new HcVal.Str(path, el, defaultValue));
  }

}
