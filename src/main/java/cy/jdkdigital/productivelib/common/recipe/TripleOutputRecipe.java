package cy.jdkdigital.productivelib.common.recipe;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

public abstract class TripleOutputRecipe implements Recipe<CraftingInput>
{
    public final ResourceLocation id;
    public final Ingredient input;
    public final ItemStack output;
    public final ItemStack secondary;
    public final ItemStack tertiary;

    public TripleOutputRecipe(ResourceLocation id, Ingredient input, ItemStack output, ItemStack secondary, ItemStack tertiary) {
        this.id = id;
        this.input = input;
        this.output = output;
        this.secondary = secondary;
        this.tertiary = tertiary;
    }

    protected static <T extends TripleOutputRecipe> Products.P5<RecordCodecBuilder.Mu<T>, ResourceLocation, Ingredient, ItemStack, ItemStack, ItemStack> codecStart(RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(m -> m.id),
                Ingredient.CODEC.fieldOf("input").forGetter(m -> m.input),
                ItemStack.CODEC.fieldOf("output").forGetter(m -> m.output),
                ItemStack.CODEC.fieldOf("secondary").forGetter(m -> m.secondary),
                ItemStack.CODEC.fieldOf("tertiary").forGetter(m -> m.tertiary)
        );
    }

    abstract int getInputSlot();

    @Override
    public boolean matches(CraftingInput container, Level level) {
        return input.test(container.getItem(getInputSlot()));
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return this.output.copy();
    }
}
