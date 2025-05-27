package dev.rurino.hasugoods.item;

import java.util.ArrayList;
import java.util.List;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.CharaUtils;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItems {
  public static final RegistryKey<ItemGroup> HASU_ITEM_GROUP_KEY = RegistryKey.of(
      Registries.ITEM_GROUP.getKey(), Hasugoods.id("item_group"));
  public static final Identifier ID_PATCHOULI_BOOK = Hasugoods.id("hasunosora_handbook");
  public static final ItemGroup HASU_ITEM_GROUP = FabricItemGroup.builder()
      .icon(() -> new ItemStack(BadgeItem.getBadgeItem(CharaUtils.RURINO_KEY).get()))
      .displayName(Text.translatable("itemGroup.hasugoods"))
      .build();

  private static final List<Item> ALL_ITEMS_IN_GROUP = new ArrayList<>();

  public static final TagKey<Item> TAG_REGULAR_BADGES = TagKey.of(RegistryKeys.ITEM, Hasugoods.id("regular_badges"));
  public static final TagKey<Item> TAG_SECRET_BADGES = TagKey.of(RegistryKeys.ITEM, Hasugoods.id("secret_badges"));
  public static final TagKey<Item> TAG_SMALL_NESOS = TagKey.of(RegistryKeys.ITEM, Hasugoods.id("small_nesos"));
  public static final TagKey<Item> TAG_MEDIUM_NESOS = TagKey.of(RegistryKeys.ITEM, Hasugoods.id("medium_nesos"));
  public static final TagKey<Item> TAG_LARGE_NESOS = TagKey.of(RegistryKeys.ITEM, Hasugoods.id("large_nesos"));
  public static final TagKey<Item> TAG_NESOS = TagKey.of(RegistryKeys.ITEM, Hasugoods.id("nesos"));

  public static Item register(RegistryKey<Item> registryKey, Item item, boolean registerInGroup) {
    Hasugoods.LOGGER.debug("Register item: " + registryKey.getValue());
    Item registeredItem = Registry.register(Registries.ITEM, registryKey.getValue(), item);
    if (registerInGroup) {
      ALL_ITEMS_IN_GROUP.add(registeredItem);
    }
    return registeredItem;
  }

  public static Item register(RegistryKey<Item> registryKey, Item item) {
    return register(registryKey, item, true);
  }

  public static void initialize() {
    Registry.register(Registries.ITEM_GROUP, HASU_ITEM_GROUP_KEY, HASU_ITEM_GROUP);
    BadgeItem.initialize();
    NesoItem.initialize();
    ItemGroupEvents.modifyEntriesEvent(ModItems.HASU_ITEM_GROUP_KEY).register(itemGroup -> {
      ALL_ITEMS_IN_GROUP.forEach(itemGroup::add);
    });
  }
}
