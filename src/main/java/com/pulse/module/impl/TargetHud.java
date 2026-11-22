package com.pulse.module.impl;

import com.pulse.Theme;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.util.Anim;
import com.pulse.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;

public class TargetHud extends Module {
    private LivingEntity last;
    private long lastSeen;
    private final Anim hp = new Anim(1f);

    public TargetHud() { super("TargetHud", "Health of the entity you look at", Category.HUD); }

    @Override
    public void onHudRender(DrawContext ctx, MinecraftClient mc) {
        long now = System.currentTimeMillis();
        if (mc.targetedEntity instanceof LivingEntity le && le != mc.player) {
            last = le;
            lastSeen = now;
        }
        if (last == null || !last.isAlive() || now - lastSeen > 1500) { last = null; return; }

        float a = 1f - Math.max(0f, (now - lastSeen - 1000) / 500f);
        TextRenderer tr = mc.textRenderer;
        float max = Math.max(1f, last.getMaxHealth());
        float frac = Math.max(0f, Math.min(1f, last.getHealth() / max));
        float shown = hp.to(frac, 10f);

        int w = 116, h = 32;
        int x = ctx.getScaledWindowWidth() / 2 + 16, y = ctx.getScaledWindowHeight() / 2 + 16;
        RenderUtil.glow(ctx, x, y, w, h, 4, Theme.alpha(Theme.accent(1), a));
        RenderUtil.roundRect(ctx, x, y, w, h, 4, Theme.alpha(Theme.BG, a));
        RenderUtil.text(ctx, tr, last.getName().getString(), x + 8, y + 5, Theme.alpha(Theme.TEXT, a), true);
        String hs = String.format("%.1f / %.1f", last.getHealth(), max);
        RenderUtil.text(ctx, tr, hs, x + w - 8 - tr.getWidth(hs), y + 5, Theme.alpha(Theme.TEXT_DIM, a), true);
        int bx = x + 8, by = y + 20, bw = w - 16;
        RenderUtil.roundRect(ctx, bx, by, bw, 5, 2, Theme.alpha(Theme.TRACK, a));
        int fw = Math.max(2, Math.round(bw * shown));
        RenderUtil.roundRect(ctx, bx, by, fw, 5, 2, Theme.alpha(Theme.accent(2), a));
    }
}
