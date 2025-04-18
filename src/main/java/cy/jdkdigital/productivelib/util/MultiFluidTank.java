package cy.jdkdigital.productivelib.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MultiFluidTank implements IFluidHandler, INBTSerializable
{
    protected int capacity;
    protected final List<FluidStack> fluids;

    public MultiFluidTank(int tanks, int capacity) {
        this.capacity = capacity;
        fluids = new ArrayList<>();
    }

    @Override
    public int getTanks() {
        return fluids.size();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int i) {
        return i < this.fluids.size() ? this.fluids.get(i) : FluidStack.EMPTY;
    }

    public void moveTankToTop(int tank) {
        var movedTank = this.fluids.get(tank);
        if (!movedTank.isEmpty()) {
            this.fluids.remove(tank);
            this.fluids.addFirst(movedTank);
            this.onContentsChanged(true);
        }
    }

    @Override
    public int getTankCapacity(int i) {
        return this.getCapacity();
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack fluidStack) {
        for (FluidStack fluid : this.fluids) {
            if (fluid.isEmpty() || FluidStack.isSameFluidSameComponents(fluidStack, fluid)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int fill(@NotNull FluidStack fluidStack, @NotNull FluidAction fluidAction) {
        int availableCapacity = this.capacity - this.totalFluidAmount();
        int maxFillAmount = Math.min(availableCapacity, fluidStack.getAmount());

        for (FluidStack fluid : this.fluids) {// It's very important that we don't get two fluid stacks of the same fluid so hopefully this check is good enough
            if (FluidStack.isSameFluidSameComponents(fluidStack, fluid)) {
                if (!fluidAction.simulate()) {
                    fluid.grow(maxFillAmount);
                    onContentsChanged(false);
                }
                return maxFillAmount;
            }
        }
        if (!fluidAction.simulate()) {
            var fluidCopy = fluidStack.copy();
            fluidCopy.setAmount(maxFillAmount);
            this.fluids.add(fluidCopy);
            onContentsChanged(true);
        }
        return maxFillAmount;
    }

    public @NotNull FluidStack drain(SizedFluidIngredient fluid, FluidAction fluidAction) {
        for (FluidStack stack: fluid.getFluids()) {
            var result = drain(stack, fluidAction);
            if (!result.isEmpty()) {
                return result;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(@NotNull FluidStack fluidStack, @NotNull FluidAction fluidAction) {
        for (FluidStack fluid: this.fluids) {
            if (FluidStack.isSameFluidSameComponents(fluid, fluidStack)) {
                FluidStack returnedFluid = new FluidStack(fluid.getFluid(), Math.min(fluid.getAmount(), fluidStack.getAmount()));
                if (!fluidAction.simulate()) {
                    fluid.shrink(returnedFluid.getAmount());
                    onContentsChanged(fluid.isEmpty());
                }
                return returnedFluid;
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, @NotNull FluidAction fluidAction) {
        for (FluidStack fluid: this.fluids) {
            if (!fluid.isEmpty()) {
                FluidStack returnedFluid = new FluidStack(fluid.getFluid(), Math.min(fluid.getAmount(), maxDrain));
                if (!fluidAction.simulate()) {
                    fluid.shrink(returnedFluid.getAmount());
                    onContentsChanged(fluid.isEmpty());
                }
                return returnedFluid;
            }
        }
        return FluidStack.EMPTY;
    }

    protected void onContentsChanged(boolean hasChangedFluid) {
        // Remove any empty fluids
        this.fluids.removeIf(FluidStack::isEmpty);
    }

    public int totalFluidAmount() {
        return this.fluids.stream().map(FluidStack::getAmount).reduce(0, Integer::sum);
    }

    @Override
    public Tag serializeNBT(HolderLookup.Provider provider) {
        ListTag fluidList = new ListTag();
        for (FluidStack fluid: this.fluids) {
            if (!fluid.isEmpty()) {
                fluidList.add(fluid.save(provider));
            }
        }
        return fluidList;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, Tag nbt) {
        if (nbt instanceof ListTag listTag) {
            this.fluids.clear();
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag tag = listTag.getCompound(i);
                this.fluids.add(FluidStack.parseOptional(provider, tag));
            }
        }
    }
}
