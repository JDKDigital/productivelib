package cy.jdkdigital.productivelib.common.block;

import cy.jdkdigital.productivelib.common.block.entity.UpgradeableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

public abstract class CapabilityContainerBlock extends BaseEntityBlock
{
    protected CapabilityContainerBlock(Properties builder) {
        super(builder);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (oldState.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null) {
                // Drop inventory
                IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
                if (handler != null) {
                    for (int slot = 0; slot < handler.getSlots(); ++slot) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), handler.getStackInSlot(slot));
                    }
                }

                if (blockEntity instanceof UpgradeableBlockEntity) {
                    IItemHandler upgradeHandler = ((UpgradeableBlockEntity) blockEntity).getUpgradeHandler();
                    if (upgradeHandler != null) {
                        for (int slot = 0; slot < upgradeHandler.getSlots(); ++slot) {
                            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), upgradeHandler.getStackInSlot(slot));
                        }
                    }
                }
            }
        }
        super.onRemove(oldState, level, pos, newState, isMoving);
    }
}
