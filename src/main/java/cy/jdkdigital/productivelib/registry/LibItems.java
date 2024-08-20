package cy.jdkdigital.productivelib.registry;

import cy.jdkdigital.productivelib.ProductiveLib;
import cy.jdkdigital.productivelib.common.item.FilterUpgradeItem;
import cy.jdkdigital.productivelib.common.item.UpgradeItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

public class LibItems
{
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_BASE = createItem("upgrade_base", () -> new Item(new Item.Properties()));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_PRODUCTIVITY = createItem("upgrade_productivity", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_PRODUCTIVITY_2 = createItem("upgrade_productivity_2", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_PRODUCTIVITY_3 = createItem("upgrade_productivity_3", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_PRODUCTIVITY_4 = createItem("upgrade_productivity_4", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_TIME = createItem("upgrade_time", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_RANGE = createItem("upgrade_range", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_ENTITY_FILTER = createItem("upgrade_entity_filter", () -> new FilterUpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_SIMULATOR = createItem("upgrade_simulator", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_CHILD = createItem("upgrade_child", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_ADULT = createItem("upgrade_adult", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_GENE_SAMPLER = createItem("upgrade_gene_sampler", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_ANTI_TELEPORT = createItem("upgrade_anti_teleport", () -> new UpgradeItem((new Item.Properties())));
    public static final DeferredHolder<Item, ? extends Item> UPGRADE_BLOCK = createItem("upgrade_block", () -> new UpgradeItem((new Item.Properties())));

    public static <I extends Item> DeferredHolder<Item, I> createItem(String name, Supplier<? extends I> supplier) {
        return ProductiveLib.ITEMS.register(name, supplier);
    }

    public static void register() {
    }
}
