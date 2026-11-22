package com.pulse.module.impl;

import com.pulse.Theme;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.util.Anim;
import com.pulse.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class KeystrokesHud extends Module {
    private final Anim[] anims = new Anim[7];

    public KeystrokesHud() {
        super("Keystrokes", "WASD, jump and mouse buttons", Category.HUD);
        for (int i = 0; i < anims.length; i++) anims[i] = new Anim(0f);
    }

    @Override
    public void onHudRender(DrawContext ctx, MinecraftClient mc) {
        TextRenderer tr = mc.textRenderer;
        int x = 8, y = ctx.getScaledWindowHeight() / 2 - 30;
        key(ctx, tr, 0, "W", mc.options.forwardKey.isPressed(), x + 22, y, 20, 20);
        key(ctx, tr, 1, "A", mc.options.leftKey.isPressed(), x, y + 22, 20, 20);
        key(ctx, tr, 2, "S", mc.options.backKey.isPressed(), x + 22, y + 22, 20, 20);
        key(ctx, tr, 3, "D", mc.options.rightKey.isPressed(), x + 44, y + 22, 20, 20);
        key(ctx, tr, 4, "LMB", mc.options.attackKey.isPressed(), x, y + 44, 31, 16);
        key(ctx, tr, 5, "RMB", mc.options.useKey.isPressed(), x + 33, y + 44, 31, 16);
        key(ctx, tr, 6, "SPACE", mc.options.jumpKey.isPressed(), x, y + 62, 64, 14);
    }

    private void key(DrawContext ctx, TextRenderer tr, int i, String label, boolean down,
                     int x, int y, int w, int h) {
        float v = anims[i].to(down ? 1f : 0f, 18f);
        int bg = Theme.lerp(0xB0101016, Theme.alpha(Theme.accent(i), 0.85f), v);
        RenderUtil.roundRect(ctx, x, y, w, h, 3, bg);
        int tc = Theme.lerp(Theme.TEXT_DIM, Theme.TEXT, v);
        int tx = x + (w - tr.getWidth(label)) / 2;
        int ty = y + (h - 8) / 2;
        RenderUtil.text(ctx, tr, label, tx, ty, tc, true);
    }
}
