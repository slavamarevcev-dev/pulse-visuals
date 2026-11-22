package com.pulse.module.setting;

public class NumberSetting extends Setting {
    public double value;
    public final double min, max, step;

    public NumberSetting(String name, double def, double min, double max, double step) {
        super(name);
        this.value = def; this.min = min; this.max = max; this.step = step;
    }

    public void set(double v) {
        v = Math.max(min, Math.min(max, v));
        this.value = Math.round(v / step) * step;
    }

    public float f() { return (float) value; }
}
