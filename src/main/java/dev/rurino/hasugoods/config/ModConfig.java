package dev.rurino.hasugoods.config;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.config.HcObj;
import dev.rurino.hasugoods.util.config.HcRoot;

public class ModConfig {
  public static final HcRoot ROOT = HcRoot
      .createAndLoad(Hasugoods.MOD_ID + "-config")
      .autoCreateConfig()
      .autoOverwriteConfig();
  public static final HcObj OSHI_PROTECTION = ROOT.child("oshiProtection");
  public static final HcObj BUY = ROOT.child("buy");
  public static final HcObj SELL = ROOT.child("sell");
  public static final HcObj TRADE = ROOT.child("trade");
  public static final HcObj LOOT = ROOT.child("loot");

  public static void onInitializateComplete() {
    ROOT.onInitializateComplete();
  }
}
