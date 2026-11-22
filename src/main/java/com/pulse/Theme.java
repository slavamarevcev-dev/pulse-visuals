package com.pulse;

import com.pulse.module.impl.Colors;
import net.minecraft.util.math.MathHelper;

public final class Theme {
    public static Colors colors;

    public static final int BG = 0xE60F0F16;
    public static final int PANEL = 0xF2161620;
    public static final int TEXT = 0xFFFFFFFF;
    public static final int TEXT_DIM = 0xFF9A9AB4;
    public static final int TRACK = 0xFF2A2A3C;

    private static final int[][] PRESETS = {
            {0xFF8B5CF6, 0xFFEC4899},
            {0xFF06B6D4, 0xFF3B82F6},
            {0xFF22C55E, 0xFFEAB308},
            {0xFFEF4444, 0xFFF97316}
    };

    public static int accent(int index) {
        long t = System.currentTimeMillis();
        boolean on = colors != null && colors.isEnabled();
        if (on && colors.rainbow.value) {
            float h = (float) (((t * colors.speed.value / 4000.0) + index * 0.03) % 1.0);
            return 0xFF000000 | MathHelper.hsvToRgb(h, 0.65f, 1.0f);
        }
        int p = on ? (int) colors.preset.value - 1 : 0;
        p = Math.max(0, Math.min(PRESETS.length - 1, p));
        float s = (float) ((Math.sin(t / 700.0 + index * 0.35) + 1) / 2);
        return lerp(PRESETS[p][0], PRESETS[p][1], s);
    }

    public static int alpha(int c, float a) {
        int al = (int) (((c >>> 24) & 255) * Math.max(0f, Math.min(1f, a)));
        return (al << 24) | (c & 0xFFFFFF);
    }

    public static int lerp(int c1, int c2, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int a = (int) (((c1 >>> 24) & 255) * (1 - t) + ((c2 >>> 24) & 255) * t);
        int r = (int) (((c1 >> 16) & 255) * (1 - t) + ((c2 >> 16) & 255) * t);
        int g = (int) (((c1 >> 8) & 255) * (1 - t) + ((c2 >> 8) & 255) * t);
        int b = (int) ((c1 & 255) * (1 - t) + (c2 & 255) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
