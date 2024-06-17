package cy.jdkdigital.productivelib.util;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class RecipeUtil
{
//    public static JsonObject itemToJson(ItemStack item) {
//        var json = new JsonObject();
//        json.addProperty("item", BuiltInRegistries.ITEM.getKey(item.getItem()).toString());
//        if (item.getCount() > 1) {
//            json.addProperty("count", item.getCount());
//        }
//        if (!item.isComponentsPatchEmpty()) {
//            json.addProperty("type", "forge:nbt");
//            json.addProperty("nbt", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, item.getComponents()).toString());
//        }
//        return json;
//    }
//    public static JsonObject itemChanceToJson(ItemStack item, float chance) {
//        var json = itemToJson(item);
//        json.addProperty("chance", chance);
//        return json;
//    }
//    public static JsonObject fluidToJson(FluidStack fluid) {
//        var json = new JsonObject();
//        json.addProperty("fluid", BuiltInRegistries.FLUID.getKey(fluid.getFluid()).toString());
//        if (fluid.getAmount() > 1) {
//            json.addProperty("amount", fluid.getAmount());
//        }
//        if (fluid.getTag() != null) {
//            json.addProperty("type", "forge:nbt");
//            json.addProperty("nbt", NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, fluid.getTag()).toString());
//        }
//        return json;
//    }
}
