package dev.rurino.hasugoods.util.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HasuConfigObject extends HasuConfig {

  private final Map<String, HasuConfig> children = new HashMap<>();

  protected HasuConfigObject(String path, JsonElement obj) {
    super(path, obj);
  }

  private HasuConfigObject get(String child) {
    if (children.containsKey(child)) {
      var ret = children.get(child);
      if (!(ret instanceof HasuConfigObject retConfig)) {
        throw new IllegalStateException(
            String.format("Duplicate config path: %s of different types", path + "." + child));
      }
      return retConfig;
    }
    JsonElement element = this.obj == null ? null : this.obj.getAsJsonObject().get(child);
    HasuConfigObject childConfig = new HasuConfigObject(path + "." + child, element);
    children.put(child, childConfig);
    return childConfig;
  }

  private <T extends HasuConfigValue<?>> T getValue(Class<T> type, String child,
      HasuConfigValue.Factory<T> defaultFactory) {
    String childPath = path + "." + child;
    if (children.containsKey(child)) {
      var ret = children.get(child);
      if (!type.isInstance(ret)) {
        throw new IllegalStateException(String.format("Duplicate config path: %s of different types", childPath));
      }
      return type.cast(ret);
    }
    JsonElement element = this.obj == null ? null : this.obj.getAsJsonObject().get(child);
    T childConfig = defaultFactory.create(childPath, element);
    children.put(child, childConfig);
    return childConfig;
  }

  private HasuConfigObject child(String[] parts, boolean excludeLast) {
    HasuConfigObject config = this;
    int lastIndex = parts.length - (excludeLast ? 1 : 0);
    for (int i = 0; i < lastIndex; i++) {
      config = config.get(parts[i]);
    }
    return config;
  }

  public HasuConfigObject child(String childPath) {
    String[] parts = StringUtils.split(childPath, ".");
    return child(parts, false);
  }

  public HasuConfigValue.Int getInt(String childPath, int defaultValue) {
    String[] parts = StringUtils.split(childPath, ".");
    HasuConfigObject config = child(parts, true);
    return config.getValue(HasuConfigValue.Int.class, parts[parts.length - 1],
        (path, el) -> new HasuConfigValue.Int(path, el, defaultValue));
  }

  public HasuConfigValue.Long getLong(String childPath, long defaultValue) {
    String[] parts = StringUtils.split(childPath, ".");
    HasuConfigObject config = child(parts, true);
    return config.getValue(HasuConfigValue.Long.class, parts[parts.length - 1],
        (path, el) -> new HasuConfigValue.Long(path, el, defaultValue));
  }

  public HasuConfigValue.Bool getBoolean(String childPath, boolean defaultValue) {
    String[] parts = StringUtils.split(childPath, ".");
    HasuConfigObject config = child(parts, true);
    return config.getValue(HasuConfigValue.Bool.class, parts[parts.length - 1],
        (path, el) -> new HasuConfigValue.Bool(path, el, defaultValue));
  }

  public HasuConfigValue.Float getFloat(String childPath, float defaultValue) {
    String[] parts = StringUtils.split(childPath, ".");
    HasuConfigObject config = child(parts, true);
    return config.getValue(HasuConfigValue.Float.class, parts[parts.length - 1],
        (path, el) -> new HasuConfigValue.Float(path, el, defaultValue));
  }

  public HasuConfigValue.Double getDouble(String childPath, double defaultValue) {
    String[] parts = StringUtils.split(childPath, ".");
    HasuConfigObject config = child(parts, true);
    return config.getValue(HasuConfigValue.Double.class, parts[parts.length - 1],
        (path, el) -> new HasuConfigValue.Double(path, el, defaultValue));
  }

  public HasuConfigValue.Str getString(String childPath, String defaultValue) {
    String[] parts = StringUtils.split(childPath, ".");
    HasuConfigObject config = child(parts, true);
    return config.getValue(HasuConfigValue.Str.class, parts[parts.length - 1],
        (path, el) -> new HasuConfigValue.Str(path, el, defaultValue));
  }

  @Override
  protected JsonElement serialize() {
    JsonObject obj = new JsonObject();
    for (Map.Entry<String, HasuConfig> entry : children.entrySet()) {
      obj.add(entry.getKey(), entry.getValue().serialize());
    }
    return obj;
  }

}
