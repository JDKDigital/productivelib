package cy.jdkdigital.productivelib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import cy.jdkdigital.productivelib.ProductiveLib;
import cy.jdkdigital.productivelib.event.BeeReleaseEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeehiveBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(value = BeehiveBlockEntity.class)
public class BeeHiveReleaseEventMixin
{
    @Inject(
        method = "releaseOccupant(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity$Occupant;Ljava/util/List;Lnet/minecraft/world/level/block/entity/BeehiveBlockEntity$BeeReleaseStatus;Lnet/minecraft/core/BlockPos;)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"),
        remap = false
    )
    private static void postBeeRelease(Level level, BlockPos pos, BlockState state, BeehiveBlockEntity.Occupant occupant, @Nullable List<Entity> releasedBees, BeehiveBlockEntity.BeeReleaseStatus beeStatus, @Nullable BlockPos flowerPos, CallbackInfoReturnable ci, @Local(ordinal = 0) Entity entity) {
        if (entity instanceof Bee bee) {
            NeoForge.EVENT_BUS.post(new BeeReleaseEvent(level, bee, level.getBlockEntity(pos), state, beeStatus));
        }
    }
}
