package com.pulse.module;

import com.pulse.module.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    public final String name;
    public final String description;
    public final Category category;
    public final List<Setting> settings = new ArrayList<>();
    public boolean expanded;
    private boolean enabled;

    protected Module(String name, String description, Category category) {
        this.name = name; this.description = description; this.category = category;
    }

    protected <T extends Setting> T add(T setting) { settings.add(setting); return setting; }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean value) {
        if (this.enabled == value) return;
        this.enabled = value;
        if (value) onEnable(); else onDisable();
    }

    public void toggle() { setEnabled(!enabled); }

    public void onEnable() {}
    public void onDisable() {}
    public void onTick(MinecraftClient mc) {}
    public void onHudRender(DrawContext ctx, MinecraftClient mc) {}
}
