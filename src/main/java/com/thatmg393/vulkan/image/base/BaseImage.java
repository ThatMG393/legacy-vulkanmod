package com.thatmg393.vulkan.image.base;

import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import com.thatmg393.vulkan.image.utils.ImageUtils;
import com.thatmg393.vulkan.vma.VMAManager;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
public class BaseImage<B extends BaseImage.Builder> {
    private final B builder;

    private long id, allocation;
    private int aspectFlags;

    public BaseImage(B builder) {
        this.builder = builder;
        this.aspectFlags = ImageUtils.getAspect(builder.getFormat());
    }

    protected void initImage() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer id = stack.mallocLong(1);
            PointerBuffer allocation = stack.pointers(0L);

            VMAManager.getInstance().createImage(
                builder.getWidth(), builder.getHeight(),
                builder.getMipLevels(), builder.getFormat(),
                builder.getTiling(), builder.getUsage(),
                VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
                stack.ints(0), // Graphics Queue
                id, allocation
            );

            this.id = id.get(0);
            this.allocation = allocation.get(0);

            VMAManager.getInstance().addImage(this);
        }
    }

    @Getter
    @SuperBuilder(builderMethodName = "internalBuilder", toBuilder = true, setterPrefix = "set", buildMethodName = "buildVulkanImage")
    public static class Builder {
        private final int width, height;

        @lombok.Builder.Default private int format = VK_FORMAT_R8G8B8A8_UNORM;
        @lombok.Builder.Default private int tiling = VK_IMAGE_TILING_OPTIMAL;
        @lombok.Builder.Default private int usage = VK_IMAGE_USAGE_TRANSFER_DST_BIT | VK_IMAGE_USAGE_TRANSFER_SRC_BIT | VK_IMAGE_USAGE_SAMPLED_BIT;
        @lombok.Builder.Default private byte mipLevels = 1;
        @lombok.Builder.Default private byte samplerFlags = 0;
    }
}
