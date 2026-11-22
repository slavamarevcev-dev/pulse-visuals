package com.pulse.gui;

import com.pulse.Theme;
import com.pulse.config.ConfigManager;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.module.ModuleManager;
import com.pulse.module.setting.BooleanSetting;
import com.pulse.module.setting.NumberSetting;
import com.pulse.module.setting.Setting;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class ClickGuiScreen extends Screen {
    private static final int PW = 112, HH = 16, MH = 14, SH = 12;
    private final List<Panel> panels = new ArrayList<>();

    public ClickGuiScreen() {
        super(Text.literal("Pulse"));
        int x = 24;
        for (Category c : Category.values()) {
            panels.add(new Panel(c, x, 24));
            x += PW + 10;
        }
    }

    @Override public boolean shouldPause() { return false; }

    @Override
    public void removed() { ConfigManager.save(); }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        ctx.fill(0, 0, width, height, 0x80000000);
        for (Panel p : panels) p.render(ctx, textRenderer, mx, my);
        ctx.drawText(textRenderer, "Pulse Visuals  |  LMB: toggle  RMB: settings",
                6, height - 12, Theme.TEXT_DIM, true);
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

    private static class Panel {
        final Category cat;
        final List<Module> mods = new ArrayList<>();
        int x, y, dragDX, dragDY;
        boolean dragging, open = true;
        NumberSetting slider;

        Panel(Category cat, int x, int y) {
            this.cat = cat; this.x = x; this.y = y;
            for (Module m : ModuleManager.all()) if (m.category == cat) mods.add(m);
        }

        void render(DrawContext ctx, TextRenderer tr, int mx, int my) {
            int totalH = HH;
            if (open) for (Module m : mods) {
                totalH += MH;
                if (m.expanded) totalH += m.settings.size() * SH;
            }
            ctx.fill(x, y, x + PW, y + totalH, Theme.PANEL);
            ctx.fill(x, y, x + PW, y + HH, Theme.BG);
            ctx.fill(x, y + HH - 1, x + PW, y + HH, Theme.accent(0));
            ctx.drawText(tr, cat.label, x + 5, y + 4, Theme.accent(1), true);
            ctx.drawText(tr, open ? "-" : "+", x + PW - 10, y + 4, Theme.TEXT, true);
            if (!open) return;

            int cy = y + HH;
            int idx = 0;
            for (Module m : mods) {
                boolean hover = in(mx, my, x, cy, PW, MH);
                int bg = m.isEnabled() ? (Theme.accent(idx) & 0x00FFFFFF) | 0x80000000
                        : (hover ? Theme.ROW_HOVER : Theme.ROW);
                ctx.fill(x + 1, cy, x + PW - 1, cy + MH, bg);
                ctx.drawText(tr, m.name, x + 5, cy + 3, m.isEnabled() ? Theme.TEXT : Theme.TEXT_DIM, true);
                if (!m.settings.isEmpty())
                    ctx.drawText(tr, m.expanded ? "v" : ">", x + PW - 10, cy + 3, Theme.TEXT_DIM, true);
                cy += MH;
                idx++;
                if (m.expanded) {
                    for (Setting s : m.settings) {
                        renderSetting(ctx, tr, s, cy);
                        cy += SH;
                    }
                }
            }
        }

        void renderSetting(DrawContext ctx, TextRenderer tr, Setting s, int cy) {
            ctx.fill(x + 1, cy, x + PW - 1, cy + SH, 0xC00E0E16);
            if (s instanceof BooleanSetting b) {
                ctx.drawText(tr, b.name, x + 9, cy + 2, Theme.TEXT_DIM, false);
                int bx = x + PW - 16;
                ctx.fill(bx, cy + 2, bx + 8, cy + 10, 0xFF303048);
                if (b.value) ctx.fill(bx + 1, cy + 3, bx + 7, cy + 9, Theme.accent(3));
            } else if (s instanceof NumberSetting n) {
                double pct = (n.value - n.min) / (n.max - n.min);
                int fill = (int) ((PW - 2) * pct);
                ctx.fill(x + 1, cy, x + 1 + fill, cy + SH, (Theme.accent(2) & 0x00FFFFFF) | 0x70000000);
                ctx.drawText(tr, n.name, x + 9, cy + 2, Theme.TEXT, false);
                String v = (n.step >= 1) ? String.valueOf((int) n.value) : String.format("%.1f", n.value);
                ctx.drawText(tr, v, x + PW - 6 - tr.getWidth(v), cy + 2, Theme.TEXT, false);
            }
        }

        boolean click(double mx, double my, int btn) {
            if (in(mx, my, x, y, PW, HH)) {
                if (btn == 0) { dragging = true; dragDX = (int) mx - x; dragDY = (int) my - y; }
                else if (btn == 1) open = !open;
                return true;
            }
            if (!open) return false;
            int cy = y + HH;
            for (Module m : mods) {
                if (in(mx, my, x, cy, PW, MH)) {
                    if (btn == 0) m.toggle();
                    else if (btn == 1 && !m.settings.isEmpty()) m.expanded = !m.expanded;
                    return true;
                }
                cy += MH;
                if (m.expanded) {
                    for (Setting s : m.settings) {
                        if (in(mx, my, x, cy, PW, SH)) {
                            if (s instanceof BooleanSetting b && btn == 0) b.value = !b.value;
                            else if (s instanceof NumberSetting n && btn == 0) {
                                slider = n;
                                updateSlider(mx);
                            }
                            return true;
                        }
                        cy += SH;
                    }
                }
            }
            return false;
        }

        void drag(double mx, double my) {
            if (dragging) { x = (int) mx - dragDX; y = (int) my - dragDY; }
            if (slider != null) updateSlider(mx);
        }

        void release() { dragging = false; slider = null; }

        void updateSlider(double mx) {
            double pct = Math.max(0, Math.min(1, (mx - (x + 1)) / (PW - 2)));
            slider.set(slider.min + pct * (slider.max - slider.min));
        }
    }
}
