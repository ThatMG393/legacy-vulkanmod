package com.thatmg393.legacyvkm.mixins.blaze3d.platform;

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
}
