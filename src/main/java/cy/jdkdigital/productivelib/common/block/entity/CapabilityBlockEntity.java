package cy.jdkdigital.productivelib.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
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

    public IItemHandler getItemHandler() {
        return null;
    }
    public EnergyStorage getEnergyHandler() {
        return null;
    }
    public FluidTank getFluidHandler() {
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

        FluidTank fluidHandler = getFluidHandler();
        if (fluidHandler != null) {
            CompoundTag nbt = new CompoundTag();
            fluidHandler.writeToNBT(provider, nbt);
            tag.put("fluid", nbt);
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

        if (tag.contains("fluid")) {
            FluidTank fluidHandler = getFluidHandler();
            if (fluidHandler != null) {
                fluidHandler.readFromNBT(provider, tag.getCompound("fluid"));
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
