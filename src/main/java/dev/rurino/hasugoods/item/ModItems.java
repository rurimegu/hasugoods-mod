package dev.rurino.hasugoods.item;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import dev.rurino.hasugoods.item.badge.BoxOfBadgeItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

public class ModItems {
  public static final RegistryKey<ItemGroup> BADGE_ITEM_GROUP_KEY = RegistryKey.of(
      Registries.ITEM_GROUP.getKey(), Hasugoods.id("item_group"));
  public static final ItemGroup BADGE_ITEM_GROUP = FabricItemGroup.builder()
      .icon(() -> new ItemStack(BadgeItem.RURINO_BADGE))
      .displayName(Text.translatable("itemGroup.hasugoods"))
      .build();

  public static final RegistryKey<Item> BOX_OF_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Hasugoods.id("box_of_badge"));
  public static final Item BOX_OF_BADGE = register(
      new BoxOfBadgeItem(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON)
          .registryKey(BOX_OF_BADGE_KEY)),
      BOX_OF_BADGE_KEY);

  public static Item register(Item item, RegistryKey<Item> registryKey) {
    Hasugoods.LOGGER.info("Register item: " + registryKey.getValue());
    Item registeredItem = Registry.register(Registries.ITEM, registryKey.getValue(), item);
    return registeredItem;
  }

  public static void initialize() {
    Registry.register(Registries.ITEM_GROUP, BADGE_ITEM_GROUP_KEY, BADGE_ITEM_GROUP);
    BadgeItem.initialize();
  }
}
