package com.pulse.mixin;

import com.pulse.module.ModuleManager;
import com.pulse.module.impl.Zoom;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void pulse$zoom(Camera camera, float tickProgress, boolean changingFov,
                            CallbackInfoReturnable<Float> cir) {
        Zoom zoom = ModuleManager.get(Zoom.class);
        if (zoom == null) return;
        float f = zoom.current();
        if (Math.abs(f - 1f) > 0.001f) {
            cir.setReturnValue(cir.getReturnValue() / f);
        }
    }
}
