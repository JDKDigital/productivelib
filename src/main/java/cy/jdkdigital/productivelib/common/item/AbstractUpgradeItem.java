package cy.jdkdigital.productivelib.common.item;

import cy.jdkdigital.productivelib.common.block.entity.UpgradeableBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractUpgradeItem extends Item
{
    public AbstractUpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (!world.isClientSide && context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            if (context.getItemInHand().getItem() instanceof AbstractUpgradeItem) {
                BlockEntity tileEntity = world.getBlockEntity(context.getClickedPos());
                if (tileEntity instanceof UpgradeableBlockEntity && ((UpgradeableBlockEntity) tileEntity).acceptsUpgrades()) {
                    AtomicBoolean hasInsertedUpgrade = new AtomicBoolean(false);
                    ((UpgradeableBlockEntity) tileEntity).getUpgradeHandler().ifPresent(handler -> {
                        for (int slot = 0; slot < handler.getSlots(); ++slot) {
                            if (handler.getStackInSlot(slot).equals(ItemStack.EMPTY)) {
                                handler.insertItem(slot, new ItemStack(context.getItemInHand().getItem()), false);
                                hasInsertedUpgrade.set(true);
                                break;
                            }
                        }
                    });
                    if (hasInsertedUpgrade.get()) {
                        context.getPlayer().swing(context.getHand());
                        if (!context.getPlayer().isCreative()) {
                            context.getItemInHand().shrink(1);
                        }
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return super.useOn(context);
    }
}
