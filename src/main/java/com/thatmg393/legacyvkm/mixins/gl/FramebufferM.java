package com.thatmg393.legacyvkm.mixins.gl;

import org.spongepowered.asm.mixin.*;

import net.minecraft.client.gl.Framebuffer;

@Mixin(Framebuffer.class)
public class FramebufferM {
    @Overwrite
    public void bind(boolean viewport) { }

    @Overwrite
    public void unbind() { }

    @Overwrite
    public void drawInternal(int width, int height, boolean color) { }

    @Overwrite
    public void clear() { }
}
