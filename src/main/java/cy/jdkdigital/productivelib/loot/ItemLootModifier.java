package cy.jdkdigital.productivelib.loot;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ItemLootModifier extends LootModifier
{
    public static final Supplier<MapCodec<ItemLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst ->
                    codecStart(inst)
                            .and(ItemStack.CODEC.fieldOf("addition").forGetter(m -> m.addition))
                            .and(Codec.FLOAT.fieldOf("chance").orElse(1f).forGetter(m -> m.chance))
                            .apply(inst, ItemLootModifier::new)));

    private final ItemStack addition;
    private final float chance;

    public ItemLootModifier(LootItemCondition[] conditionsIn, ItemStack addition, float chance) {
        super(conditionsIn);
        this.addition = addition;
        this.chance = chance;
    }

    @Nonnull
    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() <= chance) {
            generatedLoot.add(addition);
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
