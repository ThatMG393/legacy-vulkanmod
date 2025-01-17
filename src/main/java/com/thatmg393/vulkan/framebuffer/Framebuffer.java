package com.thatmg393.vulkan.framebuffer;

import org.joml.Vector4f;

public class Framebuffer {
    public Framebuffer(Builder builder) {

    }

    @lombok.Builder
    public static class Builder {
        private int width, height;

        private boolean hasDepthAttachment;
        private int depthAttachment;
        private int colorAttachment;

        @lombok.Builder.Default
        private Vector4f clearColor = new Vector4f(1.0f, 1.0f, 1.0f, 0.0f);
    }
}
