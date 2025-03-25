package dev.rurino.hasugoods.item.neso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import dev.rurino.hasugoods.Hasugoods;
import dev.rurino.hasugoods.entity.NesoEntity;
import dev.rurino.hasugoods.item.ModItems;
import dev.rurino.hasugoods.item.CharaItem;
import dev.rurino.hasugoods.util.CharaUtils.NesoSize;
import dev.rurino.hasugoods.util.config.HcObj;
import dev.rurino.hasugoods.util.CharaUtils;
import dev.rurino.hasugoods.util.ItemStackUtils;
import dev.rurino.hasugoods.util.HasuString;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
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
import team.reborn.energy.api.base.SimpleEnergyItem;

public class NesoItem extends CharaItem implements SimpleEnergyItem {
  // #region Static fields

  protected static record NesoItemEntry(RegistryKey<Item> key, NesoItem item) {
  }

  protected static final Map<String, NesoItemEntry> ALL_NESOS = new HashMap<>();

  public static String nesoKey(String charaKey, NesoSize size) {
    return charaKey + "_neso_" + size.name().toLowerCase();
  }

  protected static Optional<NesoItemEntry> getNesoItemEntry(String charaKey, NesoSize size) {
    return Optional.ofNullable(ALL_NESOS.get(nesoKey(charaKey, size)));
  }

  public static Optional<NesoItem> getNesoItem(String charaKey, NesoSize size) {
    return getNesoItemEntry(charaKey, size).map(neso -> neso.item());
  }

  public static Optional<RegistryKey<Item>> getNesoItemKey(String charaKey, NesoSize size) {
    return getNesoItemEntry(charaKey, size).map(neso -> neso.key());
  }

  public static List<NesoItem> getAllNesos(NesoSize size) {
    return ALL_NESOS.values().stream()
        .filter(entry -> entry.item().nesoSize == size)
        .map(entry -> entry.item()).toList();
  }

  public static List<NesoItem> getAllNesos() {
    return ALL_NESOS.values().stream().map(entry -> entry.item()).toList();
  }

  private static NesoItem create(Settings setting, String charaKey, NesoSize size) {
    return switch (charaKey) {
      case CharaUtils.KAHO_KEY -> new KahoNesoItem(setting, size);
      case CharaUtils.RURINO_KEY -> new RurinoNesoItem(setting, size);
      default -> new NesoItem(setting, charaKey, size);
    };
  }

