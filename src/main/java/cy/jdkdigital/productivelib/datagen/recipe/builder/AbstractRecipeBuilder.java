package cy.jdkdigital.productivelib.datagen.recipe.builder;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public abstract class AbstractRecipeBuilder implements RecipeBuilder {
    public static class IngredientOutput {
        private final Ingredient ingredient;
        protected final int chance;
        protected final int min;
        protected final int max;

        public IngredientOutput(Ingredient ingredient, int chance, int min, int max) {
            this.ingredient = ingredient;
            this.chance = chance;
            this.min = min;
            this.max = max;
        }

        public IngredientOutput(Ingredient ingredient, int chance) {
            this(ingredient, chance, 0, 0);
        }

        public IngredientOutput(Ingredient ingredient) {
            this(ingredient, 100);
        }

        public JsonElement toJson() {
            JsonObject output = new JsonObject();
            if (max != 0) {
                output.addProperty("max", chance);
            }
            if (min != 0) {
                output.addProperty("min", chance);
            }
            if (chance != 100) {
                output.addProperty("chance", chance);
            }
            output.add("item", ingredient.toJson());
            return output;
        }
    }

    public static class ModItemOutput extends IngredientOutput {
        private final String ingredient;

        public ModItemOutput(String ingredient, int chance, int min, int max) {
            super(Ingredient.of(Items.STICK), chance, min, max);
            this.ingredient = ingredient;
        }

        public ModItemOutput(String ingredient, int chance) {
            this(ingredient, chance, 0, 0);
        }

        public ModItemOutput(String ingredient) {
            this(ingredient, 100);
        }

        public JsonElement toJson() {
            JsonObject output = new JsonObject();
            if (max != 0) {
                output.addProperty("max", max);
            }
            if (min != 0) {
                output.addProperty("min", min);
            }
            if (chance != 100) {
                output.addProperty("chance", chance);
            }

            JsonObject item = new JsonObject();

            if (ingredient.startsWith("#")) {
                item.addProperty("tag", ingredient.replace("#", ""));
            } else {
                item.addProperty("item", ingredient);
            }

            output.add("item", item);
            return output;
        }
    }

    public static class FluidOutput {
        private final String fluidString;
        private final int amount;

        public FluidOutput(String fluidString, int amount) {
            this.fluidString = fluidString;
            this.amount = amount;
        }

        public FluidOutput(String fluidString) {
            this(fluidString, 50);
        }

        public JsonElement toJson() {
            JsonObject output = new JsonObject();
            JsonObject fluid = new JsonObject();
            if (fluidString.startsWith("#")) {
                fluid.addProperty("tag", fluidString.substring(1));
            } else {
                fluid.addProperty("fluid", fluidString);
            }

            output.add("fluid", fluid);
            output.addProperty("amount", amount);
            return output;
        }
    }
}
