package dev.rurino.hasugoods.block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Rarity;

public class ModBlocks {

  private static final List<BlockItem> ALL_BLOCKS_ITEMS = new ArrayList<>();

  public static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory,
      AbstractBlock.Settings settings, Settings itemSettings) {
    RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, Hasugoods.id(name));
    Block block = blockFactory.apply(settings.registryKey(blockKey));

    if (itemSettings != null) {
      RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Hasugoods.id(name));
      Hasugoods.LOGGER.info("Register block item: {}", itemKey.getValue());

      BlockItem blockItem = new BlockItem(block, itemSettings.registryKey(itemKey));
      ALL_BLOCKS_ITEMS.add(Registry.register(Registries.ITEM, itemKey, blockItem));
    }

    Hasugoods.LOGGER.info("Register block: {}", blockKey.getValue());
    return Registry.register(Registries.BLOCK, blockKey, block);
  }

  public static final Block NESO_BASE_BLOCK = register("neso_base_block", NesoBaseBlock::new,
      AbstractBlock.Settings.create()
          .sounds(BlockSoundGroup.STONE)
          .mapColor(MapColor.WHITE)
          .instrument(NoteBlockInstrument.BASEDRUM)
          .strength(1.8F)
          .luminance((state) -> 14),
      new Item.Settings()
          .rarity(Rarity.COMMON));
  public static final Block POSITION_ZERO_BLOCK = register("position_zero_block", PositionZeroBlock::new,
      AbstractBlock.Settings.create()
          .sounds(BlockSoundGroup.STONE)
          .mapColor(MapColor.WHITE)
          .instrument(NoteBlockInstrument.BELL)
          .strength(1.8F)
          .luminance((state) -> 15),
      new Item.Settings()
          .rarity(Rarity.RARE));

  public static void initialize() {
    NesoBaseBlock.initialize();
    PositionZeroBlock.initialize();

    ItemGroupEvents.modifyEntriesEvent(ModItems.HASU_ITEM_GROUP_KEY).register((itemGroup) -> {
      ALL_BLOCKS_ITEMS.forEach(itemGroup::add);
    });
  }
}
