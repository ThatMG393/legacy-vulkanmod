package com.thatmg393.legacyvkm.mixins.client;

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
import javassist.util.proxy.DefineClassHelper;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import net.minecraft.client.MinecraftClient;

@Mixin(MinecraftClient.class)
public class MinecraftClientM {
    @Inject(method = "setPixelFormat", at = @At("INVOKE"))
    private void setPixelFormat_HEAD(CallbackInfo ci) throws LWJGLException {
        LegacyVulkanMod.LOGGER.info("Patchy time? No? Alright :(");

        // TODO: MOVE TO LEGACY LWJGL3 (or not cus platform specific issue)
        ClassPool pool = new ClassPool();
        pool.appendClassPath(new LoaderClassPath(FabricLauncherBase.getLauncher().getTargetClassLoader()));

        try {
            CtClass cls = pool.get("org.lwjgl.glfw.GLFW");
            
            try {
                cls.getDeclaredMethod("glfwPlatformSupported", new CtClass[] { CtClass.intType });
                LegacyVulkanMod.LOGGER.info("seems like no patch needed.");
            } catch (Exception e1) {
                LegacyVulkanMod.LOGGER.info("punjabiloonchir detected!1!1!");

                CtMethod mth = new CtMethod(CtClass.booleanType, "glfwPlatformSupported", new CtClass[] { CtClass.intType }, cls);
                mth.setBody("return true;");

                cls.addMethod(mth);
                DefineClassHelper.toClass(cls.getName(), null, pool.getClassLoader(), null, cls.toBytecode());
                cls.detach();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    @Inject(method = "setPixelFormat", at = @At("RETURN"))
    private void setPixelFormat_RETURN(CallbackInfo ci) throws LWJGLException {
        LegacyVulkanMod.LOGGER.info("Window was probably created.");
        LegacyVulkanMod.LOGGER.info("Initializing Vulkan now...");

        Vulkan.getInstance().initialize();
    }
}