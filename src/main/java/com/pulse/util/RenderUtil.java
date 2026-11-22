package com.pulse.util;

import com.pulse.Theme;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public final class RenderUtil {
    public static void roundRect(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        if (w <= 0 || h <= 0) return;
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) { ctx.fill(x, y, x + w, y + h, color); return; }
        ctx.fill(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.round(r - Math.sqrt(r * r - dy * dy));
            ctx.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            ctx.fill(x + inset, y + h - i - 1, x + w - inset, y + h - i, color);
        }
    }

    public static void gradientH(DrawContext ctx, int x, int y, int w, int h, int c1, int c2) {
        for (int i = 0; i < w; i++) {
            ctx.fill(x + i, y, x + i + 1, y + h, Theme.lerp(c1, c2, i / (float) Math.max(1, w - 1)));
        }
    }

    public static void glow(DrawContext ctx, int x, int y, int w, int h, int r, int color) {
        for (int i = 4; i >= 1; i--) {
            roundRect(ctx, x - i, y - i, w + i * 2, h + i * 2, r + i, Theme.alpha(color, 0.07f));
        }
    }

    /** Текст, который не станет случайно непрозрачным при почти нулевой альфе. */
    public static void text(DrawContext ctx, TextRenderer tr, String s, int x, int y, int color, boolean shadow) {
        if (((color >>> 24) & 255) < 12) return;
        ctx.drawText(tr, s, x, y, color, shadow);
    }
}
