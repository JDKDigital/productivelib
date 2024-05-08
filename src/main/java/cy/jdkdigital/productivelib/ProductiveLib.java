package cy.jdkdigital.productivelib;

import com.mojang.serialization.Codec;
import cy.jdkdigital.productivelib.loot.IngredientModifier;
import cy.jdkdigital.productivelib.loot.ItemLootModifier;
import cy.jdkdigital.productivelib.loot.LootItemKilledByUUIDCondition;
import cy.jdkdigital.productivelib.loot.OptionalLootItem;
import cy.jdkdigital.productivelib.loot.condition.OptionalCopyBlockState;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ProductiveLib.MODID)
public final class ProductiveLib
{
    public static final String MODID = "productivelib";
    public static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<LootPoolEntryType> LOOT_POOL_ENTRIES = DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, MODID);
    public static final DeferredRegister<LootItemFunctionType> LOOT_POOL_FUNCTIONS = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, MODID);
    public static final DeferredRegister<LootItemConditionType> LOOT_POOL_CONDITIONS = DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, MODID);
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ProductiveLib.MODID);

    public static final RegistryObject<Codec<ItemLootModifier>> ITEM_MODIFIER = LOOT_SERIALIZERS.register("item_modifier", ItemLootModifier.CODEC);
    public static final RegistryObject<Codec<IngredientModifier>> INGREDIENT_MODIFIER = LOOT_SERIALIZERS.register("ingredient_modifier", IngredientModifier.CODEC);

    public static final RegistryObject<LootPoolEntryType> OPTIONAL_LOOT_ITEM = LOOT_POOL_ENTRIES.register("optional_loot_item", () -> new LootPoolEntryType(new OptionalLootItem.Serializer()));
    public static final RegistryObject<LootItemFunctionType> OPTIONAL_BLOCK_STATE_PROPERTY = LOOT_POOL_FUNCTIONS.register("optional_copy_block_state", () -> new LootItemFunctionType(new OptionalCopyBlockState.Serializer()));
    public static final RegistryObject<LootItemConditionType> KILLED_BY_UUID = LOOT_POOL_CONDITIONS.register("killed_by_uuid", () -> new LootItemConditionType(new LootItemKilledByUUIDCondition.Serializer()));

    public ProductiveLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        LOOT_SERIALIZERS.register(modEventBus);
        LOOT_POOL_ENTRIES.register(modEventBus);
        LOOT_POOL_FUNCTIONS.register(modEventBus);
        LOOT_POOL_CONDITIONS.register(modEventBus);
    }
}
