package com.pulse.mixin;

import com.pulse.module.ModuleManager;
import com.pulse.module.impl.Esp;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void pulse$hasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Esp esp = ModuleManager.get(Esp.class);
        if (esp != null && esp.isEnabled() && esp.shouldGlow(entity)) cir.setReturnValue(true);
    }
}
