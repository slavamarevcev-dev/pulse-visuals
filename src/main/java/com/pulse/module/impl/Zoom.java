package com.pulse.module.impl;

import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.module.setting.NumberSetting;

public class Zoom extends Module {
    public final NumberSetting factor = add(new NumberSetting("Factor", 3.0, 1.5, 10.0, 0.5));

    public Zoom() { super("Zoom", "Narrow field of view", Category.RENDER); }
}
