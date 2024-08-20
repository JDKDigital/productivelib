package cy.jdkdigital.productivelib.common.item;

import cy.jdkdigital.productivelib.event.UpgradeTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
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
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        var event = NeoForge.EVENT_BUS.post(new UpgradeTooltipEvent(pStack, pContext, pTooltipComponents, null));

        if (event.getValidBlocks().size() > 0) {
            pTooltipComponents.add(Component.translatable("productivelib.information.upgrade.valid_blocks.list_header").withStyle(ChatFormatting.WHITE));

            event.getValidBlocks().forEach(component -> {
                pTooltipComponents.add(Component.translatable("productivelib.information.upgrade.valid_blocks.list_item", component.getString()).withStyle(ChatFormatting.GOLD));
            });
        }

        pTooltipComponents.add(Component.translatable("productivelib.information.upgrade.install_help").withStyle(ChatFormatting.GREEN));
    }
}
