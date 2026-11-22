package com.pulse.mixin;

import com.pulse.module.ModuleManager;
import com.pulse.module.impl.Esp;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void pulse$teamColor(CallbackInfoReturnable<Integer> cir) {
        Esp esp = ModuleManager.get(Esp.class);
        Entity self = (Entity) (Object) this;
        if (esp != null && esp.isEnabled() && esp.shouldGlow(self)) {
            cir.setReturnValue(esp.colorFor(self));
        }
    }
}
