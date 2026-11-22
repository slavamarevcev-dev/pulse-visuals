package com.pulse.module.impl;

import com.pulse.Theme;
import com.pulse.module.Category;
import com.pulse.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

public class Watermark extends Module {
    public Watermark() { super("Watermark", "Client name, FPS, ping", Category.HUD); setEnabled(true); }

    @Override
    public void onHudRender(DrawContext ctx, MinecraftClient mc) {
        int ping = 0;
        if (mc.getNetworkHandler() != null) {
            PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            if (e != null) ping = e.getLatency();
        }
        String name = "Pulse";
        String rest = " | " + mc.getCurrentFps() + " fps | " + ping + " ms";
        int w = mc.textRenderer.getWidth(name + rest) + 10;
        ctx.fill(4, 4, 4 + w, 18, Theme.BG);
        ctx.fill(4, 4, 4 + w, 5, Theme.accent(0));
        ctx.drawText(mc.textRenderer, name, 9, 8, Theme.accent(2), true);
        ctx.drawText(mc.textRenderer, rest, 9 + mc.textRenderer.getWidth(name), 8, Theme.TEXT, true);
    }
}
