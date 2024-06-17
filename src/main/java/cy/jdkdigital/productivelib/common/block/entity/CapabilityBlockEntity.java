package cy.jdkdigital.productivelib.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class CapabilityBlockEntity extends AbstractBlockEntity implements Nameable
{
    public CapabilityBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Component getName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);
        IItemHandler invHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, getBlockPos(), null);
        if (invHandler instanceof ItemStackHandler serializable) {
            tag.put("inv", serializable.serializeNBT(provider));
        }

        IEnergyStorage energyHandler = level.getCapability(Capabilities.EnergyStorage.BLOCK, getBlockPos(), null);
        if (energyHandler != null) {
            tag.putInt("energy", energyHandler.getEnergyStored());
        }

        IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos(), null);
        if (fluidHandler instanceof InventoryHandlerHelper.FluidHandler serializable) {
            tag.put("fluid", serializable.serializeNBT(provider));
        }

        if (this instanceof UpgradeableBlockEntity) {
            IItemHandler upgradeHandler = ((UpgradeableBlockEntity) this).getUpgradeHandler();
            if (upgradeHandler instanceof ItemStackHandler serializable) {
                tag.put("upgrades", serializable.serializeNBT(provider));
            }
        }
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);
        if (tag.contains("inv")) {
            IItemHandler invHandler = level.getCapability(Capabilities.ItemHandler.BLOCK, getBlockPos(), null);
            if (invHandler instanceof ItemStackHandler serializable) {
                serializable.deserializeNBT(provider, tag.getCompound("inv"));
            }
        }

        if (tag.contains("energy")) {
            IEnergyStorage energyHandler = level.getCapability(Capabilities.EnergyStorage.BLOCK, getBlockPos(), null);
            if (energyHandler != null) {
                energyHandler.extractEnergy(energyHandler.getEnergyStored(), false);
                energyHandler.receiveEnergy(tag.getInt("energy"), false);
            }
        }

        if (tag.contains("fluid")) {
            IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, getBlockPos(), null);
            if (fluidHandler instanceof InventoryHandlerHelper.FluidHandler serializable) {
                serializable.deserializeNBT(provider, tag.getCompound("fluid"));
            }
        }

        if (tag.contains("upgrades") && this instanceof UpgradeableBlockEntity) {
            IItemHandler upgradeHandler = ((UpgradeableBlockEntity) this).getUpgradeHandler();
            if (upgradeHandler instanceof ItemStackHandler serializable) {
                serializable.deserializeNBT(provider, tag.getCompound("upgrades"));
            }
        }
    }
}
