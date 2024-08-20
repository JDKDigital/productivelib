package cy.jdkdigital.productivelib.event;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.Event;

import java.util.ArrayList;
import java.util.List;

public class CollectValidUpgradesEvent extends Event
{
    private final BlockEntity blockEntity;
    private List<Item> validUpgrades;

    public CollectValidUpgradesEvent(BlockEntity blockEntity, List<Item> validUpgrades) {
        this.blockEntity = blockEntity;
        this.validUpgrades = new ArrayList<>(validUpgrades);
    }

    public BlockEntity getBlockEntity() {
        return blockEntity;
    }

    public List<Item> getValidUpgrades() {
        return validUpgrades;
    }

    public void setValidUpgrades(List<Item> validUpgrades) {
        this.validUpgrades = validUpgrades;
    }

    public void addValidUpgrade(Item item) {
        this.validUpgrades.add(item);
    }
}