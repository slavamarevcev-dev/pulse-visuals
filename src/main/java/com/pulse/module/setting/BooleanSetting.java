package com.pulse.module.setting;

public class BooleanSetting extends Setting {
    public boolean value;
    public BooleanSetting(String name, boolean def) { super(name); this.value = def; }
}
