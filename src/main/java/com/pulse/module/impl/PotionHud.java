package com.pulse.module.impl;

import com.pulse.Theme;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.List;

public class PotionHud extends Module {
    public PotionHud() { super("PotionHud", "Active potion effects", Category.HUD); }

    @Override
    public void onHudRender(DrawContext ctx, MinecraftClient mc) {
        TextRenderer tr = mc.textRenderer;
        List<String[]> rows = new ArrayList<>();
        for (StatusEffectInstance e : mc.player.getStatusEffects()) {
            String name = e.getEffectType().value().getName().getString();
            if (e.getAmplifier() > 0) name += " " + (e.getAmplifier() + 1);
            int d = e.getDuration();
            String time = d < 0 ? "inf" : (d / 20 / 60) + ":" + String.format("%02d", (d / 20) % 60);
            rows.add(new String[]{name, time});
        }
        if (rows.isEmpty()) return;

        int nameW = 0, timeW = 0;
        for (String[] r : rows) {
            nameW = Math.max(nameW, tr.getWidth(r[0]));
            timeW = Math.max(timeW, tr.getWidth(r[1]));
        }
        int w = nameW + timeW + 24;
        int h = rows.size() * 11 + 7;
        int x = ctx.getScaledWindowWidth() - w - 6;
        int y = ctx.getScaledWindowHeight() - h - 6;
        RenderUtil.glow(ctx, x, y, w, h, 4, Theme.accent(6));
        RenderUtil.roundRect(ctx, x, y, w, h, 4, Theme.BG);
        RenderUtil.roundRect(ctx, x + w - 4, y + 3, 2, h - 6, 1, Theme.accent(7));
        int ry = y + 5;
        for (String[] r : rows) {
            RenderUtil.text(ctx, tr, r[0], x + 8, ry, Theme.TEXT, true);
            RenderUtil.text(ctx, tr, r[1], x + w - 10 - tr.getWidth(r[1]), ry, Theme.TEXT_DIM, true);
            ry += 11;
        }
    }
}
