package com.pulse.gui;

import com.pulse.Theme;
import com.pulse.config.ConfigManager;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.module.ModuleManager;
import com.pulse.module.impl.Blur;
import com.pulse.module.setting.BooleanSetting;
import com.pulse.module.setting.NumberSetting;
import com.pulse.module.setting.Setting;
import com.pulse.util.Anim;
import com.pulse.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClickGuiScreen extends Screen {
    private static final int PW = 124, HH = 22, MH = 18, SH = 17;

    private final List<Panel> panels = new ArrayList<>();
    private final Anim open = new Anim(0f);
    private final int oldBlur;
    private boolean closing, bgDrawn;

    public ClickGuiScreen() {
        super(Text.literal("Pulse"));
        oldBlur = MinecraftClient.getInstance().options.getMenuBackgroundBlurriness().getValue();
        int x = 24;
        for (Category c : Category.values()) {
            panels.add(new Panel(c, x, 28));
            x += PW + 12;
        }
    }

    @Override public boolean shouldPause() { return false; }

    /** ESC: плавно закрываем, окончательно закроемся в render(). */
    @Override public void close() { closing = true; }

    @Override
    public void removed() {
        MinecraftClient.getInstance().options.getMenuBackgroundBlurriness().setValue(oldBlur);
        ConfigManager.save();
    }

    /** Размытие: ванильный blur-шейдер меню, сила берётся из модуля Blur. */
    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        bgDrawn = true;
        Blur b = ModuleManager.get(Blur.class);
        if (b != null && b.isEnabled()) super.renderBackground(ctx, mx, my, delta);
    }

    private void updateBlur() {
        Blur b = ModuleManager.get(Blur.class);
        int want = (b != null && b.isEnabled()) ? (int) b.strength.value : 0;
        var opt = MinecraftClient.getInstance().options.getMenuBackgroundBlurriness();
        int cur = opt.getValue();
        if (cur != want) opt.setValue(want);
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        float a = open.to(closing ? 0f : 1f, 14f);
        if (closing && a <= 0.02f) { super.close(); return; }

        updateBlur();
        if (!bgDrawn) renderBackground(ctx, mx, my, delta);
        bgDrawn = false;
        ctx.fill(0, 0, width, height, Theme.alpha(0x66000000, a));

        int yo = Math.round((1f - a) * 14f);
        for (Panel p : panels) {
            p.y += yo;
            p.render(ctx, textRenderer, mx, my, a);
            p.y -= yo;
        }
        String hint = "LMB: toggle   RMB: settings   Drag header to move   ESC: close";
        RenderUtil.text(ctx, textRenderer, hint, (width - textRenderer.getWidth(hint)) / 2,
                height - 14, Theme.alpha(Theme.TEXT_DIM, a), true);
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mx = click.x(), my = click.y();
        int btn = click.button();
        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel p = panels.get(i);
            if (p.click(mx, my, btn)) {
                panels.remove(i);
                panels.add(p);
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(Click click) {
        for (Panel p : panels) p.release();
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double dx, double dy) {
        for (Panel p : panels) p.drag(click.x(), click.y());
        return super.mouseDragged(click, dx, dy);
    }

    private static boolean in(double mx, double my, int x, int y, int w, int h) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

    private static String fmt(NumberSetting n) {
        return n.step >= 1 ? String.valueOf((int) n.value) : String.format("%.1f", n.value);
    }

    private static class Row {
        final Module m;
        final Anim en, ex, hov = new Anim(0f);
        final Map<Setting, Anim> anims = new HashMap<>();

        Row(Module m) {
            this.m = m;
            en = new Anim(m.isEnabled() ? 1f : 0f);
            ex = new Anim(m.expanded ? 1f : 0f);
            for (Setting s : m.settings) {
                if (s instanceof BooleanSetting b) anims.put(s, new Anim(b.value ? 1f : 0f));
                else if (s instanceof NumberSetting n)
                    anims.put(s, new Anim((float) ((n.value - n.min) / (n.max - n.min))));
            }
        }

        int settingsH() { return Math.round(m.settings.size() * SH * ex.v); }
    }

    private static class Panel {
        final Category cat;
        final List<Row> rows = new ArrayList<>();
        final Anim pOpen = new Anim(1f);
        int x, y, dragDX, dragDY;
        boolean dragging, open = true;
        NumberSetting slider;

        Panel(Category cat, int x, int y) {
            this.cat = cat; this.x = x; this.y = y;
            for (Module m : ModuleManager.all()) if (m.category == cat) rows.add(new Row(m));
        }

        int contentH() {
            int h = 0;
            for (Row r : rows) h += MH + r.settingsH();
            return h;
        }

        int visH() { return Math.round(contentH() * pOpen.v); }

        int totalH() { int v = visH(); return HH + v + (v > 0 ? 4 : 0); }

        void render(DrawContext ctx, TextRenderer tr, int mx, int my, float a) {
            for (Row r : rows) {
                r.ex.to(r.m.expanded ? 1f : 0f, 14f);
                r.en.to(r.m.isEnabled() ? 1f : 0f, 14f);
            }
            pOpen.to(open ? 1f : 0f, 14f);
            int vis = visH();
            int total = totalH();

            RenderUtil.glow(ctx, x, y, PW, total, 5, Theme.alpha(Theme.accent(0), a));
            RenderUtil.roundRect(ctx, x, y, PW, total, 5, Theme.alpha(Theme.PANEL, a));
            int hc = Theme.alpha(Theme.BG, a);
            RenderUtil.roundRect(ctx, x, y, PW, HH, 5, hc);
            ctx.fill(x, y + 8, x + PW, y + HH, hc);
            RenderUtil.gradientH(ctx, x + 6, y + HH - 1, PW - 12, 1,
                    Theme.alpha(Theme.accent(0), a), Theme.alpha(Theme.accent(6), a));
            RenderUtil.text(ctx, tr, cat.label, x + 9, y + 7, Theme.alpha(Theme.accent(1), a), true);
            RenderUtil.text(ctx, tr, open ? "-" : "+", x + PW - 13, y + 7, Theme.alpha(Theme.TEXT, a), true);
            if (vis <= 0) return;

            ctx.enableScissor(x, y + HH, x + PW, y + total - 2);
            int cy = y + HH + 2;
            int idx = 0;
            for (Row r : rows) {
                Module m = r.m;
                boolean hover = in(mx, my, x + 4, cy, PW - 8, MH - 2);
                float h = r.hov.to(hover ? 1f : 0f, 16f);
                RenderUtil.roundRect(ctx, x + 4, cy, PW - 8, MH - 2, 3,
                        Theme.alpha(Theme.lerp(0x00FFFFFF, 0x26FFFFFF, h), a));
                if (r.en.v > 0.01f) {
                    RenderUtil.roundRect(ctx, x + 4, cy, PW - 8, MH - 2, 3,
                            Theme.alpha(Theme.accent(idx), 0.55f * r.en.v * a));
                }
                int tc = Theme.lerp(Theme.TEXT_DIM, Theme.TEXT, r.en.v);
                RenderUtil.text(ctx, tr, m.name, x + 10, cy + 4, Theme.alpha(tc, a), true);
                if (!m.settings.isEmpty()) {
                    RenderUtil.text(ctx, tr, m.expanded ? "v" : ">", x + PW - 15, cy + 4,
                            Theme.alpha(Theme.TEXT_DIM, a), true);
                }
                cy += MH;
                int sh = r.settingsH();
                if (sh > 0) {
                    ctx.enableScissor(x + 1, cy, x + PW - 1, cy + sh);
                    int sy = cy;
                    for (Setting s : m.settings) {
                        renderSetting(ctx, tr, r, s, sy, a);
                        sy += SH;
                    }
                    ctx.disableScissor();
                    cy += sh;
                }
                idx++;
            }
            ctx.disableScissor();
        }

        void renderSetting(DrawContext ctx, TextRenderer tr, Row r, Setting s, int cy, float a) {
            ctx.fill(x + 1, cy, x + PW - 1, cy + SH, Theme.alpha(0x40000000, a));
            if (s instanceof BooleanSetting b) {
                float v = r.anims.get(s).to(b.value ? 1f : 0f, 16f);
                RenderUtil.text(ctx, tr, b.name, x + 10, cy + 5, Theme.alpha(Theme.TEXT_DIM, a), false);
                int tx = x + PW - 28, ty = cy + 4;
                RenderUtil.roundRect(ctx, tx, ty, 17, 9, 4,
                        Theme.alpha(Theme.lerp(Theme.TRACK, Theme.accent(3), v), a));
                RenderUtil.roundRect(ctx, tx + 1 + Math.round(v * 8), ty + 1, 7, 7, 3,
                        Theme.alpha(0xFFFFFFFF, a));
            } else if (s instanceof NumberSetting n) {
                float target = (float) ((n.value - n.min) / (n.max - n.min));
                float v = r.anims.get(s).to(target, 20f);
                RenderUtil.text(ctx, tr, n.name, x + 10, cy + 1, Theme.alpha(Theme.TEXT_DIM, a), false);
                String val = fmt(n);
                RenderUtil.text(ctx, tr, val, x + PW - 10 - tr.getWidth(val), cy + 1,
                        Theme.alpha(Theme.TEXT, a), false);
                int bx = x + 10, by = cy + SH - 6, bw = PW - 20;
                RenderUtil.roundRect(ctx, bx, by, bw, 3, 1, Theme.alpha(Theme.TRACK, a));
                int fw = Math.max(2, Math.round(bw * v));
                RenderUtil.roundRect(ctx, bx, by, fw, 3, 1, Theme.alpha(Theme.accent(2), a));
                RenderUtil.roundRect(ctx, bx + fw - 3, by - 2, 6, 7, 3, Theme.alpha(0xFFFFFFFF, a));
            }
        }

        boolean click(double mx, double my, int btn) {
            if (in(mx, my, x, y, PW, HH)) {
                if (btn == 0) { dragging = true; dragDX = (int) mx - x; dragDY = (int) my - y; }
                else if (btn == 1) open = !open;
                return true;
            }
            int total = totalH();
            if (!in(mx, my, x, y, PW, total)) return false;
            if (pOpen.v < 0.5f) return true;

            int cy = y + HH + 2;
            for (Row r : rows) {
                Module m = r.m;
                if (in(mx, my, x + 4, cy, PW - 8, MH - 2)) {
                    if (btn == 0) m.toggle();
                    else if (btn == 1 && !m.settings.isEmpty()) m.expanded = !m.expanded;
                    return true;
                }
                cy += MH;
                int sh = r.settingsH();
                if (sh > 0) {
                    int sy = cy;
                    for (Setting s : m.settings) {
                        if (in(mx, my, x, sy, PW, SH) && my < cy + sh) {
                            if (s instanceof BooleanSetting b && btn == 0) b.value = !b.value;
                            else if (s instanceof NumberSetting n && btn == 0) {
                                slider = n;
                                updateSlider(mx);
                            }
                            return true;
                        }
                        sy += SH;
                    }
                    cy += sh;
                }
            }
            return true;
        }

        void drag(double mx, double my) {
            if (dragging) { x = (int) mx - dragDX; y = (int) my - dragDY; }
            if (slider != null) updateSlider(mx);
        }

        void release() { dragging = false; slider = null; }

        void updateSlider(double mx) {
            double pct = Math.max(0, Math.min(1, (mx - (x + 10)) / (PW - 20)));
            slider.set(slider.min + pct * (slider.max - slider.min));
        }
    }
}
