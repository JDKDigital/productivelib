package cy.jdkdigital.productivelib.registry;

import cy.jdkdigital.productivelib.ProductiveLib;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Supplier;

public class ModDataComponents
{
    public static final Supplier<DataComponentType<List<ResourceLocation>>> ENTITY_TYPE_LIST = ProductiveLib.DATA_COMPONENTS.register("entity_type_list", () -> DataComponentType.<List<ResourceLocation>>builder().persistent(ResourceLocation.CODEC.listOf()).cacheEncoding().build());

    public static void register() {
    }
}
