package dev.rurino.hasugoods.util;

public interface Easing {

  double ease(double t);

  public static double constant(double t) {
    return t < 1 ? 0 : 1;
  }

  public static double linear(double t) {
    return t;
  }

  public static double easeIn(double t) {
    return Math.pow(t, 2);
  }

  public static double easeOut(double t) {
    return 1 - Math.pow(1 - t, 2);
  }

  public static double easeInOut(double t) {
    return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
  }

  public static double easeInCubic(double t) {
    return t * t * t;
  }

  public static double easeOutCubic(double t) {
    return 1 - Math.pow(1 - t, 3);
  }

  public static double easeInOutCubic(double t) {
    return t < 0.5 ? 4 * t * t * t : 1 - Math.pow(-2 * t + 2, 3) / 2;
  }

  public static double easeInQuint(double t) {
    return t * t * t * t * t;
  }

  public static double easeOutQuint(double t) {
    return 1 - Math.pow(1 - t, 5);
  }

  public static double easeInOutQuint(double t) {
    return t < 0.5 ? 16 * t * t * t * t * t : 1 - Math.pow(-2 * t + 2, 5) / 2;
  }

  public static double easeInSine(double t) {
    return 1 - Math.cos((t * Math.PI) / 2);
  }

  public static double easeOutSine(double t) {
    return Math.sin((t * Math.PI) / 2);
  }

  public static double easeInOutSine(double t) {
    return -(Math.cos(Math.PI * t) - 1) / 2;
  }

  public static double easeInExpo(double t) {
    return Math.pow(2, 10 * (t - 1));
  }

  public static double easeOutExpo(double t) {
    return 1 - Math.pow(2, -10 * t);
  }

  public static double easeInOutExpo(double t) {
    return t < 0.5 ? Math.pow(2, 20 * t - 10) / 2 : (2 - Math.pow(2, -20 * t + 10)) / 2;
  }

  public static double easeInCirc(double t) {
    return 1 - Math.sqrt(1 - Math.pow(t, 2));
  }

  public static double easeOutCirc(double t) {
    return Math.sqrt(1 - Math.pow(t - 1, 2));
  }

  public static double easeInOutCirc(double t) {
    return t < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * t, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * t + 2, 2)) + 1) / 2;
  }

  public static double easeInElastic(double t) {
    return Math.sin(13 * Math.PI / 2 * t) * Math.pow(2, 10 * (t - 1));
  }

  public static double easeOutElastic(double t) {
    return Math.sin(-13 * Math.PI / 2 * (t + 1)) * Math.pow(2, -10 * t) + 1;
  }

  public static double easeInOutElastic(double t) {
    return t < 0.5 ? Math.sin(13 * Math.PI / 2 * (2 * t)) * Math.pow(2, 10 * (2 * t - 1)) / 2
        : (Math.sin(-13 * Math.PI / 2 * (2 * t - 1)) * Math.pow(2, -10 * (2 * t - 1)) + 2) / 2;
  }

  public static double easeInBack(double t) {
    double c1 = 1.70158;
    double c3 = c1 + 1;
    return c3 * t * t * t - c1 * t * t;
  }

  public static double easeOutBack(double t) {
    double c1 = 1.70158;
    double c3 = c1 + 1;
    return 1 + c3 * Math.pow(t - 1, 3) + c1 * Math.pow(t - 1, 2);
  }

  public static double easeInOutBack(double t) {
    double c1 = 1.70158;
    double c2 = c1 * 1.525;
    return t < 0.5 ? (Math.pow(2 * t, 2) * ((c2 + 1) * 2 * t - c2)) / 2
        : (Math.pow(2 * t - 2, 2) * ((c2 + 1) * (t * 2 - 2) + c2) + 2) / 2;
  }

  public static double easeInBounce(double t) {
    return 1 - easeOutBounce(1 - t);
  }

  public static double easeOutBounce(double t) {
    if (t < 1 / 2.75) {
      return 7.5625 * t * t;
    } else if (t < 2 / 2.75) {
      return 7.5625 * (t -= 1.5 / 2.75) * t + 0.75;
    } else if (t < 2.5 / 2.75) {
      return 7.5625 * (t -= 2.25 / 2.75) * t + 0.9375;
    } else {
      return 7.5625 * (t -= 2.625 / 2.75) * t + 0.984375;
    }
  }

  public static double easeInOutBounce(double t) {
    return t < 0.5 ? (1 - easeOutBounce(1 - 2 * t)) / 2 : (1 + easeOutBounce(2 * t - 1)) / 2;
  }

  public static double easeInQuad(double t) {
    return t * t;
  }

  public static double easeOutQuad(double t) {
    return 1 - (1 - t) * (1 - t);
  }

  public static double easeInOutQuad(double t) {
    return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
  }

  public static double easeInQuart(double t) {
    return t * t * t * t;
  }

  public static double easeOutQuart(double t) {
    return 1 - Math.pow(1 - t, 4);
  }

  public static double easeInOutQuart(double t) {
    return t < 0.5 ? 8 * t * t * t * t : 1 - Math.pow(-2 * t + 2, 4) / 2;
  }
}
