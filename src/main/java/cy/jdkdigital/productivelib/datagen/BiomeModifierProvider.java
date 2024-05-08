package cy.jdkdigital.productivelib.datagen;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.world.BiomeModifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class BiomeModifierProvider implements DataProvider
{
    private final PackOutput output;
    private final String modid;
    private final Map<String, JsonElement> toSerialize = new HashMap<>();
    private boolean replace = false;

    public BiomeModifierProvider(PackOutput output, String modid)
    {
        this.output = output;
        this.modid = modid;
    }

    /**
     * Call {@link #add} here, which will pass in the necessary information to write the jsons.
     */
    protected abstract void start();

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        start();

        Path modifierFolderPath = this.output.getOutputFolder(PackOutput.Target.DATA_PACK).resolve(this.modid).resolve("forge/biome_modifier");
        List<ResourceLocation> entries = new ArrayList<>();

        ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();

        toSerialize.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, json) ->
        {
            entries.add(new ResourceLocation(modid, name));
            Path modifierPath = modifierFolderPath.resolve(name + ".json");
            futuresBuilder.add(DataProvider.saveStable(cache, json, modifierPath));
        }));

        return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
    }

    public <T extends BiomeModifier> void add(String modifier, T instance)
    {
        JsonElement json = BiomeModifier.DIRECT_CODEC.encodeStart(JsonOps.INSTANCE, instance).getOrThrow(false, s -> {});
        this.toSerialize.put(modifier, json);
    }

    @Override
    public String getName()
    {
        return "Biome Modifiers : " + modid;
    }
}
