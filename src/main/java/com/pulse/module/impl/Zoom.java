package com.pulse.module.impl;

import com.pulse.module.Category;
import com.pulse.module.Module;
import com.pulse.module.setting.NumberSetting;
import com.pulse.util.Anim;

public class Zoom extends Module {
    public final NumberSetting factor = add(new NumberSetting("Factor", 3.0, 1.5, 10.0, 0.5));
    private final Anim anim = new Anim(1f);

    public Zoom() { super("Zoom", "Smooth narrow field of view", Category.RENDER); }

    /** Текущий плавный множитель (1.0 = без зума). */
    public float current() {
        return anim.to(isEnabled() ? factor.f() : 1f, 12f);
    }
}
