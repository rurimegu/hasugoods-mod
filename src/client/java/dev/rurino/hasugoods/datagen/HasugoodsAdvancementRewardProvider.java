package dev.rurino.hasugoods.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetComponentsLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import vazkii.patchouli.common.item.PatchouliDataComponents;
import vazkii.patchouli.common.item.PatchouliItems;

public class HasugoodsAdvancementRewardProvider extends SimpleFabricLootTableProvider {

  public static final RegistryKey<LootTable> ROOT = RegistryKey.of(RegistryKeys.LOOT_TABLE,
      Hasugoods.id("advancement/root"));

  public HasugoodsAdvancementRewardProvider(FabricDataOutput dataOutput,
      CompletableFuture<WrapperLookup> registryLookup) {
    super(dataOutput, registryLookup, LootContextTypes.ADVANCEMENT_REWARD);
  }

  private void acceptRoot(BiConsumer<RegistryKey<LootTable>, Builder> lootTableBiConsumer) {
    lootTableBiConsumer.accept(ROOT, LootTable.builder().pool(
        LootPool.builder()
            .rolls(ConstantLootNumberProvider.create(1.0F))
            .with(ItemEntry.builder(PatchouliItems.BOOK)
                .apply(SetComponentsLootFunction.builder(PatchouliDataComponents.BOOK, ModItems.ID_PATCHOULI_BOOK)))));
  }

  @Override
  public void accept(BiConsumer<RegistryKey<LootTable>, Builder> lootTableBiConsumer) {
    acceptRoot(lootTableBiConsumer);
  }

}
