package dev.rurino.hasugoods.util.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * HasuConfig value object.
 * <p>
 * This class represents a configuration value in the HasuConfig system.
 * It corresponds to a JSON primitive value and is the leaf node in the
 * configuration tree.
 */
public abstract class HcVal<T> extends HcBase {
  public static interface Factory<T extends HcVal<?>> {
    T create(String path, JsonElement obj);
  }

  protected void validate(T val) {
  }

  protected HcVal(String path, JsonElement obj) {
    super(path, obj);
    if (obj == null) {
      throw new IllegalArgumentException(
          String.format("At %s: Value is null", path));
    }
    if (!obj.isJsonPrimitive()) {
      throw new IllegalArgumentException(
          String.format("At %s: %s is not a JSON primitive", path, obj));
    }
  }

  public abstract T val();

  private static abstract class Numeric<U extends Number & Comparable<U>, Self extends Numeric<U, Self>>
      extends HcVal<U> {
    protected U minVal = null;
    protected U maxVal = null;
    protected boolean nonzero = false;

    protected Numeric(String path, JsonElement obj) {
      super(path, obj);
    }

    abstract protected Self self();

    abstract protected U cast(Number number);

    @Override
    protected void validate(U val) {
      super.validate(val);
      if (nonzero && val.doubleValue() == 0) {
        throw new IllegalArgumentException(
            String.format("At %s: must be nonzero, got %s", path, val));
      }
      if (minVal != null && val.compareTo(minVal) < 0) {
        throw new IllegalArgumentException(
            String.format("At %s: Value %s is less than minimum %s", path, val, minVal));
      }
      if (maxVal != null && val.compareTo(maxVal) > 0) {
        throw new IllegalArgumentException(
            String.format("At %s: Value %s is greater than maximum %s", path, val, maxVal));
      }
    }

    public Self nonzero() {
      this.nonzero = true;
      validate(val());
      return self();
    }

    public Self nonpositive() {
      return max(cast(0));
    }

    public Self nonnegative() {
      return min(cast(0));
    }

    public Self positive() {
      return nonnegative().nonzero();
    }

    public Self negative() {
      return nonpositive().nonzero();
    }

    public Self min(U minVal) {
      this.minVal = minVal;
      validate(val());
      return self();
    }

    public Self max(U maxVal) {
      this.maxVal = maxVal;
      validate(val());
      return self();
    }
  }

  public static class Int extends Numeric<Integer, Int> {
    protected Int(String path, JsonElement obj, int defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    protected Integer cast(Number number) {
      return number.intValue();
    }

    @Override
    protected Int self() {
      return this;
    }

    @Override
    public Integer val() {
      return obj.getAsInt();
    }
  }

  public static class Long extends Numeric<java.lang.Long, Long> {

    protected Long(String path, JsonElement obj, long defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    protected java.lang.Long cast(Number number) {
      return number.longValue();
    }

    @Override
    protected Long self() {
      return this;
    }

    @Override
    public java.lang.Long val() {
      return obj.getAsLong();
    }
  }

  public static class Float extends Numeric<java.lang.Float, Float> {

    protected Float(String path, JsonElement obj, float defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    protected java.lang.Float cast(Number number) {
      return number.floatValue();
    }

    @Override
    protected Float self() {
      return this;
    }

    @Override
    public java.lang.Float val() {
      return obj.getAsFloat();
    }

  }

  public static class Double extends Numeric<java.lang.Double, Double> {

    protected Double(String path, JsonElement obj, double defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    protected java.lang.Double cast(Number number) {
      return number.doubleValue();
    }

    @Override
    protected Double self() {
      return this;
    }

    @Override
    public java.lang.Double val() {
      return obj.getAsDouble();
    }

  }

  public static class Str extends HcVal<String> {

    private int minLength = 0;
    private int maxLength = Integer.MAX_VALUE;

    protected Str(String path, JsonElement obj, String defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public String val() {
      return obj.getAsString();
    }

    public Str min(int len) {
      this.minLength = len;
      validate(val());
      return this;
    }

    public Str max(int len) {
      this.maxLength = len;
      validate(val());
      return this;
    }

    public Str nonempty() {
      return min(1);
    }

    @Override
    protected void validate(String val) {
      super.validate(val);
      if (val.length() < minLength) {
        throw new IllegalArgumentException(
            String.format("At %s: Value %s is less than minimum length %d", path, val, minLength));
      }
      if (val.length() > maxLength) {
        throw new IllegalArgumentException(
            String.format("At %s: Value %s is greater than maximum length %d", path, val, maxLength));
      }
    }

  }

  public static class Bool extends HcVal<Boolean> {

    protected Bool(String path, JsonElement obj, boolean defaultValue) {
      super(path, obj == null ? new JsonPrimitive(defaultValue) : obj);
    }

    @Override
    public Boolean val() {
      return obj.getAsBoolean();
    }

  }
}
