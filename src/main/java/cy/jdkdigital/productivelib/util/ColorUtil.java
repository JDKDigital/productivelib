package cy.jdkdigital.productivelib.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;

import java.util.HashMap;
import java.util.Map;

public class ColorUtil
{
    // Color calc cache
    private static final Map<Integer, float[]> colorCache = new HashMap<>();
    private static final Map<String, Integer> stringColorCache = new HashMap<>();

    public static Integer getCacheColor(String color) {
        if (!stringColorCache.containsKey(color)) {
            stringColorCache.put(color, TextColor.parseColor(color).getOrThrow().getValue());
        }
        return stringColorCache.get(color);
    }

    public static float[] getCacheColor(int color) {
        if (!colorCache.containsKey(color)) {
            colorCache.put(color, ColorUtil.getComponents(color));
        }
        return colorCache.get(color);
    }

    public static float[] getComponents(int color) {
        float[] f = new float[4];
        f[0] = (float) ((color >> 16) & 0xFF)/255f;
        f[1] = (float) ((color >> 8) & 0xFF)/255f;
        f[2] = (float) (color & 0xFF)/255f;
        f[3] = (float) ((color >> 24) & 0xff)/255f;

        return f;
    }

    public static float[] getCycleColor(int color, int color2, int tickCount, float partialTicks) {
        float f3 = ((float)(tickCount % 25) + partialTicks) / 25.0F;
        float[] afloat1 = ColorUtil.getCacheColor(color);
        float[] afloat2 = ColorUtil.getCacheColor(color2);
        var f = afloat1[0] * (1.0F - f3) + afloat2[0] * f3;
        var f1 = afloat1[1] * (1.0F - f3) + afloat2[1] * f3;
        var f2 = afloat1[2] * (1.0F - f3) + afloat2[2] * f3;
        return new float[]{f, f1, f2};
    }

    public static ChatFormatting getBeeTypeColor(String type) {
        return switch (type) {
            case "hive" -> ChatFormatting.YELLOW;
            case "solitary" -> ChatFormatting.GRAY;
            default -> ChatFormatting.WHITE;
        };
    }

    public static ChatFormatting getAttributeColor(int level) {
        return switch (level) {
            case -3 -> ChatFormatting.GOLD;
            case -2 -> ChatFormatting.DARK_RED;
            case -1 -> ChatFormatting.YELLOW;
            case 1 -> ChatFormatting.BLUE;
            case 2 -> ChatFormatting.LIGHT_PURPLE;
            case 3 -> ChatFormatting.RED;
            default -> ChatFormatting.GREEN;
        };
    }

    public static int blend(int a, int b, float f) {
        int aA = (a >> 24 & 0xff);
        int aR = ((a & 0xff0000) >> 16);
        int aG = ((a & 0xff00) >> 8);
        int aB = (a & 0xff);

        int bA = (b >> 24 & 0xff);
        int bR = ((b & 0xff0000) >> 16);
        int bG = ((b & 0xff00) >> 8);
        int bB = (b & 0xff);

        int A = (int)((aA * (1.0f - f)) + (bA * f));
        int R = (int)((aR * (1.0f - f)) + (bR * f));
        int G = (int)((aG * (1.0f - f)) + (bG * f));
        int B = (int)((aB * (1.0f - f)) + (bB * f));

        return A << 24 | R << 16 | G << 8 | B;
    }
}
