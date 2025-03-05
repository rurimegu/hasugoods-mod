package dev.rurino.hasugoods.item;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.OshiUtils;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

public class ModItems {
  public static final RegistryKey<ItemGroup> BADGE_ITEM_GROUP_KEY = RegistryKey.of(
      Registries.ITEM_GROUP.getKey(), Hasugoods.id("item_group"));
  public static final ItemGroup BADGE_ITEM_GROUP = FabricItemGroup.builder()
      .icon(() -> new ItemStack(BadgeItem.getBadgeItem(OshiUtils.RURINO_KEY).get()))
      .displayName(Text.translatable("itemGroup.hasugoods"))
      .build();

  public static Item register(RegistryKey<Item> registryKey, Item item) {
    Hasugoods.LOGGER.info("Register item: " + registryKey.getValue());
    Item registeredItem = Registry.register(Registries.ITEM, registryKey.getValue(), item);
    return registeredItem;
  }

  public static void initialize() {
    Registry.register(Registries.ITEM_GROUP, BADGE_ITEM_GROUP_KEY, BADGE_ITEM_GROUP);
    BadgeItem.initialize();
    NesoItem.initialize();
  }
}
