package com.thatmg393.legacyvkm.mixins.client;

import org.lwjgl.LWJGLException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.thatmg393.legacyvkm.LegacyVulkanMod;
import com.thatmg393.legacyvkm.config.Platform;
import com.thatmg393.vkapi.Vulkan;

import net.minecraft.client.MinecraftClient;

import static com.thatmg393.legacyvkm.LegacyVulkanMod.LOGGER;

@Mixin(MinecraftClient.class)
public class MinecraftClientM {
    @Inject(method = "setPixelFormat", at = @At("RETURN"))
    private void setPixelFormat_RETURN(CallbackInfo ci) throws LWJGLException {
        LOGGER.info("== Legacy VulkanMod ==");
        LOGGER.info("== Version: " + LegacyVulkanMod.VERSION + " ==");

        Platform.initialize();
        Vulkan.getInstance().initialize();
    }
}