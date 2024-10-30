package cy.jdkdigital.productivelib.loot;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class WeightedIngredientModifier extends LootModifier
{
    public static final Supplier<MapCodec<WeightedIngredientModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst ->
                    codecStart(inst)
                            .and(Codec.list(WeightedIngredient.CODEC).fieldOf("additions").forGetter(m -> m.additions))
                            .and(Codec.FLOAT.fieldOf("chance").orElse(1f).forGetter(m -> m.chance))
                            .and(Codec.BOOL.fieldOf("replace").orElse(false).forGetter(m -> m.replace))
                            .apply(inst, WeightedIngredientModifier::new)));

    private final List<WeightedIngredient> additions;
    private final float chance;
    private final boolean replace;

    public WeightedIngredientModifier(LootItemCondition[] conditionsIn, List<WeightedIngredient> additions, float chance, boolean replace) {
        super(conditionsIn);
        this.additions = additions;
        this.chance = chance;
        this.replace = replace;
    }

    @Nonnull
    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() <= chance) {
            if (replace && !generatedLoot.isEmpty() && !additions.isEmpty()) {
                generatedLoot.removeFirst();
            }

            if (!additions.isEmpty()) {
                Ingredient addition = null;
                if (additions.size() == 1) {
                    addition = additions.getFirst().ingredient;
                } else {
                    List<WeightedIngredient> ingredients = Lists.newArrayList();
                    MutableInt totalWeight = new MutableInt();
                    additions.forEach(weightedIngredient -> {
                        ingredients.add(weightedIngredient);
                        totalWeight.add(weightedIngredient.weight);
                    });

                    if (totalWeight.getValue() > 0) {
                        int j = context.getRandom().nextInt(totalWeight.intValue());

                        for (WeightedIngredient weightedIngredient: additions) {
                            j -= weightedIngredient.weight;
                            if (j < 0) {
                                addition = weightedIngredient.ingredient;
                                break;
                            }
                        }
                    }
                }

                if (addition != null) {
                    Arrays.stream(addition.getItems()).forEach(itemStack -> {
                        generatedLoot.add(itemStack.copy());
                    });
                }
            }
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

    public static class WeightedIngredient
    {
        public static final Codec<WeightedIngredient> CODEC = RecordCodecBuilder.create(
                builder -> builder.group(
                                Ingredient.CODEC.fieldOf("ingredient").forGetter(m -> m.ingredient),
                                Codec.INT.fieldOf("weight").orElse(1).forGetter(m -> m.weight)
                        )
                        .apply(builder, WeightedIngredient::new)
        );

        int weight;
        Ingredient ingredient;

        public WeightedIngredient(Ingredient ingredient, int weight) {
            this.ingredient = ingredient;
            this.weight = weight;
        }
    }
}
