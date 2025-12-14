package cy.jdkdigital.productivelib.util;

import com.mojang.datafixers.util.Pair;
import cy.jdkdigital.productivelib.ProductiveLib;
import cy.jdkdigital.productivelib.common.block.IMultiBlockController;
import cy.jdkdigital.productivelib.common.block.IMultiBlockPeripheral;
import cy.jdkdigital.productivelib.common.block.entity.IMultiBlockPeripheralBlockEntity;
import cy.jdkdigital.productivelib.exception.InvalidStructureException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MultiBlockDetector
{
    public static MultiBlockData detectStructure(Level level, BlockPos controllerPos, TagKey<Block> wallBlocks, @Nullable TagKey<Block> bottomBlocks, boolean hollow, boolean optionalCorners, int maxVolume, int maxCirc, int maxHeight) throws InvalidStructureException {
        return detectStructure(level, controllerPos, wallBlocks, bottomBlocks, hollow, optionalCorners, false, maxVolume, maxCirc, maxHeight);
    }

    public static MultiBlockData detectStructure(Level level, BlockPos controllerPos, TagKey<Block> wallBlocks, @Nullable TagKey<Block> bottomBlocks, boolean hollow, boolean optionalCorners, boolean uniformBottom, int maxVolume, int maxCirc, int maxHeight) throws InvalidStructureException {
        return detectStructure(level, controllerPos, wallBlocks, bottomBlocks, null, hollow, optionalCorners, uniformBottom, maxVolume, maxCirc, maxHeight);
    }

    public static MultiBlockData detectStructure(Level level, BlockPos controllerPos, TagKey<Block> wallBlocks, @Nullable TagKey<Block> bottomBlocks, @Nullable TagKey<Block> topBlocks, boolean hollow, boolean optionalCorners, boolean uniformBottom, int maxVolume, int maxCirc, int maxHeight) throws InvalidStructureException {
        // Controller can be placed in any part of the structure wall, so we need to go up to the top first. Top is any valid structure block above the controller
        BlockPos top = controllerPos.mutable();
        while (top.getY() < level.getMaxBuildHeight() && level.getBlockState(top.above()).is(wallBlocks)) {
            top = top.above();
        }

        BlockState controllerState = level.getBlockState(controllerPos);
        if (controllerState.isAir()) {
            throw new InvalidStructureException("Invalid controller", controllerPos);
        }

        Direction controllerFacing = controllerState.getValue(BlockStateProperties.HORIZONTAL_FACING);

        List<BlockPos> peripherals = new ArrayList<>();

        // locate top corners
        Pair<BlockPos, BlockPos> topCorners = findStructureSliceCorners(level, controllerPos, controllerFacing.getClockWise(), top, wallBlocks, optionalCorners, maxCirc, peripherals);

        // find botttom of structure
        int height = 0;

        BlockPos.MutableBlockPos bottomCornerRelativePosition = controllerPos.mutable();
        if (bottomBlocks != null) {
            // look for a floor inside the structure
            bottomCornerRelativePosition = topCorners.getFirst().relative(controllerFacing.getOpposite()).relative(controllerFacing.getCounterClockWise()).mutable();
            while (height < maxHeight && !level.getBlockState(bottomCornerRelativePosition.move(Direction.DOWN)).is(bottomBlocks)) {
                height++;
            }
            height++;

            if (!level.getBlockState(bottomCornerRelativePosition).is(bottomBlocks)) {
                var invalidPos = topCorners.getFirst().relative(controllerFacing.getOpposite()).relative(controllerFacing.getCounterClockWise()).mutable();
                throw new InvalidStructureException("Invalid or missing bottom starting block. Valid blocks are " + bottomBlocks, invalidPos, level.getBlockState(invalidPos));
            }

            // validate bottom
            AtomicReference<BlockState> firstBottomBlock = new AtomicReference<>();
            List<BlockPos> notBottomBlocks = BlockPos.betweenClosedStream(topCorners.getFirst().relative(controllerFacing.getOpposite()).relative(controllerFacing.getCounterClockWise()).below(height), topCorners.getSecond().relative(controllerFacing).relative(controllerFacing.getClockWise()).below(height))
                    .map(BlockPos::immutable)
                    .filter(pos -> {
                        if (firstBottomBlock.get() == null) {
                            firstBottomBlock.set(level.getBlockState(pos));
                        }
                        return uniformBottom ? !level.getBlockState(pos).is(firstBottomBlock.get().getBlock()) : !level.getBlockState(pos).is(bottomBlocks);
                    }).toList();
            if (!notBottomBlocks.isEmpty()) {
                throw new InvalidStructureException("Invalid or missing bottom block." + (uniformBottom ? "All bottom blocks must be of the same type." : ""), notBottomBlocks.getFirst(), level.getBlockState(notBottomBlocks.getFirst()));
            }
        } else {
            // check how far the wall under the controller continues
            while (height < maxHeight && level.getBlockState(bottomCornerRelativePosition.move(Direction.DOWN)).is(wallBlocks)) {
                height++;
            }
        }

        // find lid structure
        if (topBlocks != null) {
            List<BlockPos> notTopBlocks = BlockPos.betweenClosedStream(topCorners.getFirst().relative(controllerFacing.getOpposite()).relative(controllerFacing.getCounterClockWise()).above(), topCorners.getSecond().relative(controllerFacing).relative(controllerFacing.getClockWise()).above())
                    .map(BlockPos::immutable)
                    .filter(pos -> !level.getBlockState(pos).is(topBlocks)).toList();
            if (!notTopBlocks.isEmpty()) {
                throw new InvalidStructureException("Invalid top block", notTopBlocks.getFirst(), level.getBlockState(notTopBlocks.getFirst()));
            }
        }

        // validate each ring between top and bottom
        for (int i = 1; i <= height; i++) {
            // skip validating lowest ring if corners can be empty and there's no wall block in the start position
            if (optionalCorners && i == height && !level.getBlockState(top.below(i)).is(wallBlocks)) continue;
            findStructureSliceCorners(level, controllerPos, controllerFacing.getClockWise(), top.below(i), wallBlocks, optionalCorners, maxCirc, peripherals);
        }

        int volume = (int) BlockPos.betweenClosedStream(topCorners.getFirst().relative(controllerFacing.getOpposite()).relative(controllerFacing.getCounterClockWise()), topCorners.getSecond().relative(controllerFacing).relative(controllerFacing.getClockWise()).below(height - 1)).count();
        if (volume > maxVolume) {
            throw new InvalidStructureException("Internal structure area is too big " + volume + "/" + maxVolume, controllerPos);
        }

        if (hollow) {
            List<BlockPos> notAirBlocks = BlockPos.betweenClosedStream(
                    topCorners.getFirst().relative(controllerFacing.getOpposite()).relative(controllerFacing.getCounterClockWise()),
                    topCorners.getSecond().relative(controllerFacing).relative(controllerFacing.getClockWise()).below(height - 1)
            ).map(BlockPos::immutable).filter(pos -> {
                var state = level.getBlockState(pos);
                return !state.isAir() && !state.is(ProductiveLib.IGNORED_INTERNAL_MULTIBLOCK_BLOCKS);
            }).toList();
            if (!notAirBlocks.isEmpty()) {
                throw new InvalidStructureException("Internal structure area is not clear", notAirBlocks.getFirst(), level.getBlockState(notAirBlocks.getFirst()));
            }
        }

        return new MultiBlockData(controllerPos, topCorners, peripherals, height, volume);
    }

    private static Pair<BlockPos, BlockPos> findStructureSliceCorners(Level level, BlockPos controllerPos, Direction dir, BlockPos initialPosition, TagKey<Block> validBlocks, boolean optionalCorners, int maxCirc, List<BlockPos> peripherals) throws InvalidStructureException {
        BlockPos firstCorner = new BlockPos(initialPosition);
        BlockPos secondCorner = new BlockPos(initialPosition);

        var pointer = new BlockPos.MutableBlockPos(initialPosition.getX(), initialPosition.getY(), initialPosition.getZ());

        int turns = 0, maxSize = maxCirc;
        while (turns <= 4 && maxSize-- > 0 && level.getBlockState(pointer).is(validBlocks) && (turns == 0 || !pointer.equals(initialPosition))) {
            // if next block is not a valid wall structure block, turn and search in new direction
            var nextBlockState = level.getBlockState(pointer.relative(dir));
            if (turns < 4 && !nextBlockState.is(validBlocks)) {
                // adjust for empty corner
                if (optionalCorners && !level.getBlockState(pointer.relative(dir.getClockWise())).is(validBlocks)) {
                    pointer.move(dir);
                }

                if (turns == 0) {
                    firstCorner = pointer.immutable();
                }
                if (turns == 2) {
                    secondCorner = pointer.immutable();
                }
                dir = dir.getClockWise();
                turns++;
            }

            pointer.move(dir);

            var stateAt = level.getBlockState(pointer);
            // Set controller position on peripherals
            if (stateAt.getBlock() instanceof IMultiBlockPeripheral && level.getBlockEntity(pointer) instanceof IMultiBlockPeripheralBlockEntity multiBlockPeripheral) {
                multiBlockPeripheral.setMultiblockController(controllerPos);
                peripherals.add(pointer.immutable());
            }

            // Check that only one controller exists in the structure
            if (stateAt.getBlock() instanceof IMultiBlockController && !pointer.equals(controllerPos)) {
                throw new InvalidStructureException("Multiple controllers are not allowed", pointer.relative(dir).immutable());
            }
        }

        if (turns == 4 && pointer.immutable().equals(initialPosition)) {
            return Pair.of(firstCorner, secondCorner);
        }
        throw new InvalidStructureException(pointer.immutable());
    }

    public static final class MultiBlockData implements INBTSerializable
    {
        private BlockPos controllerPos;
        private Pair<BlockPos, BlockPos> topCorners;
        private List<BlockPos> peripherals;
        private int height;
        private int volume;

        public MultiBlockData(BlockPos controllerPos, Pair<BlockPos, BlockPos> topCorners, List<BlockPos> peripherals, int height, int volume) {
            this.controllerPos = controllerPos;
            this.topCorners = topCorners;
            this.peripherals = peripherals;
            this.height = height;
            this.volume = volume;
        }

        @Override
        public Tag serializeNBT(HolderLookup.Provider provider) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("height", height());
            tag.putInt("volume", volume());
            tag.putLong("controller", controllerPos().asLong());
            tag.putLong("corner1", topCorners().getFirst().asLong());
            tag.putLong("corner2", topCorners().getSecond().asLong());
            tag.putInt("peripheral_count", peripherals().size());
            int i = 0;
            for (BlockPos blockPos : peripherals()) {
                tag.putLong("p" + i, blockPos.asLong());
                i++;
            }
            return tag;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, Tag tag) {
            if (tag instanceof CompoundTag compoundTag) {
                this.height = compoundTag.getInt("height");
                this.volume = compoundTag.getInt("volume");
                this.controllerPos = BlockPos.of(compoundTag.getLong("controller"));
                this.topCorners = Pair.of(BlockPos.of(compoundTag.getLong("corner1")), BlockPos.of(compoundTag.getLong("corner2")));
                List<BlockPos> ps = new ArrayList<>();
                for (int i = 0; i < compoundTag.getInt("peripheral_count"); i++) {
                    ps.add(BlockPos.of(compoundTag.getLong("p" + i)));
                }
                this.peripherals = ps;
            }
        }

        public BlockPos controllerPos() {
            return controllerPos;
        }

        public Pair<BlockPos, BlockPos> topCorners() {
            return topCorners;
        }

        public List<BlockPos> peripherals() {
            return peripherals;
        }

        public int height() {
            return height;
        }

        public int volume() {
            return volume;
        }
    }
}

