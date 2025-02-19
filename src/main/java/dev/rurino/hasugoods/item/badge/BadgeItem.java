package dev.rurino.hasugoods.item.badge;

import java.util.List;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.ModItems;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DeathProtectionComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.ClearAllEffectsConsumeEffect;
import net.minecraft.item.consume.ConsumeEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class BadgeItem extends Item {

  public static final DeathProtectionComponent HASU_BADGE = new DeathProtectionComponent(
      List.<ConsumeEffect>of(new ClearAllEffectsConsumeEffect(),
          new ApplyEffectsConsumeEffect(List.of(new StatusEffectInstance(StatusEffects.RESISTANCE, 3 * 20, 5)))));

  public static final RegistryKey<Item> RURINO_BADGE_KEY = RegistryKey.of(RegistryKeys.ITEM,
      Identifier.of(Hasugoods.MOD_ID, "rurino_badge"));
  public static final Item RURINO_BADGE = ModItems.register(
      new BadgeItem(new Item.Settings().maxCount(16).rarity(Rarity.COMMON)
          .component(DataComponentTypes.DEATH_PROTECTION, HASU_BADGE)
          .registryKey(RURINO_BADGE_KEY)),
      RURINO_BADGE_KEY);

  public BadgeItem(Settings settings) {
    super(settings);
  }

  public static void initialize() {

    ItemGroupEvents.modifyEntriesEvent(ModItems.BADGE_ITEM_GROUP_KEY).register(itemGroup -> {
      itemGroup.add(RURINO_BADGE);
    });

  }
}
