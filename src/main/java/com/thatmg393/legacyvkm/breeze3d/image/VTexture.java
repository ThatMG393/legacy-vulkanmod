package com.thatmg393.legacyvkm.breeze3d.image;

import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkImageViewCreateInfo;

import com.thatmg393.vulkan.gpu.GPUManager;
import com.thatmg393.vulkan.image.base.BaseImage;
import com.thatmg393.vulkan.utils.ResultChecker;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

public class VTexture extends BaseImage<VTexture.Builder> {
    @Getter
    private long mainImageView;

    public VTexture(VTexture.Builder builder) {
        super(builder);
    }

    @Override
    protected void initImage() {
        super.initImage();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            
            VkImageViewCreateInfo vivci = VkImageViewCreateInfo.calloc(stack);
            vivci.sType(VK_STRUCTURE_TYPE_IMAGE_VIEW_CREATE_INFO);
            vivci.viewType(VK_IMAGE_VIEW_TYPE_2D);
            vivci.image(getId());
            vivci.format(getBuilder().getFormat());
            vivci.subresourceRange(v -> {
                v.aspectMask(getAspectFlags());
                v.baseMipLevel(getBuilder().getBaseMipLevel());
                v.levelCount(getBuilder().getMipLevels());
                v.baseArrayLayer(0);
                v.layerCount(1);
            });

            LongBuffer imageView = stack.mallocLong(1);

            ResultChecker.checkResult(
                vkCreateImageView(
                    GPUManager.getInstance().getSelectedGPU().asLogicalDevice(),
                    vivci, null, imageView
                ),
                "Failed to create image view."
            );

            mainImageView = imageView.get(0);
        }
    }

    @Getter
    @SuperBuilder(builderMethodName = "internalBuilder", toBuilder = true, setterPrefix = "set", buildMethodName = "buildVulkanImage")
    public static class Builder extends BaseImage.Builder {
        @lombok.Builder.Default private int baseMipLevel = 0;
    }
}
