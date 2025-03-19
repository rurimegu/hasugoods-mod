package dev.rurino.hasugoods.util;

public class HasuString {
  public static String formatEnergy(long amount) {
    if (amount < 0) {
      return "0";
    }
    if (amount >= 1_000_000_000) {
      return String.format("%.2fB", amount / 1_000_000_000.0);
    }
    if (amount >= 1_000_000) {
      return String.format("%.2fM", amount / 1_000_000.0);
    }
    if (amount >= 1_000) {
      return String.format("%.2fK", amount / 1_000.0);
    }
    if (amount >= 0) {
      return String.valueOf(amount);
    }
    return "?";
  }
}
