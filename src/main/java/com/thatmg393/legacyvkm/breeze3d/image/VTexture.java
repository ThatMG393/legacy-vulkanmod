package com.thatmg393.legacyvkm.breeze3d.image;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import com.thatmg393.legacyvkm.vulkan.image.base.BaseImage;

public class VTexture extends BaseImage {
    private long mainImageView;

    public VTexture(Builder builder) {
        super(builder);
    }

    @Override
    protected void initImage() {
        super.initImage();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkImageViewCreateInfo vivci = VkImageViewCreateInfo.calloc(stack);
            vivci.sType$Default();

            LongBuffer imageView = stack.mallocLong(1);
            mainImageView = imageView.get(0);
        }
    }
}
