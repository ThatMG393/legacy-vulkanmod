package com.thatmg393.legacyvkm.mixins.lwjgl;

import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Display.class)
public class DisplayM {
    @Inject(method = "create(Lorg/lwjgl/opengl/PixelFormat;)V", at = @At("RETURN"), remap = false)
    public void onCreateWindow(CallbackInfo ci) {
        System.out.println("Window created, getting window handle...");
        System.out.println("Window Handle -> " + Display.getHandle());
    }
}
