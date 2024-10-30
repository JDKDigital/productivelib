package cy.jdkdigital.productivelib.common.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

public abstract class TripleOutputRecipe implements Recipe<RecipeInput>
{
    private final Ingredient input;
    private final ItemStack output;
    private final ItemStack secondary;
    private final ItemStack tertiary;

    public TripleOutputRecipe(Ingredient input, ItemStack output, ItemStack secondary, ItemStack tertiary) {
        this.input = input;
        this.output = output;
        this.secondary = secondary;
        this.tertiary = tertiary;
    }

    public Ingredient input() {
        return input;
    }

    public ItemStack output() {
        return output;
    }

    public ItemStack secondary() {
        return secondary;
    }

    public ItemStack tertiary() {
        return tertiary;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return input.test(container.getItem(0)); // TODO change slot number to var
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider provider) {
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

    public static class Serializer<T extends TripleOutputRecipe> implements RecipeSerializer<T>
    {
        private MapCodec<T> CODEC;
        public StreamCodec<RegistryFriendlyByteBuf, T> STREAM_CODEC;

        public Serializer(Factory<T> constructor) {
            CODEC = RecordCodecBuilder.mapCodec(
                    builder -> builder.group(
                                    Ingredient.CODEC.fieldOf("input").forGetter(TripleOutputRecipe::input),
                                    ItemStack.CODEC.fieldOf("output").forGetter(TripleOutputRecipe::output),
                                    ItemStack.OPTIONAL_CODEC.fieldOf("secondary").orElse(ItemStack.EMPTY).forGetter(TripleOutputRecipe::secondary),
                                    ItemStack.OPTIONAL_CODEC.fieldOf("tertiary").orElse(ItemStack.EMPTY).forGetter(TripleOutputRecipe::tertiary)
                            )
                            .apply(builder, constructor::create)
            );
            STREAM_CODEC = StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC,
                    TripleOutputRecipe::input,
                    ItemStack.STREAM_CODEC,
                    TripleOutputRecipe::output,
                    ItemStack.OPTIONAL_STREAM_CODEC,
                    TripleOutputRecipe::secondary,
                    ItemStack.OPTIONAL_STREAM_CODEC,
                    TripleOutputRecipe::tertiary,
                    constructor::create
            );
        }

        @Override
        public MapCodec<T> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
            return STREAM_CODEC;
        }

        @FunctionalInterface
        public interface Factory<T extends TripleOutputRecipe> {
            T create(Ingredient input, ItemStack output, ItemStack secondary, ItemStack tertiary);
        }
    }
}
