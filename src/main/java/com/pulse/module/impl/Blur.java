package com.pulse.module.impl;

import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.module.setting.NumberSetting;

public class Blur extends Module {
    public final NumberSetting strength = add(new NumberSetting("Strength", 5, 1, 10, 1));

    public Blur() {
        super("Blur", "Blur game behind the menu", Category.CLIENT);
        setEnabled(true);
    }
}
