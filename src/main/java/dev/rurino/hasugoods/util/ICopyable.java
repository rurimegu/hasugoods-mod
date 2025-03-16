package dev.rurino.hasugoods.util;

public interface ICopyable<T extends ICopyable<T>> {
  T copy();
}
