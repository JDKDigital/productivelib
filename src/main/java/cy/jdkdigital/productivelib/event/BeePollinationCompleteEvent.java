package cy.jdkdigital.productivelib.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class BeePollinationCompleteEvent extends Event implements ICancellableEvent
{
    private final Level level;
    private final Bee beeEntity;
    private final BlockPos blockPos;

    public BeePollinationCompleteEvent(Level level, Bee beeEntity, BlockPos blockPos) {
        this.level = level;
        this.beeEntity = beeEntity;
        this.blockPos = blockPos;
    }

    public Level getLevel() {
        return this.level;
    }

    public Bee getBee() {
        return this.beeEntity;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }
}