package com.quickload.mixin;

import net.minecraft.client.renderer.fog.environment.BlindnessFogEnvironment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Solo visual: el efecto de ceguera ya no cierra la niebla negra alrededor del
 * jugador. El servidor sigue aplicando el efecto igual que siempre (por ejemplo,
 * con ceguera sigues sin poder correr).
 *
 * "require = 0": si en alguna version esta clase o metodo cambia de nombre, este
 * ajuste simplemente no se aplica, pero el resto del mod sigue funcionando.
 */
@Mixin(BlindnessFogEnvironment.class)
public abstract class BlindnessFogEnvironmentMixin {

    @Inject(method = "isApplicable", at = @At("HEAD"), cancellable = true, require = 0)
    private void quickload$noBlindnessFog(FogType fogType, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
