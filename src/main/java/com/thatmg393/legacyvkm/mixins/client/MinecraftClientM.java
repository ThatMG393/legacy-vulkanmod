package com.thatmg393.legacyvkm.mixins.client;

import org.lwjgl.LWJGLException;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.thatmg393.legacyvkm.LegacyVulkanMod;
import com.thatmg393.legacyvkm.vulkan.Vulkan;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.util.proxy.DefineClassHelper;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientM {
    @Inject(method = "setPixelFormat", at = @At("RETURN"))
    private void setPixelFormat_RETURN(CallbackInfo ci) throws LWJGLException {
        LegacyVulkanMod.LOGGER.info("Window was probably created.");
        LegacyVulkanMod.LOGGER.info("Initializing Vulkan now...");

        Vulkan.getInstance().initialize();
    }
}