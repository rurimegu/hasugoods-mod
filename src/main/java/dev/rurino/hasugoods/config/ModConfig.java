package dev.rurino.hasugoods.config;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.util.config.HcObj;
import dev.rurino.hasugoods.util.config.HcRoot;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
  public static final HcRoot ROOT = HcRoot
      .createAndLoad(Hasugoods.MOD_ID + "-config")
      .autoCreateConfig()
      .autoOverwriteConfig();
  public static final HcObj BUY = ROOT.child("buy");
  public static final HcObj SELL = ROOT.child("sell");
  public static final HcObj TRADE = ROOT.child("trade");
  public static final HcObj LOOT = ROOT.child("loot");

  public static final ModConfig V;
  public static final ModConfigSpec SPEC;

  static {
    var pair = new ModConfigSpec.Builder().configure(ModConfig::new);
    V = pair.getLeft();
    SPEC = pair.getRight();
    NeoForgeConfigRegistry.INSTANCE.register(Hasugoods.MOD_ID, net.neoforged.fml.config.ModConfig.Type.SERVER, SPEC);
  }

  public final OshiProtectionConfig oshiProtection;

  private ModConfig(ModConfigSpec.Builder builder) {
    oshiProtection = new OshiProtectionConfig(builder);
  }

  public static void onInitializateComplete() {
    ROOT.onInitializateComplete();
  }
}
