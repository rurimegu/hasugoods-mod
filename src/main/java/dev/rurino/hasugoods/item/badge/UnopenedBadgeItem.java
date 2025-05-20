package dev.rurino.hasugoods.item.badge;

import java.util.List;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.util.ItemStackUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class UnopenedBadgeItem extends Item {

  private static final IntValue REGULAR_BADGE_DROP_WEIGHT = Hasugoods.CONFIG.loot.regularBadgeDropWeight;
  private static final IntValue SECRET_BADGE_DROP_WEIGHT = Hasugoods.CONFIG.loot.secretBadgeDropWeight;

  private static LootTable createBadgeLootTable() {
    return LootTable.builder().type(LootContextTypes.EMPTY).pool(
        LootPool.builder()
            .with(TagEntry.expandBuilder(ModItems.TAG_REGULAR_BADGES).weight(REGULAR_BADGE_DROP_WEIGHT.get()))
            .with(TagEntry.expandBuilder(ModItems.TAG_SECRET_BADGES).weight(SECRET_BADGE_DROP_WEIGHT.get()))
            .build())
        .build();
  }

  public UnopenedBadgeItem(Settings settings) {
    super(settings);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    if (world.isClient)
      return super.use(world, user, hand);
    ServerWorld serverWorld = (ServerWorld) world;
    ItemStack handStack = user.getStackInHand(hand);
    List<ItemStack> stacks = createBadgeLootTable()
        .generateLoot(new LootContextParameterSet.Builder(serverWorld).build(LootContextTypes.EMPTY));
    if (stacks.isEmpty() || stacks.size() > 1) {
      Hasugoods.LOGGER.warn("Unopened badge loot table returned {} items, ignored", stacks.size());
      return TypedActionResult.fail(handStack);
    }
    ItemStack newStack = stacks.get(0);
    if (handStack.getCount() <= 1) {
      return ItemStackUtils.replaceItemsToPlayerOrDrop(user, newStack.getItem(), newStack.getCount());
    } else {
      ItemStackUtils.giveItemsToPlayerOrDrop(user, newStack);
      handStack.decrement(1);
      return TypedActionResult.success(handStack);
    }
  }
}
