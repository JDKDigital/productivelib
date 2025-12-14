package cy.jdkdigital.productivelib.common.block.entity;

import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public interface ICapabilityBlockEntity
{
    public IItemHandler getItemHandler();
    public EnergyStorage getEnergyHandler();
    public IFluidHandler getFluidHandler();
}
