package cy.jdkdigital.productivelib.common.block.entity;

import cy.jdkdigital.productivelib.util.MultiFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public abstract class CapabilityBlockEntity extends AbstractBlockEntity implements ICapabilityBlockEntity, Nameable
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
    public IItemHandler getItemHandler() {
        return null;
    }
    @Override
    public EnergyStorage getEnergyHandler() {
        return null;
    }
    @Override
    public IFluidHandler getFluidHandler() {
        return null;
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);
        IItemHandler invHandler = getItemHandler();
        if (invHandler instanceof ItemStackHandler serializable) {
            tag.put("inv", serializable.serializeNBT(provider));
        }

        EnergyStorage energyHandler = getEnergyHandler();
        if (energyHandler != null) {
            tag.put("energy", energyHandler.serializeNBT(provider));
        }

        IFluidHandler fluidHandler = getFluidHandler();
        if (fluidHandler instanceof FluidTank fluidTank) {
            CompoundTag nbt = new CompoundTag();
            fluidTank.writeToNBT(provider, nbt);
            tag.put("fluid", nbt);
        } else if (fluidHandler instanceof MultiFluidTank fluidTank) {
            tag.put("fluid", fluidTank.serializeNBT(provider));
        }

        if (this instanceof IUpgradeableBlockEntity) {
            IItemHandler upgradeHandler = ((IUpgradeableBlockEntity) this).getUpgradeHandler();
            if (upgradeHandler instanceof ItemStackHandler serializable) {
                tag.put("upgrades", serializable.serializeNBT(provider));
            }
        }
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);
        if (tag.contains("inv")) {
            IItemHandler invHandler = getItemHandler();
            if (invHandler instanceof ItemStackHandler serializable) {
                serializable.deserializeNBT(provider, tag.getCompound("inv"));
            }
        }

        if (tag.contains("energy")) {
            EnergyStorage energyHandler = getEnergyHandler();
            if (energyHandler != null) {
                energyHandler.deserializeNBT(provider, tag.get("energy"));
            }
        }

        IFluidHandler fluidHandler = getFluidHandler();
        if (tag.contains("fluid")) {
            if (fluidHandler instanceof FluidTank fluidTank) {
                fluidTank.readFromNBT(provider, tag.getCompound("fluid"));
            } else if (fluidHandler instanceof MultiFluidTank fluidTank) {
                fluidTank.deserializeNBT(provider, tag.get("fluid"));
            }
        }

        if (tag.contains("upgrades") && this instanceof IUpgradeableBlockEntity) {
            IItemHandler upgradeHandler = ((IUpgradeableBlockEntity) this).getUpgradeHandler();
            if (upgradeHandler instanceof ItemStackHandler serializable) {
                serializable.deserializeNBT(provider, tag.getCompound("upgrades"));
            }
        }
    }
}
