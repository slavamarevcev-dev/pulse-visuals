package com.pulse.module.impl;

import com.pulse.Theme;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.module.setting.BooleanSetting;
import com.pulse.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InfoHud extends Module {
    public final BooleanSetting coords = add(new BooleanSetting("Coords", true));
    public final BooleanSetting facing = add(new BooleanSetting("Facing", true));
    public final BooleanSetting speed = add(new BooleanSetting("Speed", true));
    public final BooleanSetting time = add(new BooleanSetting("Time", false));

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private double lastX, lastZ, shown;
    private boolean init;

    public InfoHud() { super("Info", "Coordinates, speed, direction", Category.HUD); setEnabled(true); }

    @Override
    public void onTick(MinecraftClient mc) {
        double x = mc.player.getX(), z = mc.player.getZ();
        if (init) {
            double raw = Math.sqrt((x - lastX) * (x - lastX) + (z - lastZ) * (z - lastZ)) * 20;
            shown += (raw - shown) * 0.3;
        }
        lastX = x; lastZ = z; init = true;
    }

    @Override
    public void onHudRender(DrawContext ctx, MinecraftClient mc) {
        TextRenderer tr = mc.textRenderer;
        List<String[]> lines = new ArrayList<>();
        if (coords.value) lines.add(new String[]{"XYZ", String.format("%.1f  %.1f  %.1f",
                mc.player.getX(), mc.player.getY(), mc.player.getZ())});
        if (facing.value) lines.add(new String[]{"Facing", mc.player.getHorizontalFacing().asString()});
        if (speed.value) lines.add(new String[]{"Speed", String.format("%.1f b/s", shown)});
        if (time.value) lines.add(new String[]{"Time", LocalTime.now().format(FMT)});
        if (lines.isEmpty()) return;

        int labelW = 0, valueW = 0;
        for (String[] l : lines) {
            labelW = Math.max(labelW, tr.getWidth(l[0]));
            valueW = Math.max(valueW, tr.getWidth(l[1]));
        }
        int w = labelW + valueW + 24;
        int h = lines.size() * 11 + 7;
        int x = 6, y = ctx.getScaledWindowHeight() - h - 6;

        RenderUtil.glow(ctx, x, y, w, h, 4, Theme.accent(4));
        RenderUtil.roundRect(ctx, x, y, w, h, 4, Theme.BG);
        RenderUtil.roundRect(ctx, x + 2, y + 3, 2, h - 6, 1, Theme.accent(5));
        int ly = y + 5;
        for (String[] l : lines) {
            RenderUtil.text(ctx, tr, l[0], x + 9, ly, Theme.TEXT_DIM, true);
            RenderUtil.text(ctx, tr, l[1], x + 9 + labelW + 8, ly, Theme.TEXT, true);
            ly += 11;
        }
    }
}
