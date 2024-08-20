package cy.jdkdigital.productivelib;

import com.mojang.serialization.MapCodec;
import cy.jdkdigital.productivelib.crafting.condition.FluidTagEmptyCondition;
import cy.jdkdigital.productivelib.loot.IngredientModifier;
import cy.jdkdigital.productivelib.loot.ItemLootModifier;
import cy.jdkdigital.productivelib.loot.LootItemKilledByUUIDCondition;
import cy.jdkdigital.productivelib.loot.OptionalLootItem;
import cy.jdkdigital.productivelib.loot.condition.OptionalCopyBlockState;
import cy.jdkdigital.productivelib.registry.ModDataComponents;
import cy.jdkdigital.productivelib.registry.LibItems;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ProductiveLib.MODID)
public final class ProductiveLib
{
    public static final String MODID = "productivelib";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<LootPoolEntryType> LOOT_POOL_ENTRIES = DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, MODID);
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_POOL_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID);
    public static final DeferredRegister<LootItemConditionType> LOOT_POOL_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MODID);
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MODID);
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS = DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, MODID);
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<ItemLootModifier>> ITEM_MODIFIER = LOOT_SERIALIZERS.register("item_modifier", ItemLootModifier.CODEC);
    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<IngredientModifier>> INGREDIENT_MODIFIER = LOOT_SERIALIZERS.register("ingredient_modifier", IngredientModifier.CODEC);
    public static final DeferredHolder<LootPoolEntryType, LootPoolEntryType> OPTIONAL_LOOT_ITEM = LOOT_POOL_ENTRIES.register("optional_loot_item", () -> new LootPoolEntryType(OptionalLootItem.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<?>> OPTIONAL_BLOCK_STATE_PROPERTY = LOOT_POOL_FUNCTIONS.register("optional_copy_block_state", () -> new LootItemFunctionType(OptionalCopyBlockState.CODEC));
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> KILLED_BY_UUID = LOOT_POOL_CONDITIONS.register("killed_by_uuid", () -> new LootItemConditionType(LootItemKilledByUUIDCondition.CODEC));
    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<FluidTagEmptyCondition>> FLUID_TAG_EMPTY_CONDITION = CONDITION_CODECS.register("fluid_tag_empty", () -> FluidTagEmptyCondition.CODEC);

    public ProductiveLib(IEventBus modEventBus, ModContainer container) {
        LOOT_SERIALIZERS.register(modEventBus);
        LOOT_POOL_ENTRIES.register(modEventBus);
        LOOT_POOL_FUNCTIONS.register(modEventBus);
        LOOT_POOL_CONDITIONS.register(modEventBus);
        CONDITION_CODECS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        ITEMS.register(modEventBus);

        LibItems.register();
        ModDataComponents.register();

        container.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
    }
}
