package com.pulse.module.impl;

import com.pulse.Theme;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.module.ModuleManager;
import com.pulse.module.setting.BooleanSetting;
import com.pulse.util.Anim;
import com.pulse.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayListHud extends Module {
    public final BooleanSetting showHud = add(new BooleanSetting("Show HUD mods", false));
    private final Map<Module, Anim> anims = new HashMap<>();

    private record Entry(Module m, float a, int w) {}

    public ArrayListHud() { super("ArrayList", "Animated list of enabled modules", Category.HUD); setEnabled(true); }

    @Override
    public void onHudRender(DrawContext ctx, MinecraftClient mc) {
        TextRenderer tr = mc.textRenderer;
        List<Entry> list = new ArrayList<>();
        for (Module m : ModuleManager.all()) {
            if (m == this) continue;
            if (!showHud.value && m.category == Category.HUD) continue;
            Anim an = anims.computeIfAbsent(m, k -> new Anim(0f));
            float a = an.to(m.isEnabled() ? 1f : 0f, 12f);
            if (a < 0.02f) continue;
            list.add(new Entry(m, a, tr.getWidth(m.name)));
        }
        list.sort((p, q) -> Integer.compare(q.w(), p.w()));

        int sw = ctx.getScaledWindowWidth();
        float y = 4;
        int i = 0;
        for (Entry e : list) {
            int w = e.w();
            int off = Math.round((1f - e.a()) * (w + 12));
            int x = sw - w - 10 + off;
            int iy = Math.round(y);
            RenderUtil.roundRect(ctx, x - 4, iy, w + 14, 13, 3, Theme.alpha(Theme.BG, e.a()));
            ctx.fill(sw - 2 + off, iy + 2, sw + off, iy + 11, Theme.alpha(Theme.accent(i), e.a()));
            RenderUtil.text(ctx, tr, e.m().name, x, iy + 3, Theme.alpha(Theme.accent(i), e.a()), true);
            y += 14 * e.a();
            i++;
        }
    }
}
