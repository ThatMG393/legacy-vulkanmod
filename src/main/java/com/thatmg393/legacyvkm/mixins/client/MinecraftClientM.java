package com.thatmg393.legacyvkm.mixins.client;

import java.io.IOException;

import org.lwjgl.LWJGLException;
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

import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientM {
    @Inject(method = "initializeGame", at = @At("HEAD"))
    private void onInitializeGame(CallbackInfo ci) throws LWJGLException, IOException {
        // TODO: MOVE TO LEGACY LWJGL3 (or not cus platform specific issue)
        ClassPool pool = new ClassPool();
        pool.appendClassPath(new LoaderClassPath(FabricLauncherBase.getLauncher().getTargetClassLoader()));

        try {
            CtClass cls = pool.get("org.lwjgl.glfw.GLFW");
            if (cls.getDeclaredMethod("glfwPlatformSupported", new CtClass[] { CtClass.intType }) == null) {
                CtMethod mth = new CtMethod(CtClass.booleanType, "glfwPlatformSupported", new CtClass[] { CtClass.intType }, cls);
                mth.setBody("return true;");

                cls.addMethod(mth);
            }
        } catch (Exception e) { }
    }

    @Inject(method = "setPixelFormat", at = @At("RETURN"))
    private void onSetPixelFormat(CallbackInfo ci) throws LWJGLException {
        LegacyVulkanMod.LOGGER.info("Window was probably created.");
        LegacyVulkanMod.LOGGER.info("Initializing Vulkan now...");

        Vulkan.getInstance().initialize();
    }
}