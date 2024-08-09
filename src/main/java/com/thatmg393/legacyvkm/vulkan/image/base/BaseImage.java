package com.thatmg393.legacyvkm.vulkan.image.base;

import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import com.thatmg393.legacyvkm.vulkan.vma.VMAManager;

import lombok.*;

@Getter
public class BaseImage {
    private final Builder builder;

    private long id, allocation, sampler;

    public BaseImage(Builder builder) {
        this.builder = builder;
        this.sampler = 0; // mipLevels and samplerFlags
    }

    protected void initImage() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer id = stack.mallocLong(1);
            PointerBuffer allocation = stack.pointers(0L);

            VMAManager.getInstance().createImage(
                builder.width, builder.height,
                builder.mipLevels, builder.format,
                builder.tiling, builder.usage,
                VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
                stack.ints(0), // Graphics Queue
                id, allocation
            );

            this.id = id.get(0);
            this.allocation = allocation.get(0);

            VMAManager.getInstance().addImage(this);
        }
    }

    @lombok.Builder(builderClassName = "InternalBuilder", builderMethodName = "internalBuilder", access = AccessLevel.PRIVATE, toBuilder = true, setterPrefix = "set", buildMethodName = "buildVulkanImage")
    public static class Builder {
        private final int width, height;

        private int format = VK_FORMAT_R8G8B8A8_UNORM;
        private int tiling = VK_IMAGE_TILING_OPTIMAL;
        private int usage = VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_SAMPLED_BIT;
        private byte mipLevels = 1;
        private byte samplerFlags = 0;

        public static final Builder.InternalBuilder builder(int width, int height) {
            return internalBuilder().setWidth(width).setHeight(height);
        }
    }
}
