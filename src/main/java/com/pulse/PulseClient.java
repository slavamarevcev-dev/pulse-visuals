package com.pulse;

import com.pulse.config.ConfigManager;
import com.pulse.gui.ClickGuiScreen;
import com.pulse.module.Module;
import com.pulse.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class PulseClient implements ClientModInitializer {
    public static final String MOD_ID = "pulsevisuals";
    private static KeyBinding guiKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        ConfigManager.load();

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));
        guiKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.pulsevisuals.gui", GLFW.GLFW_KEY_RIGHT_SHIFT, category));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (guiKey.wasPressed()) {
                client.setScreen(new ClickGuiScreen());
            }
            if (client.player == null) return;
            for (Module m : ModuleManager.all()) {
                if (m.isEnabled()) m.onTick(client);
            }
        });

        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                Identifier.of(MOD_ID, "hud"),
                (ctx, tickCounter) -> {
                    MinecraftClient mc = MinecraftClient.getInstance();
                    if (mc.player == null || mc.options.hudHidden) return;
                    for (Module m : ModuleManager.all()) {
                        if (m.isEnabled()) m.onHudRender(ctx, mc);
                    }
                });

        Runtime.getRuntime().addShutdownHook(new Thread(ConfigManager::save));
    }
}
