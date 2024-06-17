package cy.jdkdigital.productivelib;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = ProductiveLib.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // a list of strings that are treated as resource locations for items
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER
            .comment("A priority list of Mod IDs that results for recipe tag output should stem from, aka which mod you want the resource to come from.")
            .defineListAllowEmpty("items", List.of("minecraft", "productivebees", "productivetrees", "alltheores", "ato", "thermal", "tconstruct", "create", "immersiveengineering", "mekanism", "enderio", "silents_mechanisms"), o -> true);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static Set<String> modPreference;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        modPreference = new HashSet<>(ITEM_STRINGS.get());
    }
}
