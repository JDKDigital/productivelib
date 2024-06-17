package cy.jdkdigital.productivelib.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivelib.ProductiveLib;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.function.Consumer;

public class OptionalLootItem extends LootPoolSingletonContainer
{
    public static final MapCodec<OptionalLootItem> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("name").orElse(ItemStack.EMPTY.getItemHolder()).forGetter(optionalLootItem -> optionalLootItem.item))
                    .and(singletonFields(builder))
                    .apply(builder, OptionalLootItem::new)
    );

    private final Holder<Item> item;

    private OptionalLootItem(Holder<Item> item, int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions) {
        super(weight, quality, conditions, functions);
        this.item = item;
    }

    public LootPoolEntryType getType() {
        return ProductiveLib.OPTIONAL_LOOT_ITEM.get();
    }

    public void createItemStack(Consumer<ItemStack> pStackConsumer, LootContext pLootContext) {
        pStackConsumer.accept(new ItemStack(this.item));
    }

    public static LootPoolSingletonContainer.Builder<?> lootTableItem(ItemLike item) {
        return simpleBuilder(
                (weight, quality, conditions, functions) -> new OptionalLootItem(item.asItem().builtInRegistryHolder(), weight, quality, conditions, functions)
        );
    }
}
