package cy.jdkdigital.productivelib.exception;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class InvalidStructureException extends Exception
{
    private final BlockPos pos;
    private final BlockState state;

    public InvalidStructureException(BlockPos pos) {
        this("Invalid or missing structure block", pos);
    }

    public InvalidStructureException(String message, BlockPos pos) {
        this(message, pos, null);
    }

    public InvalidStructureException(String message, BlockPos pos, BlockState state) {
        super(message);
        this.pos = pos;
        this.state = state;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " at " + "[" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + "]" + (this.state != null ? " found " + this.state.getBlock() : "");
    }

    public BlockPos getPos() {
        return pos;
    }
}
