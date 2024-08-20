package cy.jdkdigital.productivelib.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivelib.Config;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.*;

public abstract class TagOutputRecipe
{
    public final List<ChancedOutput> itemOutput;
    public final Map<ItemStack, ChancedOutput> calculatedItemOutput = new LinkedHashMap<>();
    public static Map<String, Integer> modPreference = new HashMap<>();

    public TagOutputRecipe(Ingredient itemOutput) {
        this.itemOutput = new ArrayList<>();
        this.itemOutput.add(new ChancedOutput(itemOutput, 1, 1, 1f));
    }

    public TagOutputRecipe(List<ChancedOutput> itemOutput) {
        this.itemOutput = itemOutput;
    }

    public Map<ItemStack, ChancedOutput> getRecipeOutputs() {
        if (calculatedItemOutput.isEmpty() && !itemOutput.isEmpty()) {
            itemOutput.forEach(chancedOutput -> {
                ItemStack preferredItem = getPreferredItemByMod(chancedOutput.ingredient);
                if (preferredItem != null && !preferredItem.getItem().equals(Items.BARRIER)) {
                    calculatedItemOutput.put(preferredItem, chancedOutput);
                }
            });
        }

        return new LinkedHashMap<>(calculatedItemOutput);
    }

    private static ItemStack getPreferredItemByMod(Ingredient ingredient) {
        List<ItemStack> stacks = Arrays.asList(ingredient.getItems());
        return getPreferredItemByMod(stacks);
    }

    private static ItemStack getPreferredItemByMod(List<ItemStack> list) {
        Map<String, Integer> modPreference = getModPreference();
        ItemStack preferredItem = null;
        int currBest = modPreference.size();
        for (ItemStack item : list) {
            ResourceLocation rl = BuiltInRegistries.ITEM.getKey(item.getItem());
            if (!rl.equals(BuiltInRegistries.ITEM.getDefaultKey())) {
                String modId = rl.getNamespace();
                int priority = 100;
                if (modPreference.containsKey(modId)) {
                    priority = modPreference.get(modId);
                }
                if (preferredItem == null || (priority >= 0 && priority <= currBest)) {
                    preferredItem = item.copy();
                    currBest = priority;
                }
            }
        }
        return preferredItem;
    }

    public static FluidStack getPreferredFluidStackByMod(SizedFluidIngredient fluidIngredient) {
        FluidStack bestFluid = FluidStack.EMPTY;
        int currBest = 100;
        for (FluidStack stack : fluidIngredient.getFluids()) {
            if (bestFluid.isEmpty()) {
                bestFluid = stack;
            } else {
                ResourceLocation rl = BuiltInRegistries.FLUID.getKey(stack.getFluid());
                if (!rl.equals(BuiltInRegistries.FLUID.getDefaultKey())) {
                    String modId = rl.getNamespace();
                    int priority = currBest;
                    if (getModPreference().containsKey(modId)) {
                        priority = getModPreference().get(modId);
                    }

                    if (priority >= 0 && priority <= currBest) {
                        bestFluid = stack;
                        currBest = priority;
                    }
                }
            }
        }
        return bestFluid;
    }

    public static Fluid getPreferredFluidByMod(String fluidName) {
        // Try loading from fluid registry
        Fluid preferredFluid = BuiltInRegistries.FLUID.get(ResourceLocation.parse(fluidName));

        // Try loading fluid from fluid tag
        if (preferredFluid.equals(Fluids.EMPTY)) {
            try {
                HolderSet.Named<Fluid> fluidTag = BuiltInRegistries.FLUID.getOrCreateTag(TagKey.create(Registries.FLUID, ResourceLocation.parse(fluidName)));
                if (fluidTag.size() > 0) {
                    int currBest = 100;
                    for (Holder<Fluid> fluidHolder: fluidTag.stream().toList()) {
                        Fluid fluid = fluidHolder.value();
                        if (!fluid.isSource(fluid.defaultFluidState())) {
                            fluid = ((FlowingFluid) fluid).getSource();
                        }

                        if (!fluid.isSource(fluid.defaultFluidState())) {
                            continue;
                        }

                        ResourceLocation rl = BuiltInRegistries.FLUID.getKey(fluid);
                        if (!rl.equals(BuiltInRegistries.FLUID.getDefaultKey())) {
                            String modId = rl.getNamespace();
                            int priority = currBest;
                            if (getModPreference().containsKey(modId)) {
                                priority = getModPreference().get(modId);
                            }

                            if (preferredFluid == null || (priority >= 0 && priority <= currBest)) {
                                preferredFluid = fluid;
                                currBest = priority;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Who cares
            }
        }

        return preferredFluid;
    }

    public static List<Fluid> getAllFluidsFromName(String fluidName) {
        // Try loading from fluid registry
        List<Fluid> fluids = Collections.singletonList(BuiltInRegistries.FLUID.get(ResourceLocation.parse(fluidName)));

        // Try loading fluid from fluid tag
        if (fluids.get(0).equals(Fluids.EMPTY)) {
            try {
                HolderSet.Named<Fluid> fluidTag = BuiltInRegistries.FLUID.getOrCreateTag(TagKey.create(Registries.FLUID, ResourceLocation.parse(fluidName)));
                if (fluidTag.size() > 0) {
                    return fluidTag.stream().map(Holder::value).toList();
                }
            } catch (Exception e) {
                // Who cares
            }
        }

        return fluids;
    }

    private static Map<String, Integer> getModPreference() {
        if (modPreference.size() > 0) {
            return modPreference;
        }

        int priority = 0;
        modPreference.put("minecraft", ++priority);
        for (String modId : Config.MOD_PREFERENCE.get()) {
            if (ModList.get().isLoaded(modId)) {
                modPreference.put(modId, ++priority);
            }
        }

        return modPreference;
    }

    public record ChancedOutput(Ingredient ingredient, int min, int max, float chance) {
        public static final Codec<ChancedOutput> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    Ingredient.CODEC.fieldOf("item").forGetter(chancedOutput -> chancedOutput.ingredient),
                    Codec.INT.fieldOf("min").orElse(1).forGetter(chancedOutput -> chancedOutput.min),
                    Codec.INT.fieldOf("max").orElse(1).forGetter(chancedOutput -> chancedOutput.max),
                    Codec.FLOAT.fieldOf("chance").orElse(1f).forGetter(chancedOutput -> chancedOutput.chance)
            )
            .apply(builder, ChancedOutput::new)
        );

        public static ChancedOutput read(RegistryFriendlyByteBuf buffer) {
            return new ChancedOutput(Ingredient.CONTENTS_STREAM_CODEC.decode(buffer), buffer.readInt(), buffer.readInt(), buffer.readFloat());
        }

        public static void write(RegistryFriendlyByteBuf buffer, ChancedOutput chancedOutput) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, chancedOutput.ingredient);
            buffer.writeInt(chancedOutput.min);
            buffer.writeInt(chancedOutput.max);
            buffer.writeFloat(chancedOutput.chance);
        }
    }
}
