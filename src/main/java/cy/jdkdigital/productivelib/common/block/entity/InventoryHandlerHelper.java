package cy.jdkdigital.productivelib.common.block.entity;

import cy.jdkdigital.productivelib.common.item.AbstractUpgradeItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class InventoryHandlerHelper
{
    // TODO don't hardcode slot meaning, make abstract
    public static final int BOTTLE_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int FLUID_ITEM_OUTPUT_SLOT = 11;

    public static final int[] OUTPUT_SLOTS = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10};

    private static int getAvailableOutputSlot(BlockEntityItemStackHandler handler, ItemStack insertStack) {
        return getAvailableOutputSlot(handler, insertStack, new ArrayList<>());
    }

    private static int getAvailableOutputSlot(BlockEntityItemStackHandler handler, ItemStack insertStack, List<Integer> blacklistedSlots) {
        int emptySlot = 0;
        for (int slot : handler.getOutputSlots()) {
            if (blacklistedSlots.contains(slot)) {
                continue;
            }
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.isEmpty() && emptySlot == 0) {
                emptySlot = slot;
            } else if (stack.getItem().equals(insertStack.getItem()) && (stack.getCount() + insertStack.getCount()) <= stack.getMaxStackSize()) {
                // TODO removed some Gene specific code, find a way to abstract it
                if (stack.isEmpty() || areItemsAndTagsEqual(stack, insertStack)) {
                    return slot;
                }
            }
        }
        return emptySlot;
    }

    public static boolean areItemsAndTagsEqual(ItemStack stack1, ItemStack stack2) {
        return (
                    stack1.isEmpty() && stack2.isEmpty()
                ) ||
                (
                    stack1.getItem() == stack2.getItem() && areItemStackTagsEqual(stack1, stack2)
                );
    }

    @Deprecated
    public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
        return ItemStack.isSameItemSameComponents(stackA, stackB);
    }

    public static class BlockEntityItemStackHandler extends ItemStackHandler
    {
        protected BlockEntity blockEntity;

        public BlockEntityItemStackHandler(int size) {
            this(size, null);
        }

        public BlockEntityItemStackHandler(int size, @Nullable BlockEntity blockEntity) {
            super(size);
            this.blockEntity = blockEntity;
        }

        public int[] getOutputSlots() {
            return OUTPUT_SLOTS;
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            if (blockEntity != null) {
                blockEntity.setChanged();
            }
        }

        public boolean isInputSlot(int slot) {
            return slot == BOTTLE_SLOT || slot == INPUT_SLOT;
        }

        public boolean isInsertableSlot(int slot) {
            return slot != BOTTLE_SLOT && slot != INPUT_SLOT && slot != FLUID_ITEM_OUTPUT_SLOT;
        }

        public boolean isContainerItem(Item item) {
            return item == Items.GLASS_BOTTLE;
        }

        public boolean isInputSlotItem(int slot, ItemStack item) {
            return (slot == BOTTLE_SLOT && isContainerItem(item.getItem())) || (slot == FLUID_ITEM_OUTPUT_SLOT && !isContainerItem(item.getItem()));
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return isItemValid(slot, stack, true);
        }

        public boolean isItemValid(int slot, @Nonnull ItemStack stack, boolean fromAutomation) {
            // Always allow an input item into an input slot
            if (isInputSlotItem(slot, stack)) {
                return true;
            }

            return !fromAutomation && isInsertableSlot(slot);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return extractItem(slot, amount, simulate, true);
        }

        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate, boolean fromAutomation) {
            // Do not extract from input slots
            if (fromAutomation && isInputSlot(slot)) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return insertItem(slot, stack, simulate, true);
        }

        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate, boolean fromAutomation) {
            return super.insertItem(slot, stack, simulate);
        }

        public ItemStack addOutput(@Nonnull ItemStack stack) {
            //Split the stack into smaller pieces if its over 64 items big
            List<Integer> outputStacks = new LinkedList<>();
            while (stack.getCount() > 0) {
                if (stack.getCount() <= 64) {
                    outputStacks.add(stack.getCount());
                    break;
                }
                outputStacks.add(64);
                stack.setCount(stack.getCount() - 64);
            }
            //Add items to the available output slots for each of the splits created
            Iterator<Integer> iterator = outputStacks.iterator();
            while (iterator.hasNext()) {
                stack.setCount(iterator.next());
                int slot = getAvailableOutputSlot(this, stack);
                if (slot > 0) {
                    ItemStack existingStack = this.getStackInSlot(slot);
                    if (existingStack.isEmpty()) {
                        setStackInSlot(slot, stack.copy());
                    } else {
                        existingStack.grow(stack.getCount());
                    }
                    onContentsChanged(slot);
                    iterator.remove();
                }
            }
            stack.setCount(outputStacks.stream().mapToInt(Integer::intValue).sum());
            //Returning the stack makes it possible for other methods to see if all or some items got added to the inventory
            return stack;
        }

        public boolean canFitStacks(List<ItemStack> stacks) {
            List<Integer> usedSlots = new ArrayList<>();
            for (ItemStack stack : stacks) {
                int slot = getAvailableOutputSlot(this, stack, usedSlots);
                if (slot == 0) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            int size = nbt.contains("Size", 3) ? nbt.getInt("Size") : stacks.size();
            if (size < stacks.size()) {
                nbt.putInt("Size", stacks.size());
            }
            super.deserializeNBT(provider, nbt);
        }
    }

    public static class UpgradeHandler extends BlockEntityItemStackHandler
    {
        public UpgradeHandler(int size, BlockEntity tileEntity) {
            super(size, tileEntity);
        }

        @Override
        public boolean isInsertableSlot(int slot) {
            return false;
        }

        @Override
        public boolean isInputSlotItem(int slot, ItemStack item) {
            return item.getItem() instanceof AbstractUpgradeItem;
        }
    }

    public static class FluidHandler extends FluidTank implements INBTSerializable<CompoundTag>
    {
        public FluidHandler(int capacity) {
            super(capacity);
        }

        public FluidHandler(int capacity, Predicate<FluidStack> validator) {
            super(capacity, validator);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag nbt = new CompoundTag();
            this.fluid.save(provider, nbt);
            return nbt;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            fluid = FluidStack.parse(provider, nbt).orElse(FluidStack.EMPTY);
        }
    }
}
