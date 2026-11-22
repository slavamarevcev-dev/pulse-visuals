package com.pulse;

public final class Theme {
    public static final int BG = 0xE0121218;
    public static final int PANEL = 0xF01B1B26;
    public static final int ROW = 0xA0202030;
    public static final int ROW_HOVER = 0xB0303048;
    public static final int TEXT = 0xFFFFFFFF;
    public static final int TEXT_DIM = 0xFFA0A0B8;
    private static final int A = 0xFF8B5CF6, B = 0xFFEC4899;

    public static int accent(int index) {
        float t = (float) ((Math.sin(System.currentTimeMillis() / 600.0 + index * 0.35) + 1) / 2);
        return lerp(A, B, t);
    }

    public static int lerp(int c1, int c2, float t) {
        int a = (int) (((c1 >>> 24) & 255) * (1 - t) + ((c2 >>> 24) & 255) * t);
        int r = (int) (((c1 >> 16) & 255) * (1 - t) + ((c2 >> 16) & 255) * t);
        int g = (int) (((c1 >> 8) & 255) * (1 - t) + ((c2 >> 8) & 255) * t);
        int b = (int) ((c1 & 255) * (1 - t) + (c2 & 255) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
