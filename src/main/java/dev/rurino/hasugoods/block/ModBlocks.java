package dev.rurino.hasugoods.block;

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
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

public class ModBlocks {

  public static Block register(String name, Function<AbstractBlock.Settings, Block> blockFactory,
      AbstractBlock.Settings settings, boolean shouldRegisterItem) {
    RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, Hasugoods.id(name));
    Block block = blockFactory.apply(settings.registryKey(blockKey));

    if (shouldRegisterItem) {
      RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Hasugoods.id(name));
      Hasugoods.LOGGER.info("Register block item: {}", itemKey.getValue());

      BlockItem blockItem = new BlockItem(block, new Item.Settings().registryKey(itemKey));
      Registry.register(Registries.ITEM, itemKey, blockItem);
    }

    Hasugoods.LOGGER.info("Register block: {}", blockKey.getValue());
    return Registry.register(Registries.BLOCK, blockKey, block);
  }

  public static final Block NESO_BASE_BLOCK = register("neso_base_block", Block::new,
      AbstractBlock.Settings.create()
          .sounds(BlockSoundGroup.STONE)
          .mapColor(MapColor.WHITE)
          .instrument(NoteBlockInstrument.BASEDRUM)
          .strength(1.8F)
          .luminance((state) -> 14),
      true);
  public static final Block POSITION_ZERO_BLOCK = register("position_zero_block", Block::new,
      AbstractBlock.Settings.create()
          .sounds(BlockSoundGroup.STONE)
          .mapColor(MapColor.WHITE)
          .instrument(NoteBlockInstrument.BELL)
          .strength(1.8F)
          .luminance((state) -> 15),
      true);

  public static void initialize() {
    ItemGroupEvents.modifyEntriesEvent(ModItems.BADGE_ITEM_GROUP_KEY).register((itemGroup) -> {
      itemGroup.add(NESO_BASE_BLOCK.asItem());
      itemGroup.add(POSITION_ZERO_BLOCK.asItem());
    });
  }
}
