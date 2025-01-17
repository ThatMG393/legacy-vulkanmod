package com.thatmg393.legacyvkm.mixins.render.world;

import org.spongepowered.asm.mixin.*;

import net.minecraft.client.render.WorldRenderer;

@Mixin(WorldRenderer.class)
@SuppressWarnings("")
public class WorldRendererM {
    @Overwrite
    public void setupEntityOutlineShader() {

    }
}
