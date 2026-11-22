package com.pulse.module.impl;

import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.module.setting.BooleanSetting;
import com.pulse.module.setting.NumberSetting;

public class Colors extends Module {
    public final NumberSetting preset = add(new NumberSetting("Preset", 1, 1, 4, 1));
    public final BooleanSetting rainbow = add(new BooleanSetting("Rainbow", false));
    public final NumberSetting speed = add(new NumberSetting("Speed", 1.0, 0.2, 5.0, 0.2));

    public Colors() {
        super("Colors", "Theme colors", Category.CLIENT);
        setEnabled(true);
    }
}
