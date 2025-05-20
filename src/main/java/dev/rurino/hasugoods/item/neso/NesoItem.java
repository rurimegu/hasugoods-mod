package dev.rurino.hasugoods.item.neso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.config.NesoConfig;
import dev.rurino.hasugoods.entity.NesoEntity;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import dev.rurino.hasugoods.util.HasuString;
import dev.rurino.hasugoods.util.ItemStackUtils;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class NesoItem extends Item implements INesoItem {
  // #region Static fields

  protected record NesoItemEntry(RegistryKey<Item> key, NesoItem item) {
  }

  protected static final Map<String, NesoItemEntry> ALL_NESOS = new HashMap<>();

  protected static Optional<NesoItemEntry> getNesoItemEntry(String charaKey, NesoSize size) {
    return Optional.ofNullable(ALL_NESOS.get(NesoConfig.nesoKey(charaKey, size)));
  }

  public static Optional<NesoItem> getNesoItem(String charaKey, NesoSize size) {
    return getNesoItemEntry(charaKey, size).map(NesoItemEntry::item);
  }

  public static Optional<RegistryKey<Item>> getNesoItemKey(String charaKey, NesoSize size) {
    return getNesoItemEntry(charaKey, size).map(NesoItemEntry::key);
  }

  public static List<NesoItem> getAllNesos(NesoSize size) {
    return ALL_NESOS.values().stream()
        .map(NesoItemEntry::item)
        .filter(item -> item.nesoSize == size).toList();
  }

  public static List<NesoItem> getAllNesos() {
    return ALL_NESOS.values().stream().map(NesoItemEntry::item).toList();
  }

  private static NesoItem create(Settings setting, String charaKey, NesoSize size) {
    return switch (charaKey) {
      case CharaUtils.KAHO_KEY -> new KahoNesoItem(setting, size);
      case CharaUtils.RURINO_KEY -> new RurinoNesoItem(setting, size);
      case CharaUtils.MEGUMI_KEY -> new MegumiNesoItem(setting, size);
      default -> new NesoItem(setting, charaKey, size);
    };
  }

  private static NesoItem registerNeso(String charaKey, NesoSize size) {
    String itemKey = NesoConfig.nesoKey(charaKey, size);
    RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Hasugoods.id(itemKey));
    Rarity rarity = switch (size) {
      case SMALL -> Rarity.COMMON;
      case MEDIUM -> Rarity.UNCOMMON;
      case LARGE -> Rarity.RARE;
    };
    Settings settings = new Settings().maxCount(1).rarity(rarity);
    NesoConfig.Base config = Hasugoods.CONFIG.neso.getConfig(charaKey, size);
    if (config == null) {
      Hasugoods.LOGGER.error("Config for {}, {} not found", charaKey, size);
    } else {
      // TODO: add item cooldown
    }
    NesoItem item = (NesoItem) ModItems.register(key, create(settings, charaKey, size));
    ALL_NESOS.put(itemKey, new NesoItemEntry(key, item));
    return item;
  }

  public static void initialize() {
    for (String charaKey : CharaUtils.ALL_CHARA_KEYS) {
      for (NesoSize size : NesoSize.values()) {
        registerNeso(charaKey, size);
      }
    }
  }

  // #endregion Static fields

  // #region Data component
  public static final ComponentType<KahoNesoComponent> KAHO_NESO_COMPONENT = Registry.register(
      Registries.DATA_COMPONENT_TYPE,
      Hasugoods.id("kaho_neso"),
      ComponentType.<KahoNesoComponent>builder().codec(KahoNesoComponent.CODEC).build());

  // #endregion Data component
  private final String charaKey;
  private final NesoSize nesoSize;
  protected final NesoConfig.Base config;

  public NesoItem(Settings settings, String charaKey, NesoSize size) {
    super(settings);
    this.charaKey = charaKey;
    this.nesoSize = size;
    this.config = Hasugoods.CONFIG.neso.getConfig(charaKey, size);
  }

  @Override
  public String getCharaKey() {
    return charaKey;
  }

  @Override
  public NesoSize getNesoSize() {
    return nesoSize;
  }

  @Override
  public NesoConfig.Base getConfig() {
    return config;
  }

  // #region GUI

  @Override
  public int getItemBarColor(ItemStack stack) {
    if (!(stack.getItem() instanceof NesoItem item)) {
      Hasugoods.LOGGER.error("getItemBarColor called on non-neso ItemStack {}", stack);
      return 0xFFFFFF;
    }
    return CharaUtils.getCharaColor(item.getCharaKey());
  }

  @Override
  public boolean isItemBarVisible(ItemStack stack) {
    return getStoredEnergy(stack) < getEnergyCapacity(stack);
  }

  @Override
  public int getItemBarStep(ItemStack stack) {
    long capacity = getEnergyCapacity(stack);
    if (capacity <= 0) {
      return ItemStackUtils.MAX_ITEM_BAR_STEPS;
    }
    return ItemStackUtils.getItemBarStep((float) getStoredEnergy(stack) / capacity);
  }

  @Override
  public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
    super.appendTooltip(stack, context, tooltip, type);
    long stored = getStoredEnergy(stack);
    long capacity = getEnergyCapacity(stack);
    tooltip.add(HasuString.formatEnergyTooltip(stored, capacity));
  }

  // #endregion GUI

  @Override
  public void onCraft(ItemStack stack, World world) {
    super.onCraft(stack, world);
    setFullEnergy(stack);
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    if (!context.getPlayer().isSneaking()) {
      return ActionResult.PASS;
    }
    // Place the neso entity on the block if the top block is not solid
    World world = context.getWorld();
    if (world.isClient)
      return ActionResult.CONSUME;

    ItemStack itemStack = context.getStack();
    BlockPos blockPos = context.getBlockPos();
    Direction direction = context.getSide();
    BlockState blockState = world.getBlockState(blockPos);
    EntityType<NesoEntity> entityType = getEntityType();

    BlockPos blockPos2;
    if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
      blockPos2 = blockPos;
    } else {
      blockPos2 = blockPos.offset(direction);
    }
    if (!world.isSpaceEmpty(entityType.getSpawnBox(blockPos2.getX() + 0.5, blockPos2.getY(), blockPos2.getZ() + 0.5))) {
      return ActionResult.PASS;
    }

    NesoEntity entity = NesoEntity.spawnFromItemStack(
        entityType,
        (ServerWorld) world,
        itemStack, context.getPlayer(),
        blockPos2,
        SpawnReason.MOB_SUMMONED,
        true,
        !Objects.equals(blockPos, blockPos2) && direction == Direction.UP,
        context.getPlayer().getYaw());
    if (entity == null) {
      return ActionResult.PASS;
    }

    itemStack.decrement(1);
    world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);

    return ActionResult.SUCCESS;
  }

  // #region Energy

  @Override
  public long getEnergyCapacity(ItemStack stack) {
    return config == null ? 0 : config.maxEnergy();
  }

  @Override
  public long getEnergyMaxInput(ItemStack stack) {
    return 0;
  }

  @Override
  public long getEnergyMaxOutput(ItemStack stack) {
    return 0;
  }

  // #endregion Energy

}
