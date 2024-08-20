package cy.jdkdigital.productivelib.loot;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.productivelib.ProductiveLib;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.Set;
import java.util.UUID;

public record LootItemKilledByUUIDCondition(UUID uuid) implements LootItemCondition
{
   public static MapCodec<LootItemKilledByUUIDCondition> CODEC = RecordCodecBuilder.mapCodec(
           builder -> builder
                   .group(UUIDUtil.CODEC.fieldOf("uuid").forGetter(LootItemKilledByUUIDCondition::uuid))
                   .apply(builder, LootItemKilledByUUIDCondition::new));

   @Override
   public LootItemConditionType getType() {
      return ProductiveLib.KILLED_BY_UUID.get();
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.LAST_DAMAGE_PLAYER);
   }

   @Override
   public boolean test(LootContext context) {
      if (context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER)) {
         return context.hasParam(LootContextParams.LAST_DAMAGE_PLAYER) && context.getParam(LootContextParams.LAST_DAMAGE_PLAYER).getUUID().equals(uuid);
      }
      return false;
   }

   public static Builder builder(final UUID uuid) {
      return new Builder(uuid);
   }

   public static class Builder implements LootItemCondition.Builder {
      private final UUID uuid;

      public Builder(UUID uuid) {
         this.uuid = uuid;
      }

      @Override
      public LootItemCondition build() {
         return new LootItemKilledByUUIDCondition(this.uuid);
      }
   }
}