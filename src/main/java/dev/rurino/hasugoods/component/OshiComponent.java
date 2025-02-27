package dev.rurino.hasugoods.component;

import org.apache.commons.lang3.StringUtils;

import dev.rurino.hasugoods.item.OshiItem;
import dev.rurino.hasugoods.util.CollectionUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class OshiComponent implements IOshiComponent {
  private static final String OSHI_KEY = "oshi_key";
  private final LivingEntity provider;
  private String oshiKey;

  public OshiComponent(LivingEntity provider) {
    this.provider = provider;
    this.oshiKey = CollectionUtils.getRandomElement(OshiItem.ALL_OSHI_KEYS, provider.getRandom());
  }

  @Override
  public String getOshiKey() {
    return this.oshiKey;
  }

  @Override
  public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
    oshiKey = tag.getString(OSHI_KEY);
  }

  @Override
  public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
    if (StringUtils.isEmpty(oshiKey)) {
      tag.remove(OSHI_KEY);
    } else {
      tag.putString(OSHI_KEY, oshiKey);
    }
  }
}
