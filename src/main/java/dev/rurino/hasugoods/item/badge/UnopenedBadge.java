package dev.rurino.hasugoods.item.badge;

import java.util.List;

import dev.rurino.hasugoods.Hasugoods;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.context.LootWorldContext;
import net.minecraft.loot.entry.TagEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class UnopenedBadge extends Item {

  public static final RegistryKey<LootTable> UNOPENED_BADGE_LOOT_TABLE = RegistryKey.of(RegistryKeys.LOOT_TABLE,
      Hasugoods.id("item/unopened_badge"));

  public UnopenedBadge(Settings settings) {
    super(settings);
  }

  @Override
  public ActionResult use(World world, PlayerEntity user, Hand hand) {
    if (world.isClient)
      return super.use(world, user, hand);
    ServerWorld serverWorld = (ServerWorld) world;
    LootTable supplier = LootTable.builder().type(LootContextTypes.EMPTY).pool(
        LootPool.builder()
            .with(TagEntry.expandBuilder(BadgeItem.REGULAR_BADGE_TAG).weight(Hasugoods.CONFIG.regularBadgeDropWeight()))
            .with(TagEntry.expandBuilder(BadgeItem.SECRET_BADGE_TAG).weight(Hasugoods.CONFIG.secretBadgeDropWeight()))
            .build())
        .build();
    List<ItemStack> stacks = supplier
        .generateLoot(new LootWorldContext.Builder(serverWorld).build(LootContextTypes.EMPTY));
    if (stacks.isEmpty())
      return ActionResult.FAIL;
    for (ItemStack stack : stacks) {
      user.giveItemStack(stack);
    }
    ItemStack handStack = user.getStackInHand(hand);
    handStack.decrement(1);
    return ActionResult.SUCCESS;
  }
}
