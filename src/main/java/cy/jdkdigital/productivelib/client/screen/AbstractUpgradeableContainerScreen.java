package cy.jdkdigital.productivelib.client.screen;

import cy.jdkdigital.productivelib.common.block.entity.ICapabilityBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.IUpgradeableBlockEntity;
import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import cy.jdkdigital.productivelib.container.AbstractContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUpgradeableContainerScreen<T extends AbstractContainer<? extends ICapabilityBlockEntity>> extends AbstractContainerScreen<T>
{
    public AbstractUpgradeableContainerScreen(T container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.imageWidth = 202;
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        List<FormattedCharSequence> tooltipList = new ArrayList<>();
        if (insideUpgradeSlots(mouseX, mouseY) && this.getMenu() instanceof AbstractContainer<? extends ICapabilityBlockEntity> container && container.getBlockEntity() instanceof IUpgradeableBlockEntity upgradeableBlockEntity && upgradeableBlockEntity.getUpgradeHandler() instanceof InventoryHandlerHelper.UpgradeHandler upgradeHandler) {
            for (int slot = 0; slot < 4; slot++) {
                if (insideUpgradeSlot(slot, mouseX, mouseY) && upgradeHandler.getStackInSlot(slot).isEmpty()) {
                    tooltipList.add(Component.translatable("Valid upgrades:").getVisualOrderText());
                    for (Item item : upgradeHandler.getValidUpgrades()) {
                        tooltipList.add(Component.translatable(item.getDescriptionId()).withStyle(ChatFormatting.GOLD).getVisualOrderText());
                    }
                    break;
                }
            }
        }
        if (!tooltipList.isEmpty()) {
            guiGraphics.renderTooltip(font, tooltipList, mouseX - getGuiLeft(), mouseY - getGuiTop());
        }
    }

    protected boolean insideUpgradeSlots(double mouseX, double mouseY) {
        return isHovering(this.imageWidth - 24, 8, 16, 72, mouseX, mouseY);
    }

    protected boolean insideUpgradeSlot(int slot, double mouseX, double mouseY) {
        return isHovering(this.imageWidth - 24, 8 + 16 * slot + 2 * slot, 16, 16, mouseX, mouseY);
    }
}
