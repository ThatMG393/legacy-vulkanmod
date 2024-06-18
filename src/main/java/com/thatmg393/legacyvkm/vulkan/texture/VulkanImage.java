package com.thatmg393.legacyvkm.vulkan.texture;

public class VulkanImage {
    private long id, allocation, mainImageView, sampler;
    private long[] levelImageViews;

    // Some of them should be final but who cares??
    private int width, height, aspect, format, formatSize, mipLevels, usage, currentLayout;

    public VulkanImage(Builder builder) {

    }

    public class Builder {

    }
}
