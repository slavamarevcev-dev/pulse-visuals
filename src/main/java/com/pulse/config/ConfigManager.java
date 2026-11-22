package com.pulse.config;

import com.google.gson.*;
import com.pulse.module.Module;
import com.pulse.module.ModuleManager;
import com.pulse.module.setting.BooleanSetting;
import com.pulse.module.setting.NumberSetting;
import com.pulse.module.setting.Setting;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path file() { return FabricLoader.getInstance().getConfigDir().resolve("pulsevisuals.json"); }

    public static void save() {
        JsonObject root = new JsonObject();
        for (Module m : ModuleManager.all()) {
            JsonObject o = new JsonObject();
            o.addProperty("enabled", m.isEnabled());
            for (Setting s : m.settings) {
                if (s instanceof BooleanSetting b) o.addProperty(s.name, b.value);
                else if (s instanceof NumberSetting n) o.addProperty(s.name, n.value);
            }
            root.add(m.name, o);
        }
        try (Writer w = Files.newBufferedWriter(file())) {
            GSON.toJson(root, w);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        try (Reader r = Files.newBufferedReader(f)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            for (Module m : ModuleManager.all()) {
                if (!root.has(m.name)) continue;
                JsonObject o = root.getAsJsonObject(m.name);
                if (o.has("enabled")) m.setEnabled(o.get("enabled").getAsBoolean());
                for (Setting s : m.settings) {
                    if (!o.has(s.name)) continue;
                    if (s instanceof BooleanSetting b) b.value = o.get(s.name).getAsBoolean();
                    else if (s instanceof NumberSetting n) n.set(o.get(s.name).getAsDouble());
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
