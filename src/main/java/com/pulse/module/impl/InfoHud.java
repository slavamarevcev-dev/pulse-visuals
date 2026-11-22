package com.pulse.module.impl;

import com.pulse.Theme;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.module.setting.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class InfoHud extends Module {
    public final BooleanSetting coords = add(new BooleanSetting("Coords", true));
    public final BooleanSetting facing = add(new BooleanSetting("Facing", true));

    public InfoHud() { super("Info", "Coordinates and direction", Category.HUD); setEnabled(true); }

    @Override
    public void onHudRender(DrawContext ctx, MinecraftClient mc) {
        int y = ctx.getScaledWindowHeight() - 14;
        if (facing.value) {
            line(ctx, mc, "Facing: " + mc.player.getHorizontalFacing().asString(), y);
            y -= 12;
        }
        if (coords.value) {
            line(ctx, mc, String.format("XYZ: %.1f %.1f %.1f",
                    mc.player.getX(), mc.player.getY(), mc.player.getZ()), y);
        }
    }

    private void line(DrawContext ctx, MinecraftClient mc, String text, int y) {
        int w = mc.textRenderer.getWidth(text);
        ctx.fill(4, y - 2, 4 + w + 6, y + 10, 0x90000000);
        ctx.fill(4, y - 2, 6, y + 10, Theme.accent(y / 12));
        ctx.drawText(mc.textRenderer, text, 9, y, Theme.TEXT, true);
    }
}
