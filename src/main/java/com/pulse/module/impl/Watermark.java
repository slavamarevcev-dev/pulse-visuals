package com.pulse.module.impl;

import com.pulse.Theme;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.module.setting.BooleanSetting;
import com.pulse.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public class Watermark extends Module {
    public final BooleanSetting fps = add(new BooleanSetting("FPS", true));
    public final BooleanSetting ping = add(new BooleanSetting("Ping", true));

    public Watermark() { super("Watermark", "Client name bar", Category.HUD); setEnabled(true); }

    @Override
    public void onHudRender(DrawContext ctx, MinecraftClient mc) {
        TextRenderer tr = mc.textRenderer;
        int p = 0;
        if (mc.getNetworkHandler() != null) {
            PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (e != null) p = e.getLatency();
        }
        String name = "Pulse";
        String rest = "";
        if (fps.value) rest += "   " + mc.getCurrentFps() + " fps";
        if (ping.value) rest += "   " + p + " ms";

        int w = tr.getWidth(name) + tr.getWidth(rest) + 16;
        int x = 6, y = 6, h = 16;
        RenderUtil.glow(ctx, x, y, w, h, 4, Theme.accent(0));
        RenderUtil.roundRect(ctx, x, y, w, h, 4, Theme.BG);
        RenderUtil.gradientH(ctx, x + 4, y, w - 8, 1, Theme.accent(0), Theme.accent(8));
        RenderUtil.text(ctx, tr, name, x + 8, y + 4, Theme.accent(2), true);
        RenderUtil.text(ctx, tr, rest, x + 8 + tr.getWidth(name), y + 4, Theme.TEXT_DIM, true);
    }
}
