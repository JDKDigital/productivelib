package cy.jdkdigital.productivelib.util.harvest;

import com.mojang.authlib.GameProfile;
import cy.jdkdigital.productivelib.ProductiveLib;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public class MinecraftHarvester
{
    public static boolean isCropValid(Level level, BlockPos pos) {
        if (pos == null) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof CocoaBlock && state.getValue(CocoaBlock.AGE) == 2) {
            return true;
        }
        if (state.hasProperty(SweetBerryBushBlock.AGE) && state.getValue(SweetBerryBushBlock.AGE) == 3) {
            return true;
        }

        if (block instanceof AttachedStemBlock) {
            return true;
        }

        // Cactus and sugarcane blocks taller than 1 are harvestable
        if (block instanceof CactusBlock || block instanceof SugarCaneBlock) {
            return level.getBlockState(pos.below()).getBlock().equals(state.getBlock());
        }

        return block instanceof CropBlock && !((CropBlock) block).isValidBonemealTarget(level, pos, state);
    }

    public static void harvestBlock(Level level, BlockPos pos) {
        BlockState cropBlockState = level.getBlockState(pos);
        Block cropBlock = cropBlockState.getBlock();
        if (cropBlock instanceof AttachedStemBlock stemBlock) {
            BlockState fruitBlock = level.getBlockState(pos.relative(cropBlockState.getValue(HorizontalDirectionalBlock.FACING)));
            if (fruitBlock.is(stemBlock.fruit)) {
                level.destroyBlock(pos.relative(cropBlockState.getValue(HorizontalDirectionalBlock.FACING)), true);
            }
        } else if (cropBlock instanceof SugarCaneBlock || cropBlock instanceof CactusBlock) {
            int i = 0;
            while (i++ < 5 && level.getBlockState(pos.below()).getBlock().equals(cropBlock)) {
                pos = pos.below();
            }
            level.destroyBlock(pos.above(), true);
        } else if (cropBlock instanceof SweetBerryBushBlock) {
            int i = cropBlockState.getValue(SweetBerryBushBlock.AGE);
            if (i > 1) {
                int j = 1 + level.random.nextInt(2);
                var dropStack = cropBlock.getCloneItemStack(level, pos, cropBlockState);
                dropStack.setCount(j + (i == 3 ? 1 : 0));
                Block.popResource(level, pos, dropStack);
                level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 0.2F, 0.8F + level.random.nextFloat() * 0.4F);
                level.setBlock(pos, cropBlockState.setValue(SweetBerryBushBlock.AGE, 1), 2);
            }
        } else {
            // right click crop harvest
            Player fakePlayer = FakePlayerFactory.get((ServerLevel) level, new GameProfile(HarvestCompatHandler.FARMER_UUID, "productive_farmer"));
            cropBlockState.useWithoutItem(level, fakePlayer, new BlockHitResult(Vec3.ZERO, Direction.UP, pos, true));

            // If it's not harvested, destroy instead
            if (isCropValid(level, pos)) {
                level.destroyBlock(pos, true);
            }
        }
    }
}
