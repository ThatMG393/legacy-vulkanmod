package com.thatmg393.legacyvkm.mixins.gl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mojang.blaze3d.platform.GLX;
import com.thatmg393.legacyvkm.LegacyVulkanMod;

@Mixin(GLX.class)
public class GLXM {
    @Overwrite
    public static void createContext() {
        LegacyVulkanMod.LOGGER.info("GL Capabilities all max!!");
    }

    @Overwrite
    public static boolean supportsFbo() {
        return true;
    }
}
