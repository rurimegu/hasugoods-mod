package dev.rurino.hasugoods.component;

import org.apache.commons.lang3.StringUtils;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.CharaItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.Identifier;

public class ToutoshiComponent implements IToutoshiComponent {

  private static final String TOUTOSHI_SOURCE_ITEM = "toutoshiSourceItem";
  private final LivingEntity provider;
  private Item toutoshiSourceItem;

  public ToutoshiComponent(LivingEntity provider) {
    this.provider = provider;
  }

  @Override
  public Item getToutoshiSourceItem() {
    return toutoshiSourceItem;
  }

  @Override
  public void setToutoshiSourceItem(Item item) {
    toutoshiSourceItem = item;
    ModComponents.TOUTOSHI.sync(provider);
  }

  @Override
  public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
    String itemKey = tag.getString(TOUTOSHI_SOURCE_ITEM);
    if (StringUtils.isEmpty(itemKey)) {
      toutoshiSourceItem = null;
      return;
    }
    RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(tag.getString(TOUTOSHI_SOURCE_ITEM)));
    Item item = registryLookup.getOrThrow(RegistryKeys.ITEM).getOrThrow(key).value();
    if (item instanceof CharaItem) {
      toutoshiSourceItem = (CharaItem) item;
    } else {
      Hasugoods.LOGGER.warn("Toutoshi source item is not a CharaItem: {}", item);
      toutoshiSourceItem = null;
    }
  }

  @Override
  public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
    if (toutoshiSourceItem == null) {
      tag.remove(TOUTOSHI_SOURCE_ITEM);
      return;
    }
    RegistryKey<Item> key = Registries.ITEM.getKey(toutoshiSourceItem).get();
    tag.putString(TOUTOSHI_SOURCE_ITEM, key.getValue().toString());
  }

}
