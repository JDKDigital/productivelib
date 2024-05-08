package cy.jdkdigital.productivelib.container;

import cy.jdkdigital.productivelib.common.block.entity.InventoryHandlerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class ManualSlotItemHandler extends SlotItemHandler
{
    InventoryHandlerHelper.BlockEntityItemStackHandler handler;

    public ManualSlotItemHandler(InventoryHandlerHelper.BlockEntityItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        handler = itemHandler;
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return handler.isInputSlotItem(this.getSlotIndex(), stack) && handler.isItemValid(this.getSlotIndex(), stack, false);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return !this.handler.extractItem(this.getSlotIndex(), 1, true, false).isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack remove(int amount) {
        return this.handler.extractItem(this.getSlotIndex(), amount, false, false);
    }

    @Override
    public int getMaxStackSize() {
        if (this.getItemHandler() instanceof InventoryHandlerHelper.UpgradeHandler) {
            return 1;
        }
        return super.getMaxStackSize();
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        if (this.getItemHandler() instanceof InventoryHandlerHelper.UpgradeHandler) {
            return 1;
        }
        return super.getMaxStackSize(stack);
    }
}