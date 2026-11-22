package com.pulse.module.impl;

import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.module.setting.BooleanSetting;
import com.pulse.module.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;

public class Esp extends Module {
    public final BooleanSetting players = add(new BooleanSetting("Players", true));
    public final BooleanSetting hostiles = add(new BooleanSetting("Hostiles", true));
    public final BooleanSetting passives = add(new BooleanSetting("Passives", false));
    public final BooleanSetting items = add(new BooleanSetting("Items", false));
    public final NumberSetting range = add(new NumberSetting("Range", 64, 8, 256, 4));

    public Esp() { super("ESP", "Glow outline for entities", Category.RENDER); }

    public boolean shouldGlow(Entity e) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || e == mc.player) return false;
        if (mc.player.distanceTo(e) > range.value) return false;
        return (players.value && e instanceof PlayerEntity)
                || (hostiles.value && e instanceof HostileEntity)
                || (passives.value && e instanceof PassiveEntity)
                || (items.value && e instanceof ItemEntity);
    }

    public int colorFor(Entity e) {
        if (e instanceof PlayerEntity) return 0x00E5FF;
        if (e instanceof HostileEntity) return 0xFF3B3B;
        if (e instanceof PassiveEntity) return 0x4ADE80;
        if (e instanceof ItemEntity) return 0xFACC15;
        return 0xFFFFFF;
    }
}
