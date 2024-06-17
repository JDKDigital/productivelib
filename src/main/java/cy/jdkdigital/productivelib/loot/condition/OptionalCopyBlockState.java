package cy.jdkdigital.productivelib.loot.condition;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivelib.ProductiveLib;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OptionalCopyBlockState extends LootItemConditionalFunction
{
    public static final MapCodec<OptionalCopyBlockState> CODEC = RecordCodecBuilder.mapCodec(
            builder -> commonFields(builder)
                    .and(
                            builder.group(
                                    BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("block").forGetter(optionalCopyBlockState -> optionalCopyBlockState.block),
                                    Codec.STRING.listOf().fieldOf("properties").orElse(List.of()).forGetter(optionalCopyBlockState -> optionalCopyBlockState.properties.stream().map(Property::getName).toList())
                            )
                    )
                    .apply(builder, OptionalCopyBlockState::new)
    );

    private final Holder<Block> block;
    private final Set<Property<?>> properties;

    OptionalCopyBlockState(List<LootItemCondition> lootItemConditions, Holder<Block> pBlock, Set<Property<?>> pStatePredicate) {
        super(lootItemConditions);
        this.block = pBlock;
        this.properties = pStatePredicate;
    }

    private OptionalCopyBlockState(List<LootItemCondition> lootItemConditions, Holder<Block> pBlock, List<String> pStatePredicate) {
        this(lootItemConditions, pBlock, pStatePredicate.stream().map(pBlock.value().getStateDefinition()::getProperty).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    @Override
    public LootItemFunctionType getType() {
        return ProductiveLib.OPTIONAL_BLOCK_STATE_PROPERTY.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParams.BLOCK_STATE);
    }

    @Override
    protected ItemStack run(ItemStack itemStack, LootContext p_80061_) {
        BlockState blockstate = p_80061_.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (blockstate != null) {
            itemStack.update(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY, stateProperties -> {
                for (Property<?> property : this.properties) {
                    if (blockstate.hasProperty(property)) {
                        stateProperties = stateProperties.with(property, blockstate);
                    }
                }

                return stateProperties;
            });
        }

        return itemStack;
    }

    public static OptionalCopyBlockState.Builder copyState(Block block) {
        return new OptionalCopyBlockState.Builder(block);
    }

    public static class Builder extends LootItemConditionalFunction.Builder<OptionalCopyBlockState.Builder> {
        private final Holder<Block> block;
        private final ImmutableSet.Builder<Property<?>> properties = ImmutableSet.builder();

        Builder(Block block) {
            this.block = block.builtInRegistryHolder();
        }

        public Builder copy(Property<?> p_80085_) {
            if (!this.block.value().getStateDefinition().getProperties().contains(p_80085_)) {
                throw new IllegalStateException("Property " + p_80085_ + " is not present on block " + this.block);
            } else {
                this.properties.add(p_80085_);
                return this;
            }
        }

        protected Builder getThis() {
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new OptionalCopyBlockState(this.getConditions(), this.block, this.properties.build());
        }
    }
}
