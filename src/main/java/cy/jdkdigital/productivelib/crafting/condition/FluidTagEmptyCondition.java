package cy.jdkdigital.productivelib.crafting.condition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;

public record FluidTagEmptyCondition(TagKey<Fluid> tag_name) implements ICondition
{
    public static MapCodec<FluidTagEmptyCondition> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(TagKey.codec(Registries.FLUID).fieldOf("tag").forGetter(FluidTagEmptyCondition::tag_name))
                    .apply(builder, FluidTagEmptyCondition::new));

    public FluidTagEmptyCondition(String location) {
        this("minecraft", location);
    }

    public FluidTagEmptyCondition(String namespace, String path) {
        this(TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(namespace, path)));
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public boolean test(IContext context)
    {
        return context.getTag(tag_name).isEmpty();
    }

    @Override
    public String toString() {
        return "fluid_tag_empty(\"" + tag_name + "\")";
    }
}