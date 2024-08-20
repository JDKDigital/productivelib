package cy.jdkdigital.productivelib.event;

import cy.jdkdigital.productivelib.ProductiveLib;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@EventBusSubscriber(modid = ProductiveLib.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientModEventHandler
{
    public static ResourceKey<CreativeModeTab> PB_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath("productivebees", "productivebees"));

    @SubscribeEvent
    public static void tabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(PB_TAB_KEY)) {
            for (DeferredHolder<Item, ? extends Item> item: ProductiveLib.ITEMS.getEntries()) {
                event.accept(new ItemStack(item.get(), 1));
            }
        }
    }
}
