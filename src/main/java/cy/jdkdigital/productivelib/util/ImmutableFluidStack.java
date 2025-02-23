package cy.jdkdigital.productivelib.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidStack;

public record ImmutableFluidStack(FluidStack fluid)
{
    public static ImmutableFluidStack EMPTY = new ImmutableFluidStack(FluidStack.EMPTY);
    public static final Codec<ImmutableFluidStack> CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(instance -> instance.group(FluidStack.CODEC.fieldOf("fluid").forGetter(ImmutableFluidStack::fluid)).apply(instance, ImmutableFluidStack::new))
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ImmutableFluidStack> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ImmutableFluidStack decode(RegistryFriendlyByteBuf buf) {
            FluidStack stack = FluidStack.OPTIONAL_STREAM_CODEC.decode(buf);
            if (stack.isEmpty()) {
                throw new DecoderException("Empty FluidStack not allowed");
            } else {
                return new ImmutableFluidStack(stack);
            }
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ImmutableFluidStack stack) {
            if (stack.fluid.isEmpty()) {
                throw new EncoderException("Empty FluidStack not allowed");
            } else {
                FluidStack.OPTIONAL_STREAM_CODEC.encode(buf, stack.fluid);
            }
        }
    };

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ImmutableFluidStack im && FluidStack.matches(im.fluid, this.fluid);
    }

    @Override
    public int hashCode() {
        return FluidStack.hashFluidAndComponents(fluid);
    }
}
