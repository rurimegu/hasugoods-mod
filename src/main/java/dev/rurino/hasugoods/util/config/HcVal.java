package dev.rurino.hasugoods.util.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public abstract class HcVal<T> extends HcBase {
  public static interface Factory<T extends HcVal<?>> {
    T create(String path, JsonElement obj);
  }

  protected HcVal(String path, JsonElement obj) {
    super(path, obj);
  }

  public abstract T value();

  public static class Int extends HcVal<Integer> {

    protected Int(String path, JsonElement obj, int defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public Integer value() {
      return obj.getAsInt();
    }

  }

  public static class Long extends HcVal<java.lang.Long> {

    protected Long(String path, JsonElement obj, long defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public java.lang.Long value() {
      return obj.getAsLong();
    }

  }

  public static class Float extends HcVal<java.lang.Float> {

    protected Float(String path, JsonElement obj, float defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public java.lang.Float value() {
      return obj.getAsFloat();
    }

  }

  public static class Double extends HcVal<java.lang.Double> {

    protected Double(String path, JsonElement obj, double defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public java.lang.Double value() {
      return obj.getAsDouble();
    }

  }

  public static class Str extends HcVal<String> {

    protected Str(String path, JsonElement obj, String defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public String value() {
      return obj.getAsString();
    }

  }

  public static class Bool extends HcVal<Boolean> {

    protected Bool(String path, JsonElement obj, boolean defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public Boolean value() {
      return obj.getAsBoolean();
    }

  }
}
