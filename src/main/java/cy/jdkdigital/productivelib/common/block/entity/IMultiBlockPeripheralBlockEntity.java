package cy.jdkdigital.productivelib.common.block.entity;

import net.minecraft.core.BlockPos;

public interface IMultiBlockPeripheralBlockEntity
{
    void setMultiblockController(BlockPos pos);

    BlockPos getMultiblockController();
}
