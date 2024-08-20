package cy.jdkdigital.productivelib;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // a list of strings that are treated as resource locations for items
    public static final ModConfigSpec.ConfigValue<List<? extends String>> MOD_PREFERENCE = BUILDER
            .comment("A priority list of Mod IDs that results for recipe tag output should stem from, aka which mod you want the resource to come from.")
            .defineListAllowEmpty("modPreference", List.of("productivebees", "productivetrees", "alltheores", "ato", "thermal", "tconstruct", "create", "immersiveengineering", "mekanism", "enderio", "silents_mechanisms"), o -> true);

    static final ModConfigSpec SPEC = BUILDER.build();
}
