package com.thatmg393.vulkan.vma;

import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.*;
import java.util.HashMap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VkImageCreateInfo;

import com.thatmg393.vulkan.image.base.BaseImage;
import com.thatmg393.vulkan.utils.ResultChecker;
import com.thatmg393.vulkan.vma.base.*;

import lombok.*;

public class VMAImage extends BaseVMAFeature<VMAImage.Builder> {
    @Getter
    private static final VMAImage instance = new VMAImage();

    private VMAImage() { }

    @AllArgsConstructor
    public static class Builder extends BaseFeatureBuilder {
        private int width, height, depth;
        private byte mipLevels;
        private int format, tiling, usage, memoryFlags;
   
        private IntBuffer queueFamily;
        private LongBuffer imagePtr;
        private PointerBuffer imageMemoryPtr;
    }

    private final HashMap<Long, BaseImage<? extends BaseImage.Builder>> images = new HashMap<>();

    @Override
    public void create(Builder params) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkImageCreateInfo vici = VkImageCreateInfo.calloc(stack);
            vici.sType$Default();
            vici.imageType(VK_IMAGE_TYPE_2D);
            vici.mipLevels(params.mipLevels);
            vici.format(params.format);
            vici.tiling(params.tiling);
            vici.usage(params.usage);
            vici.arrayLayers(1);
            vici.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            vici.samples(VK_SAMPLE_COUNT_1_BIT);
            vici.sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            vici.pQueueFamilyIndices(params.queueFamily);
            vici.extent((e) -> {
                e.width(params.width); 
                e.height(params.height); 
                e.depth(params.depth);
            });

            VmaAllocationCreateInfo vaci = VmaAllocationCreateInfo.calloc(stack);
            vaci.requiredFlags(params.memoryFlags);

            ResultChecker.checkResult(
                vmaCreateImage(
                    VMAManager.getInstance().getVmaInstance(), vici, vaci, params.imagePtr, params.imageMemoryPtr, null
                ),
                "Failed to create image"
            );                                                                                                                                                                                                                                                                    
        }
    }

    @Override
    public void destroy(long id) {
        BaseImage<?> image = images.remove(id);
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            vmaDestroyImage(VMAManager.getInstance().getVmaInstance(), id, image.getAllocation());
        }
    }

    public void addImage(BaseImage<? extends BaseImage.Builder> image) {
        images.putIfAbsent(image.getId(), image);
    }
}
