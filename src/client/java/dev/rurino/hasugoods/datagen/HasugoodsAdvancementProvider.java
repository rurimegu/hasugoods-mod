package dev.rurino.hasugoods.datagen;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.block.ModBlocks;
import dev.rurino.hasugoods.damage.ModDamageTypes;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.item.badge.BadgeItem;
import dev.rurino.hasugoods.item.neso.NesoItem;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.EntityHurtPlayerCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.predicate.DamagePredicate;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;

public class HasugoodsAdvancementProvider extends FabricAdvancementProvider {

  private static Text titlekey(String key) {
    return Text.translatable("advancement.hasugoods." + key + ".title");
  }

  private static Text descriptionKey(String key) {
    return Text.translatable("advancement.hasugoods." + key + ".description");
  }

  public HasugoodsAdvancementProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registryLookup) {
    super(output, registryLookup);
  }

  private AdvancementEntry createRoot(Consumer<AdvancementEntry> consumer) {
    return Advancement.Builder.create()
        .display(
            BadgeItem.getBadgeItem(CharaUtils.KAHO_KEY).get(),
            titlekey("root"),
            descriptionKey("root"),
            Hasugoods.id("textures/gui/advancement/background.png"),
            AdvancementFrame.TASK,
            false,
            false,
            false)
        .criterion("tick", TickCriterion.Conditions.createTick())
        .requirements(AdvancementRequirements.anyOf(List.of("tick")))
        .rewards(AdvancementRewards.Builder.loot(HasugoodsAdvancementRewardProvider.ROOT))
        .build(consumer, Hasugoods.idStr("root"));
  }

  private AdvancementEntry createObtainBadge(Consumer<AdvancementEntry> consumer, AdvancementEntry parent,
      boolean isSecret) {
    TagKey<Item> tag = isSecret ? ModItems.TAG_SECRET_BADGES : ModItems.TAG_REGULAR_BADGES;
    String advName = isSecret ? "obtain_secret_badge" : "obtain_regular_badge";
    int experience = isSecret ? 100 : 50;
    return Advancement.Builder.create().parent(parent)
        .display(
            BadgeItem.getBadgeItem(CharaUtils.KOSUZU_KEY, isSecret).get(),
            titlekey(advName),
            descriptionKey(advName),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false)
        .criterion("obtain_badge", InventoryChangedCriterion.Conditions
            .items(ItemPredicate.Builder.create().tag(tag).build()))
        .requirements(AdvancementRequirements.anyOf(List.of("obtain_badge")))
        .rewards(AdvancementRewards.Builder.experience(experience))
        .build(consumer, Hasugoods.idStr(advName));
  }

  private AdvancementEntry createObtainAllBadges(Consumer<AdvancementEntry> consumer, AdvancementEntry parent,
      boolean isSecret) {
    String advName = isSecret ? "obtain_all_secret_badges" : "obtain_all_regular_badges";
    int experience = isSecret ? 200 : 100;
    var builder = Advancement.Builder.create().parent(parent)
        .display(
            BadgeItem.getBadgeItem(CharaUtils.HIME_KEY, isSecret).get(),
            titlekey(advName),
            descriptionKey(advName),
            null,
            AdvancementFrame.GOAL,
            true,
            true,
            false);
    var allBadges = BadgeItem.getAllBadges(isSecret);
    for (BadgeItem badgeItem : allBadges) {
      builder = builder
          .criterion("obtain_" + badgeItem.getCharaKey() + "_badge",
              InventoryChangedCriterion.Conditions.items(badgeItem));
    }
    return builder
        .requirements(AdvancementRequirements.allOf(allBadges.stream()
            .map(badgeItem -> "obtain_" + badgeItem.getCharaKey() + "_badge").toList()))
        .rewards(AdvancementRewards.Builder.experience(experience))
        .build(consumer, Hasugoods.idStr(advName));
  }

  private AdvancementEntry createObtainNeso(Consumer<AdvancementEntry> consumer, AdvancementEntry parent,
      NesoSize size) {
    String advName = "obtain_" + size.name().toLowerCase() + "_neso";
    int experience = switch (size) {
      case SMALL -> 50;
      case MEDIUM -> 100;
      case LARGE -> 500;
    };
    TagKey<Item> tag = switch (size) {
      case SMALL -> ModItems.TAG_SMALL_NESOS;
      case MEDIUM -> ModItems.TAG_MEDIUM_NESOS;
      case LARGE -> ModItems.TAG_LARGE_NESOS;
    };
    return Advancement.Builder.create().parent(parent)
        .display(
            NesoItem.getNesoItem(CharaUtils.MEGUMI_KEY, size).get(),
            titlekey(advName),
            descriptionKey(advName),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false)
        .criterion("obtain_neso", InventoryChangedCriterion.Conditions
            .items(ItemPredicate.Builder.create().tag(tag).build()))
        .requirements(AdvancementRequirements.anyOf(List.of("obtain_neso")))
        .rewards(AdvancementRewards.Builder.experience(experience))
        .build(consumer, Hasugoods.idStr(advName));
  }

  private AdvancementEntry createObtainAllNesos(Consumer<AdvancementEntry> consumer, AdvancementEntry parent,
      NesoSize size) {
    String advName = "obtain_all_" + size.name().toLowerCase() + "_nesos";
    int experience = switch (size) {
      case SMALL -> 100;
      case MEDIUM -> 200;
      case LARGE -> 1000;
    };
    var builder = Advancement.Builder.create().parent(parent)
        .display(
            NesoItem.getNesoItem(CharaUtils.KOZUE_KEY, size).get(),
            titlekey(advName),
            descriptionKey(advName),
            null,
            AdvancementFrame.GOAL,
            true,
            true,
            false);
    var allNesos = NesoItem.getAllNesos(size);
    for (NesoItem nesoItem : allNesos) {
      builder = builder
          .criterion("obtain_" + CharaUtils.nesoKey(nesoItem.getCharaKey(), size),
              InventoryChangedCriterion.Conditions.items(nesoItem));
    }
    return builder
        .requirements(AdvancementRequirements.allOf(allNesos.stream()
            .map(nesoItem -> "obtain_" + CharaUtils.nesoKey(nesoItem.getCharaKey(), size))
            .toList()))
        .rewards(AdvancementRewards.Builder.experience(experience))
        .build(consumer, Hasugoods.idStr(advName));
  }

  private AdvancementEntry createToutoshi(Consumer<AdvancementEntry> consumer, AdvancementEntry parent) {
    return Advancement.Builder.create().parent(parent)
        .display(
            BadgeItem.getBadgeItem(CharaUtils.MEGUMI_KEY).get(),
            titlekey("toutoshi"),
            descriptionKey("toutoshi"),
            null,
            AdvancementFrame.CHALLENGE,
            true,
            true,
            true)
        .criterion("toutoshi", EntityHurtPlayerCriterion.Conditions.create(
            DamagePredicate.Builder.create()
                .type(DamageSourcePredicate.Builder.create().tag(TagPredicate.expected(
                    ModDamageTypes.TAG_TOUTOSHI)))
                .blocked(false)))
        .requirements(AdvancementRequirements.anyOf(List.of("toutoshi")))
        .rewards(AdvancementRewards.Builder.experience(50))
        .build(consumer, Hasugoods.idStr("toutoshi"));
  }

  private AdvancementEntry createObtainNesoBase(Consumer<AdvancementEntry> consumer, AdvancementEntry parent,
      boolean pos0) {
    String advName = pos0 ? "obtain_position_zero_block" : "obtain_neso_base";
    Block block = pos0 ? ModBlocks.POSITION_ZERO_BLOCK : ModBlocks.NESO_BASE_BLOCK;
    int experience = pos0 ? 100 : 50;
    return Advancement.Builder.create().parent(parent)
        .display(
            block,
            titlekey(advName),
            descriptionKey(advName),
            null,
            AdvancementFrame.TASK,
            true,
            true,
            false)
        .criterion("obtain_neso_base", InventoryChangedCriterion.Conditions.items(block))
        .requirements(AdvancementRequirements.anyOf(List.of("obtain_neso_base")))
        .rewards(AdvancementRewards.Builder.experience(experience))
        .build(consumer, Hasugoods.idStr(advName));
  }

  @SuppressWarnings("unused")
  @Override
  public void generateAdvancement(WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
    AdvancementEntry root = createRoot(consumer);
    AdvancementEntry obtainRegularBadge = createObtainBadge(consumer, root, false);
    AdvancementEntry obtainAllRegularBadges = createObtainAllBadges(consumer, obtainRegularBadge, false);
    AdvancementEntry obtainSecretBadge = createObtainBadge(consumer, obtainRegularBadge, true);
    AdvancementEntry obtainAllSecretBadges = createObtainAllBadges(consumer, obtainSecretBadge, true);
    AdvancementEntry toutoshi = createToutoshi(consumer, obtainRegularBadge);
    AdvancementEntry obtainSmallNeso = createObtainNeso(consumer, obtainRegularBadge, NesoSize.SMALL);
    AdvancementEntry obtainAllSmallNesos = createObtainAllNesos(consumer, obtainSmallNeso, NesoSize.SMALL);
    AdvancementEntry obtainMediumNeso = createObtainNeso(consumer, obtainSmallNeso, NesoSize.MEDIUM);
    AdvancementEntry obtainAllMediumNesos = createObtainAllNesos(consumer, obtainMediumNeso, NesoSize.MEDIUM);
    AdvancementEntry obtainLargeNeso = createObtainNeso(consumer, obtainAllMediumNesos, NesoSize.LARGE);
    AdvancementEntry obtainAllLargeNesos = createObtainAllNesos(consumer, obtainLargeNeso, NesoSize.LARGE);
    AdvancementEntry obtainNesoBase = createObtainNesoBase(consumer, obtainRegularBadge, false);
    AdvancementEntry obtainPositionZeroBlock = createObtainNesoBase(consumer, obtainSecretBadge, true);
  }

}
