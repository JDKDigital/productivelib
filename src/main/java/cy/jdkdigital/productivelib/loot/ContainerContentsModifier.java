package cy.jdkdigital.productivelib.loot;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivelib.ProductiveLib;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContainerContentsModifier extends LootModifier
{
    public static final Supplier<MapCodec<ContainerContentsModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.mapCodec(inst ->
                    codecStart(inst)
                            .and(ItemStack.CODEC.fieldOf("target").forGetter(m -> m.target))
                            .and(Ingredient.CODEC.fieldOf("addition").forGetter(m -> m.addition))
                            .and(Codec.FLOAT.fieldOf("chance").orElse(1f).forGetter(m -> m.chance))
                            .and(Codec.BOOL.fieldOf("replace").orElse(false).forGetter(m -> m.replace))
                            .apply(inst, ContainerContentsModifier::new)));

    private final ItemStack target;
    private final Ingredient addition;
    private final float chance;
    private final boolean replace;

    public ContainerContentsModifier(LootItemCondition[] conditionsIn, ItemStack target, Ingredient addition, float chance, boolean replace) {
        super(conditionsIn);
        this.target = target;
        this.addition = addition;
        this.chance = chance;
        this.replace = replace;
    }

    @Nonnull
    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.forEach(stack -> {
            if (stack.is(target.getItem()) && stack.has(DataComponents.CONTAINER)) {
                List<ItemStack> contents = new ArrayList<>(stack.get(DataComponents.CONTAINER).stream().toList());
                if (context.getRandom().nextFloat() <= chance) {
                    Arrays.stream(addition.getItems()).forEach(itemStack -> {
                        contents.add(itemStack.copy());
                    });
                }
                stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(contents));
            }
        });
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
