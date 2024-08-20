package cy.jdkdigital.productivelib.common.item;

import cy.jdkdigital.productivelib.ProductiveLib;
import cy.jdkdigital.productivelib.event.AddEntityToFilterEvent;
import cy.jdkdigital.productivelib.event.UpgradeTooltipEvent;
import cy.jdkdigital.productivelib.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FilterUpgradeItem extends UpgradeItem
{
    public FilterUpgradeItem(Properties properties) {
        super(properties);
    }

    public static void addAllowedEntity(ItemStack stack, ResourceLocation entity) {
        List<ResourceLocation> list = stack.getOrDefault(ModDataComponents.ENTITY_TYPE_LIST, new ArrayList<>());
        if (!list.contains(entity)) {
            var newList = new ArrayList<>(list);
            newList.add(entity);
            stack.set(ModDataComponents.ENTITY_TYPE_LIST, newList);
        }
    }

    @Override
    public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents, TooltipFlag pTooltipFlag) {
        super.appendHoverText(pStack, pContext, pTooltipComponents, pTooltipFlag);

        List<ResourceLocation> entities = pStack.getOrDefault(ModDataComponents.ENTITY_TYPE_LIST, new ArrayList<>());

        if (entities.size() > 0) {
            pTooltipComponents.add(Component.translatable("productivelib.information.upgrade.upgrade_entity_filter.list_header").withStyle(ChatFormatting.WHITE));

            var event = NeoForge.EVENT_BUS.post(new UpgradeTooltipEvent(pStack, pContext, pTooltipComponents, new ArrayList<>(entities)));
            if (event.getEntities() != null) {
                event.getEntities().forEach(id -> {
                    pTooltipComponents.add(Component.translatable("productivelib.information.upgrade.upgrade_entity_filter.list_item", Component.translatable("entity." + id.getNamespace() + "." + id.getPath()).getString()).withStyle(ChatFormatting.GOLD));
                });
            }
        }

        if (entities.isEmpty()) {
            pTooltipComponents.add(Component.translatable("productivelib.information.upgrade.upgrade_entity_filter.empty").withStyle(ChatFormatting.WHITE));
        } else {
            pTooltipComponents.add(Component.translatable("productivelib.information.upgrade.upgrade_entity_filter.clear").withStyle(ChatFormatting.WHITE));
        }
    }

    @Nonnull
    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity targetIn, InteractionHand hand) {
        if (targetIn.getCommandSenderWorld().isClientSide()) {
            return InteractionResult.PASS;
        }

        var event = NeoForge.EVENT_BUS.post(new AddEntityToFilterEvent(targetIn.getCommandSenderWorld(), targetIn, BuiltInRegistries.ENTITY_TYPE.getKey(targetIn.getType())));
        if (!event.isCanceled()) {
            addAllowedEntity(itemStack, event.getKey());
            player.setItemInHand(hand, itemStack);
        }

        return InteractionResult.SUCCESS;
    }
}
