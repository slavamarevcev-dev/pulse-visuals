package com.pulse.module.impl;

import com.pulse.Theme;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.Comparator;
import java.util.List;

public class ArrayListHud extends Module {
    public ArrayListHud() { super("ArrayList", "List of enabled modules", Category.HUD); setEnabled(true); }

    @Override
    public void onHudRender(DrawContext ctx, MinecraftClient mc) {
        List<Module> list = ModuleManager.all().stream()
                .filter(m -> m.isEnabled() && m != this)
                .sorted(Comparator.comparingInt((Module m) -> mc.textRenderer.getWidth(m.name)).reversed())
                .toList();
        int sw = ctx.getScaledWindowWidth();
        int y = 2;
        for (int i = 0; i < list.size(); i++) {
            Module m = list.get(i);
            int w = mc.textRenderer.getWidth(m.name);
            int x = sw - w - 6;
            ctx.fill(x - 2, y - 1, sw, y + 10, 0x90000000);
            ctx.fill(sw - 2, y - 1, sw, y + 10, Theme.accent(i));
            ctx.drawText(mc.textRenderer, m.name, x, y + 1, Theme.accent(i), true);
            y += 11;
        }
    }
}
