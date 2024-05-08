package cy.jdkdigital.productivelib.common.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public abstract class TripleOutputRecipe implements Recipe<Container>
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

    @Override
    public boolean matches(Container container, Level level) {
        return input.test(container.getItem(0)); // TODO change slot number to var
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess level) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return false;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess level) {
        return this.output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public static class Serializer<T extends TripleOutputRecipe> implements RecipeSerializer<T>
    {
        final IRecipeFactory<T> factory;

        public Serializer(IRecipeFactory<T> factory) {
            this.factory = factory;
        }

        @Nonnull
        @Override
        public T fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input;
            if (GsonHelper.isArrayNode(json, "input")) {
                input = Ingredient.fromJson(GsonHelper.getAsJsonArray(json, "input"));
            } else {
                input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            }
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            ItemStack secondary = ItemStack.EMPTY;
            if (json.has("secondary")) {
                secondary = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "secondary"));
            }
            ItemStack tertiary = ItemStack.EMPTY;
            if (json.has("tertiary")) {
                tertiary = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "tertiary"));
            }

            return this.factory.create(id, input, output, secondary, tertiary);
        }

        public T fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buffer) {
            try {
                return this.factory.create(id, Ingredient.fromNetwork(buffer), buffer.readItem(), buffer.readItem(), buffer.readItem());
            } catch (Exception e) {
                throw e;
            }
        }

        public void toNetwork(@Nonnull FriendlyByteBuf buffer, T recipe) {
            try {
                recipe.input.toNetwork(buffer);
                buffer.writeItem(recipe.output);
                buffer.writeItem(recipe.secondary);
                buffer.writeItem(recipe.tertiary);
            } catch (Exception e) {
                throw e;
            }
        }

        public interface IRecipeFactory<T extends TripleOutputRecipe>
        {
            T create(ResourceLocation id, Ingredient in, ItemStack out, ItemStack out2, ItemStack out3);
        }
    }
}
