package dev.rurino.hasugoods.item.badge;

import java.util.List;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.util.ItemStackUtils;
import dev.rurino.hasugoods.util.config.HcVal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class UnopenedBadge extends Item {
  private static final HcVal.Int REGULAR_BADGE_DROP_WEIGHT = Hasugoods.CONFIG.getInt("regularBadgeDropWeight", 95);
  private static final HcVal.Int SECRET_BADGE_DROP_WEIGHT = Hasugoods.CONFIG.getInt("secretBadgeDropWeight", 5);

  private static final LootTable BADGE_LOOT_TABLE = LootTable.builder().type(LootContextTypes.EMPTY).pool(
      LootPool.builder()
          .with(TagEntry.expandBuilder(ModItems.REGULAR_BADGE_TAG).weight(REGULAR_BADGE_DROP_WEIGHT.val()))
          .with(TagEntry.expandBuilder(ModItems.SECRET_BADGE_TAG).weight(SECRET_BADGE_DROP_WEIGHT.val()))
          .build())
      .build();

  public UnopenedBadge(Settings settings) {
    super(settings);
  }

  @Override
  public ActionResult use(World world, PlayerEntity user, Hand hand) {
    if (world.isClient)
      return super.use(world, user, hand);
    ServerWorld serverWorld = (ServerWorld) world;
    List<ItemStack> stacks = BADGE_LOOT_TABLE
        .generateLoot(new LootWorldContext.Builder(serverWorld).build(LootContextTypes.EMPTY));
    if (stacks.isEmpty())
      return ActionResult.FAIL;
    ItemStack handStack = user.getStackInHand(hand);
    handStack.decrement(1);
    for (ItemStack stack : stacks) {
      ItemStackUtils.giveItemsToPlayerOrDrop(user, stack);
    }
    return ActionResult.SUCCESS;
  }
}
