package cy.jdkdigital.productivelib.common.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TickingBlockEntity extends CapabilityBlockEntity
{
    int tickCounter = 0;

    public TickingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    abstract int tickRate();

    public void tickClient(Level level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {};

    public void tickServer(ServerLevel level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {};

    boolean shouldTick() {
        return tickCounter%tickRate() == 0;
    }

    public void tickHandler(Level level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {
        tickCounter++;
        if (shouldTick()) {
            tickCounter = 0;
            if (level.isClientSide) {
                tickClient(level, blockPos, blockState, blockEntity);
            } else {
                tickServer((ServerLevel) level, blockPos, blockState, blockEntity);
            }
        }
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, TickingBlockEntity blockEntity) {
        blockEntity.tickHandler(level, blockPos, blockState, blockEntity);
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);
        tag.putInt("tickCounter", tickCounter);
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);
        if (tag.contains("tickCounter")) {
            tickCounter = tag.getInt("tickCounter");
        }
    }
}
