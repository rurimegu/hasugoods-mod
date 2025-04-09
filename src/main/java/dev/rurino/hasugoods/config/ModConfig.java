package dev.rurino.hasugoods.config;

import dev.rurino.hasugoods.Hasugoods;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfig {
  public static final ModConfig V;
  public static final ModConfigSpec SPEC;

  static {
    var pair = new ModConfigSpec.Builder().configure(ModConfig::new);
    V = pair.getLeft();
    SPEC = pair.getRight();
    NeoForgeConfigRegistry.INSTANCE.register(Hasugoods.MOD_ID, net.neoforged.fml.config.ModConfig.Type.COMMON, SPEC);
  }

  public final OshiProtectionConfig oshiProtection;
  public final BuyConfig buy;
  public final SellConfig sell;
  public final TradeConfig trade;
  public final LootConfig loot;
  public final NesoConfig neso;

  private ModConfig(ModConfigSpec.Builder builder) {
    oshiProtection = new OshiProtectionConfig(builder);
    buy = new BuyConfig(builder);
    sell = new SellConfig(builder);
    trade = new TradeConfig(builder);
    loot = new LootConfig(builder);
    neso = new NesoConfig(builder);
  }
}
