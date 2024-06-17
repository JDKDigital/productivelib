package cy.jdkdigital.productivelib.loot;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gson.JsonParseException;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class IngredientModifier extends LootModifier
{
    public static final Supplier<MapCodec<IngredientModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst ->
                    codecStart(inst)
                            .and(Ingredient.CODEC.fieldOf("addition").forGetter(m -> m.addition))
                            .and(Codec.FLOAT.fieldOf("chance").orElse(1f).forGetter(m -> m.chance))
                            .and(Codec.BOOL.fieldOf("replace").orElse(false).forGetter(m -> m.replace))
                            .apply(inst, IngredientModifier::new)));

    private final Ingredient addition;
    private final float chance;
    private final boolean replace;

    public IngredientModifier(LootItemCondition[] conditionsIn, Ingredient addition, float chance, boolean replace) {
        super(conditionsIn);
        this.addition = addition;
        this.chance = chance;
        this.replace = replace;
    }

    @Nonnull
    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() <= chance) {
            if (replace && generatedLoot.size() > 0) {
                generatedLoot.remove(0);
            }
            Arrays.stream(addition.getItems()).forEach(itemStack -> {
                generatedLoot.add(itemStack.copy());
            });
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
