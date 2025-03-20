package dev.rurino.hasugoods.component;

import org.apache.commons.lang3.StringUtils;

import dev.rurino.hasugoods.util.CollectionUtils;
import dev.rurino.hasugoods.util.CharaUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class OshiComponent implements IOshiComponent {
  private static final String NBT_OSHI = "oshiKey";
  private String oshiKey;

  public OshiComponent(LivingEntity provider) {
    this.oshiKey = CollectionUtils.getRandomElement(CharaUtils.ALL_CHARA_KEYS, provider.getRandom());
  }

  @Override
  public String getOshiKey() {
    return this.oshiKey;
  }

  @Override
  public void readFromNbt(NbtCompound tag, WrapperLookup registryLookup) {
    oshiKey = tag.getString(NBT_OSHI);
  }

  @Override
  public void writeToNbt(NbtCompound tag, WrapperLookup registryLookup) {
    if (StringUtils.isEmpty(oshiKey)) {
      tag.remove(NBT_OSHI);
    } else {
      tag.putString(NBT_OSHI, oshiKey);
    }
  }
}
