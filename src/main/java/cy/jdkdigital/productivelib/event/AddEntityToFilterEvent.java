package cy.jdkdigital.productivelib.event;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public class AddEntityToFilterEvent extends Event implements ICancellableEvent
{
    private final Level level;
    private final LivingEntity entity;
    private ResourceLocation key;

    public AddEntityToFilterEvent(Level level, LivingEntity entity, ResourceLocation key) {
        this.level = level;
        this.entity = entity;
        this.key = key;
    }

    public Level getLevel() {
        return level;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public ResourceLocation getKey() {
        return key;
    }

    public void setKey(ResourceLocation key) {
        this.key = key;
    }
}