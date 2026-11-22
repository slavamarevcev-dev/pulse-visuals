package com.pulse.module;

import com.pulse.Theme;
import com.pulse.module.impl.*;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();

    public static void init() {
        MODULES.add(new Esp());
        MODULES.add(new Fullbright());
        MODULES.add(new Zoom());
        MODULES.add(new Watermark());
        MODULES.add(new InfoHud());
        MODULES.add(new ArrayListHud());
        MODULES.add(new KeystrokesHud());
        MODULES.add(new ArmorHud());
        MODULES.add(new TargetHud());
        MODULES.add(new PotionHud());
        MODULES.add(new Blur());
        MODULES.add(new Colors());
        Theme.colors = get(Colors.class);
    }

    public static List<Module> all() { return MODULES; }

    @SuppressWarnings("unchecked")
    public static <T extends Module> T get(Class<T> cls) {
        for (Module m : MODULES) if (m.getClass() == cls) return (T) m;
        return null;
    }
}
