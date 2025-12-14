package cy.jdkdigital.productivelib.util.harvest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class HarvestCompatHandler
{
    public static final UUID FARMER_UUID = UUID.nameUUIDFromBytes("productive_farmer".getBytes(StandardCharsets.UTF_8));

    public static boolean isCropValid(Level level, BlockPos pos) {
        boolean isValid = MinecraftHarvester.isCropValid(level, pos);
        if (!isValid && ModList.get().isLoaded("pamhc2trees")) {
            isValid = PamsHarvester.isCropValid(level, pos);
        }
        return isValid;
    }

    public static boolean harvestBlock(Level level, BlockPos pos) {
        boolean hasHarvested = false;
        if (MinecraftHarvester.isCropValid(level, pos)) {
            MinecraftHarvester.harvestBlock(level, pos);
            hasHarvested = true;
        }
        if (!hasHarvested && ModList.get().isLoaded("pamhc2trees")) {
            if (PamsHarvester.isCropValid(level, pos)) {
                PamsHarvester.harvestBlock(level, pos);
                hasHarvested = true;
            }
        }
        return hasHarvested;
    }
}
