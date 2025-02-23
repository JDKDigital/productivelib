package cy.jdkdigital.productivelib.exception;

import net.minecraft.core.BlockPos;

public class InvalidStructureException extends Exception
{
    private final BlockPos pos;

    public InvalidStructureException(BlockPos pos) {
        this("Invalid or missing structure block", pos);
    }

    public InvalidStructureException(String message, BlockPos pos) {
        super(message);
        this.pos = pos;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " at " + pos;
    }

    public BlockPos getPos() {
        return pos;
    }
}
