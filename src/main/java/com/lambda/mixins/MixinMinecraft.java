package com.lambda.mixins;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void clinit(CallbackInfo ci) {
        System.out.println("THIS FUCKING WORKS\n\n\n\n\n\n\n\n\n\n##########################################");
    }

    @Inject(method = "run()V", at = @At("HEAD"))
    public void run(CallbackInfo ci) {
        System.out.println("This also works");
    }
}