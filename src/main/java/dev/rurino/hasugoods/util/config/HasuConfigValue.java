package dev.rurino.hasugoods.util.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public abstract class HasuConfigValue<T> extends HasuConfig {
  public static interface Factory<T extends HasuConfigValue<?>> {
    T create(String path, JsonElement obj);
  }

  protected HasuConfigValue(String path, JsonElement obj) {
    super(path, obj);
  }

  public abstract T value();

  public static class Int extends HasuConfigValue<Integer> {

    protected Int(String path, JsonElement obj, int defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public Integer value() {
      return obj.getAsInt();
    }

  }

  public static class Long extends HasuConfigValue<java.lang.Long> {

    protected Long(String path, JsonElement obj, long defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public java.lang.Long value() {
      return obj.getAsLong();
    }

  }

  public static class Float extends HasuConfigValue<java.lang.Float> {

    protected Float(String path, JsonElement obj, float defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public java.lang.Float value() {
      return obj.getAsFloat();
    }

  }

  public static class Double extends HasuConfigValue<java.lang.Double> {

    protected Double(String path, JsonElement obj, double defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public java.lang.Double value() {
      return obj.getAsDouble();
    }

  }

  public static class Str extends HasuConfigValue<String> {

    protected Str(String path, JsonElement obj, String defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public String value() {
      return obj.getAsString();
    }

  }

  public static class Bool extends HasuConfigValue<Boolean> {

    protected Bool(String path, JsonElement obj, boolean defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public Boolean value() {
      return obj.getAsBoolean();
    }

  }
}
