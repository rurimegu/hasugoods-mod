package dev.rurino.hasugoods.block;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;

public class PositionZeroBlockEntityTest {

  @BeforeAll
  static void beforeAll() {
    SharedConstants.createGameVersion();
    Bootstrap.initialize();
  }

  @Test
  void testCheckIsolatedComponents_regularSuccess() {
    boolean[] result = PositionZeroBlockEntity.checkIsolatedComponents(
        new Integer[] { 1, 1, 1, 2, 2, 2, 3, 3, 3 }, i -> i == 0);
    Assertions.assertArrayEquals(
        new boolean[] { false, false, false, false, false, false, false, false, false }, result);
  }

  @Test
  void testCheckIsolatedComponents_circularSuccess() {
    boolean[] result = PositionZeroBlockEntity.checkIsolatedComponents(
        new Integer[] { 3, 1, 1, 1, 2, 2, 3, 3, 2 }, i -> i == 0);
    Assertions.assertArrayEquals(
        new boolean[] { false, false, false, false, false, false, false, false, false }, result);
  }

  @Test
  void testCheckIsolatedComponents_circularSuccess2() {
    boolean[] result = PositionZeroBlockEntity.checkIsolatedComponents(
        new Integer[] { 3, 3, 1, 1, 1, 2, 2, 3, 2 }, i -> i == 0);
    Assertions.assertArrayEquals(
        new boolean[] { false, false, false, false, false, false, false, false, false }, result);
  }

  @Test
  void testCheckIsolatedComponents_oneIsolated() {
    boolean[] result = PositionZeroBlockEntity.checkIsolatedComponents(
        new Integer[] { 3, 3, 1, 1, 2, 2, 2, 1, 2 }, i -> i == 0);
    Assertions.assertArrayEquals(
        new boolean[] { false, false, true, true, false, false, false, true, false }, result);
  }

  @Test
  void testCheckIsolatedComponents_multipleIsolated() {
    boolean[] result = PositionZeroBlockEntity.checkIsolatedComponents(
        new Integer[] { 3, 2, 3, 3, 1, 2, 2, 1, 2 }, i -> i == 0);
    Assertions.assertArrayEquals(
        new boolean[] { true, false, true, true, true, false, false, true, false }, result);
  }

  @Test
  void testCheckIsolatedComponents_multipleIsolatedWithSpace() {
    boolean[] result = PositionZeroBlockEntity.checkIsolatedComponents(
        new Integer[] { 3, 0, 3, 3, 1, 0, 0, 1, 2 }, i -> i == 0);
    Assertions.assertArrayEquals(
        new boolean[] { true, false, true, true, true, false, false, true, false }, result);
  }
}
