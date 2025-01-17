package com.thatmg393.legacyvkm.mixins.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mojang.blaze3d.platform.GlStateManager;

@Mixin(GlStateManager.class)
public class GlStateManagerM {
    @Overwrite
    public static void matrixMode(int mode) { }

    @Overwrite
    public static void loadIdentity() { }

    @Overwrite
    public static void ortho(
        double left, double right, 
        double back, double top,
        double near, double far
    ) { }

    @Overwrite
    public static void translate(float x, float y, float z) { }

    @Overwrite
    public static void translate(double x, double y, double z) { }

    @Overwrite
    public static void enableTexture() { }

    @Overwrite
    public static int getTexLevelParameter() { return 0; }

    @Overwrite
    public static void deleteTexture(int texture) { }
}