package com.thatmg393.legacyvkm.mixins.lwjgl;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.thatmg393.legacyvkm.LegacyVulkanMod;

@Mixin(Display.class)
public class DisplayM {
    @Inject(method = "create(Lorg/lwjgl/opengl/PixelFormat;)V", at = @At("RETURN"), remap = false)
    public static void onCreateWindow(PixelFormat useless1, CallbackInfo ci) {
        LegacyVulkanMod.LOGGER.info("Window created, getting window handle...");
        LegacyVulkanMod.LOGGER.info("Window Handle -> " + Display.getHandle());
    }
}
