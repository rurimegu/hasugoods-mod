package dev.rurino.hasugoods;

import com.google.common.collect.ImmutableList;

import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKey;

public class ModConstants {
  public static final float TOUTOSHI_DAMAGE_AMOUNT = 1000f;
  public static final int TOUTOSHI_EFFECT_DURATION = 2 * 20;

  public static final ImmutableList<RegistryKey<LootTable>> BADGE_LOOT_TABLES = ImmutableList.of(
      LootTables.VILLAGE_ARMORER_CHEST,
      LootTables.VILLAGE_BUTCHER_CHEST,
      LootTables.VILLAGE_CARTOGRAPHER_CHEST,
      LootTables.VILLAGE_DESERT_HOUSE_CHEST,
      LootTables.VILLAGE_FISHER_CHEST,
      LootTables.VILLAGE_FLETCHER_CHEST,
      LootTables.VILLAGE_MASON_CHEST,
      LootTables.VILLAGE_PLAINS_CHEST,
      LootTables.VILLAGE_SAVANNA_HOUSE_CHEST,
      LootTables.VILLAGE_SHEPARD_CHEST,
      LootTables.VILLAGE_SNOWY_HOUSE_CHEST,
      LootTables.VILLAGE_TAIGA_HOUSE_CHEST,
      LootTables.VILLAGE_TANNERY_CHEST,
      LootTables.VILLAGE_TEMPLE_CHEST,
      LootTables.VILLAGE_TOOLSMITH_CHEST,
      LootTables.VILLAGE_WEAPONSMITH_CHEST);
}
