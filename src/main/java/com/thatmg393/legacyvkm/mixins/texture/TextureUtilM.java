package com.thatmg393.legacyvkm.mixins.texture;

import org.spongepowered.asm.mixin.*;

import com.thatmg393.legacyvkm.LegacyVulkanMod;

import net.minecraft.client.texture.TextureUtil;

@Mixin(TextureUtil.class)
public class TextureUtilM {
    @Shadow static void bindTexture(int texture) { }

    @Overwrite
    public static void prepareImage(int id, int maxLevel, int width, int height) {
        TextureUtil.deleteTexture(id);
        bindTexture(id);
        if (maxLevel >= 0) {
            LegacyVulkanMod.LOGGER.info("Say no to GL11#glTexParameteri()!!!");
            
            /* GL11.glTexParameteri(3553, 33085, maxLevel);
            GL11.glTexParameterf(3553, 33082, 0.0F);
            GL11.glTexParameterf(3553, 33083, (float)maxLevel);
            GL11.glTexParameterf(3553, 34049, 0.0F); */
        }

        for (int i = 0; i <= maxLevel; ++i) {
            // GL11.glTexImage2D(3553, i, 6408, width >> i, height >> i, 0, 32993, 33639, (IntBuffer)null);
        }
    }
}