  private static NesoItem registerNeso(String charaKey, NesoSize size) {
    String itemKey = nesoKey(charaKey, size);
    RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Hasugoods.id(itemKey));
    Rarity rarity = switch (size) {
      case SMALL -> Rarity.COMMON;
      case MEDIUM -> Rarity.UNCOMMON;
      case LARGE -> Rarity.RARE;
    };
    Settings settings = new Settings().maxCount(1).rarity(rarity).registryKey(key);
    Config config = getConfig(charaKey, size);
    if (config == null) {
      Hasugoods.LOGGER.warn("Config for {}, {} not found", charaKey, size);
    } else {
      settings = settings.useCooldown(config.useCooldown());
    }
    NesoItem item = (NesoItem) ModItems.register(key, create(settings, charaKey, size));
    ALL_NESOS.put(itemKey, new NesoItemEntry(key, item));
    return item;
  }

  public static void initialize() {
    for (NesoSize size : NesoSize.values()) {
      registerConfig(CharaUtils.RURINO_KEY, size, new RurinoNesoItem.Config(size));
      registerConfig(CharaUtils.KAHO_KEY, size, new KahoNesoItem.Config(size));
    }
    for (String charaKey : CharaUtils.ALL_CHARA_KEYS) {
      for (NesoSize size : NesoSize.values()) {
        registerNeso(charaKey, size);
      }
    }
    ItemGroupEvents.modifyEntriesEvent(ModItems.HASU_ITEM_GROUP_KEY).register((itemGroup) -> {
      for (NesoItemEntry entry : ALL_NESOS.values()) {
        itemGroup.add(entry.item());
      }
    });
  }

  // #endregion Static fields

  // #region Config
  public static final HcObj HC_NESO = Hasugoods.CONFIG.child("neso");
  protected static final HcObj HC_SMALL = HC_NESO.child("small");
  protected static final HcObj HC_MEDIUM = HC_NESO.child("medium");
  protected static final HcObj HC_LARGE = HC_NESO.child("large");

  public static abstract class Config {
    private final long maxEnergy;

    protected Config(NesoSize size) {
      this.maxEnergy = switch (size) {
        case SMALL -> HC_SMALL.getLong("maxEnergy", 128 * 1000).val();
        case MEDIUM -> HC_MEDIUM.getLong("maxEnergy", 1000 * 1000).val();
        case LARGE -> HC_LARGE.getLong("maxEnergy", 16 * 1000 * 1000).val();
      };
    }

    public long maxEnergy() {
      return maxEnergy;
    }

    public abstract long energyPerAction();

    public abstract float useCooldown();
  }

  private static Map<String, Config> CONFIGS = new HashMap<>();

  private static void registerConfig(String charaKey, NesoSize size, Config config) {
    String key = nesoKey(charaKey, size);
    if (CONFIGS.containsKey(key)) {
      Hasugoods.LOGGER.warn("Config for {} already registered, replacing", key);
    }
    CONFIGS.put(key, config);
  }

  public static Config getConfig(String charaKey, NesoSize size) {
    String key = nesoKey(charaKey, size);
    if (!CONFIGS.containsKey(key)) {
      Hasugoods.LOGGER.error("Config for {} not found", key);
      return null;
    }
    return CONFIGS.get(key);
  }

  // #endregion Config

  // #region Data component
  public static final ComponentType<KahoNesoComponent> KAHO_NESO_COMPONENT = Registry.register(
      Registries.DATA_COMPONENT_TYPE,
      Hasugoods.id("kaho_neso"),
      ComponentType.<KahoNesoComponent>builder().codec(KahoNesoComponent.CODEC).build());

  // #endregion Data component
  protected final NesoSize nesoSize;
  protected final Config config;

  public NesoItem(Settings settings, String charaKey, NesoSize size) {
    super(settings, charaKey);
    this.nesoSize = size;
    this.config = getConfig(this.getCharaKey(), size);
  }

  public NesoSize getNesoSize() {
    return nesoSize;
  }

  public EntityType<NesoEntity> getEntityType() {
    String charaKey = getCharaKey();
    Optional<EntityType<NesoEntity>> type = NesoEntity.getNesoEntityType(charaKey, nesoSize);
    if (type.isEmpty()) {
      Hasugoods.LOGGER.error("Neso entity type not found: {} {}", charaKey, nesoSize);
    }
    return type.get();
  }

  // #region GUI

  @Override
  public int getItemBarColor(ItemStack stack) {
    if (!(stack.getItem() instanceof NesoItem item)) {
      Hasugoods.LOGGER.warn("getItemBarColor called on non-neso ItemStack {}", stack);
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

  public Text getTooltip(ItemStack stack) {
    long stored = getStoredEnergy(stack);
    long capacity = getEnergyCapacity(stack);
    return HasuString.formatEnergyTooltip(stored, capacity);
  }

  @Override
  public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
    super.appendTooltip(stack, context, tooltip, type);
    tooltip.add(getTooltip(stack));
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
        SpawnReason.SPAWN_ITEM_USE,
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

  public void chargeEnergy(ItemStack stack, long amount) {
    if (amount > 0) {
      long stored = getStoredEnergy(stack);
      long capacity = getEnergyCapacity(stack);
      if (stored < capacity) {
        setStoredEnergy(stack, Math.min(stored + amount, capacity));
      }
    }
  }

  public void setFullEnergy(ItemStack stack) {
    long capacity = getEnergyCapacity(stack);
    if (capacity > 0) {
      setStoredEnergy(stack, capacity);
    }
  }

  // #endregion Energy

}
