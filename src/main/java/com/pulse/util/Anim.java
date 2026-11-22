package com.pulse.util;

/** Плавная анимация значения к цели (не зависит от FPS). */
public final class Anim {
    public float v;
    private long last = System.nanoTime();

    public Anim(float init) { this.v = init; }

    public float to(float target, float speed) {
        long n = System.nanoTime();
        float dt = Math.min(0.1f, (n - last) / 1_000_000_000f);
        last = n;
        v += (target - v) * Math.min(1f, dt * speed);
        if (Math.abs(target - v) < 0.002f) v = target;
        return v;
    }
}
