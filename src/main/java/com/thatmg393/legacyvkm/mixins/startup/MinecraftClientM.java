package com.thatmg393.legacyvkm.mixins.startup;

import org.lwjgl.LWJGLException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.thatmg393.legacyvkm.LegacyVulkanMod;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientM {
    @Inject(method = "setPixelFormat", at = @At("RETURN"))
    private void setPixelFormat_RETURN(CallbackInfo ci) throws LWJGLException {
        LegacyVulkanMod.initializeVulkan();
    }
}