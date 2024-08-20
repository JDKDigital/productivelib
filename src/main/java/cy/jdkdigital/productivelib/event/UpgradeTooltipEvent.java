package cy.jdkdigital.productivelib.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class UpgradeTooltipEvent extends Event
{
    private final ItemStack stack;
    private final Item.TooltipContext context;
    private final List<Component> tooltipComponents;
    @Nullable
    private List<ResourceLocation> entities;
    private final List<Component> validBlocks;

    public UpgradeTooltipEvent(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, @Nullable List<ResourceLocation> entities) {
        this.stack = stack;
        this.context = context;
        this.tooltipComponents = tooltipComponents;
        this.validBlocks = new ArrayList<>();
        this.entities = entities;
    }

    public ItemStack getStack() {
        return stack;
    }

    public Item.TooltipContext getContext() {
        return context;
    }

    public List<Component> getTooltipComponents() {
        return tooltipComponents;
    }

    @Nullable
    public List<ResourceLocation> getEntities() {
        return entities;
    }

    public void setEntities(@Nullable List<ResourceLocation> entities) {
        this.entities = entities;
    }

    public List<Component> getValidBlocks() {
        return validBlocks;
    }

    public void addValidBlock(Component blockName) {
        this.validBlocks.add(blockName);
    }
}