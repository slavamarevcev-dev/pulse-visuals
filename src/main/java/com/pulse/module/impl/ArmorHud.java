package com.pulse.module.impl;

import com.pulse.Theme;
import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ArmorHud extends Module {
    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public ArmorHud() { super("ArmorHud", "Worn armor with durability", Category.HUD); }

    @Override
    public void onHudRender(DrawContext ctx, MinecraftClient mc) {
        int x = 6, y = 28;
        RenderUtil.roundRect(ctx, x, y, 4 * 18 + 4, 22, 4, Theme.BG);
        int ix = x + 4;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack s = mc.player.getEquippedStack(slot);
            if (!s.isEmpty()) {
                ctx.drawItem(s, ix, y + 3);
                ctx.drawStackOverlay(mc.textRenderer, s, ix, y + 3);
            }
            ix += 18;
        }
    }
}
