package cy.jdkdigital.productivelib.common.item;

import cy.jdkdigital.productivelib.event.UpgradeTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.NeoForge;

import java.util.List;

public class UpgradeItem extends AbstractUpgradeItem
{
    public UpgradeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> tooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, tooltipComponents, pTooltipFlag);

        var event = NeoForge.EVENT_BUS.post(new UpgradeTooltipEvent(pStack, pContext, tooltipComponents, null));

        if (!pTooltipFlag.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("productivelib.information.upgrade.extend").withStyle(ChatFormatting.DARK_GRAY));
        }

        if (!event.getValidBlocks().isEmpty()) {
            tooltipComponents.add(Component.translatable("productivelib.information.upgrade.valid_blocks.list_header").withStyle(ChatFormatting.WHITE));

            event.getValidBlocks().forEach(component -> {
                tooltipComponents.add(Component.translatable("productivelib.information.upgrade.valid_blocks.list_item", component.blockName()).withStyle(ChatFormatting.GOLD));
                if (pTooltipFlag.hasShiftDown()) {
                    event.getTooltipComponents().add(Component.translatable("productivelib.information.upgrade.valid_blocks.list_item_content", Component.translatable(component.translationKey(), component.value()).getString()).withStyle(ChatFormatting.GRAY));
                }
            });
        }

        tooltipComponents.add(Component.translatable("productivelib.information.upgrade.install_help").withStyle(ChatFormatting.GREEN));
    }
}
