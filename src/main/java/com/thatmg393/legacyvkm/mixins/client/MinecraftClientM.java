package com.thatmg393.legacyvkm.mixins.client;

import org.lwjgl.LWJGLException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.thatmg393.legacyvkm.LegacyVulkanMod;
import com.thatmg393.legacyvkm.vulkan.Vulkan;

import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientM {
    @Inject(method = "setPixelFormat", at = @At("RETURN"), remap = false)
    private void onSetPixelFormat(CallbackInfo ci) throws LWJGLException {
        LegacyVulkanMod.LOGGER.info("Window was probably created.");
        LegacyVulkanMod.LOGGER.info("Initializing Vulkan now");

        Vulkan.getInstance().initialize();
    }
}