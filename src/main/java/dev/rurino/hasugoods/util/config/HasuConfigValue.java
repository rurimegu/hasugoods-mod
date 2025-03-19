package dev.rurino.hasugoods.util.config;

import com.google.gson.JsonElement;

import dev.rurino.hasugoods.Hasugoods;

public abstract class HasuConfigValue<T> extends HasuConfig {
  public static interface Factory<T extends HasuConfigValue<?>> {
    T create(String path, JsonElement obj);
  }

  private final T defaultValue;

  protected HasuConfigValue(String path, JsonElement obj, T defaultValue) {
    super(path, obj);
    this.defaultValue = defaultValue;
  }

  protected abstract T getNotNull();

  public final T value() {
    if (obj == null)
      return defaultValue;
    try {
      return getNotNull();
    } catch (Exception e) {
      Hasugoods.LOGGER.error("Failed to get config value for {}, {}: {}", path, obj, e);
      return defaultValue;
    }
  }

  @Override
  protected JsonElement serialize() {
    return HasuConfig.GSON.toJsonTree(value());
  }

  public static class Int extends HasuConfigValue<Integer> {

    protected Int(String path, JsonElement obj, int defaultValue) {
      super(path, obj, defaultValue);
    }

    @Override
    public Integer getNotNull() {
      return obj.getAsInt();
    }

  }

  public static class Long extends HasuConfigValue<java.lang.Long> {

    protected Long(String path, JsonElement obj, long defaultValue) {
      super(path, obj, defaultValue);
    }

    @Override
    public java.lang.Long getNotNull() {
      return obj.getAsLong();
    }

  }

  public static class Float extends HasuConfigValue<java.lang.Float> {

    protected Float(String path, JsonElement obj, float defaultValue) {
      super(path, obj, defaultValue);
    }

    @Override
    public java.lang.Float getNotNull() {
      return obj.getAsFloat();
    }

  }

  public static class Double extends HasuConfigValue<java.lang.Double> {

    protected Double(String path, JsonElement obj, double defaultValue) {
      super(path, obj, defaultValue);
    }

    @Override
    public java.lang.Double getNotNull() {
      return obj.getAsDouble();
    }

  }

  public static class Str extends HasuConfigValue<String> {

    protected Str(String path, JsonElement obj, String defaultValue) {
      super(path, obj, defaultValue);
    }

    @Override
    public String getNotNull() {
      return obj.getAsString();
    }

  }

  public static class Bool extends HasuConfigValue<Boolean> {

    protected Bool(String path, JsonElement obj, boolean defaultValue) {
      super(path, obj, defaultValue);
    }

    @Override
    public Boolean getNotNull() {
      return obj.getAsBoolean();
    }

  }
}
