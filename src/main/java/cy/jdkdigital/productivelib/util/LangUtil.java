package cy.jdkdigital.productivelib.util;

import java.util.HashMap;
import java.util.Map;

public class LangUtil
{
    private static final Map<String, String> pluralMap = new HashMap<>() {{
        put("Elderberry", "Elderberries");
        put("Sloe", "Sloe");
        put("Haw", "Haw");
        put("Berry", "Berries");
        put("Cherry", "Cherries");
        put("Peach", "Peaches");
        put("Copoazu", "Copoazu");
        put("Cempedak", "Cempedak");
    }};

    public static String capName(String name) {
        String[] nameParts = name.split("_");

        for (int i = 0; i < nameParts.length; i++) {
            nameParts[i] = nameParts[i].substring(0, 1).toUpperCase() + nameParts[i].substring(1);
        }

        return String.join(" ", nameParts);
    }

    public static String pluralCapName(String name) {
        String[] capNameParts = capName(name).split(" ");

        int i = capNameParts.length - 1;
        capNameParts[i] = pluralMap.containsKey(capNameParts[i]) ? pluralMap.get(capNameParts[i]) : capNameParts[i] + "s";

        return String.join(" ", capNameParts);
    }
}
