package com.thatmg393.legacyvkm.vulkan.vma;

import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.HashMap;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.vulkan.VkBufferCreateInfo;
import org.lwjgl.vulkan.VkImageCreateInfo;

import com.thatmg393.legacyvkm.vulkan.buffer.base.BaseBuffer;
import com.thatmg393.legacyvkm.vulkan.gpu.GPUManager;
import com.thatmg393.legacyvkm.vulkan.image.base.BaseImage;
import com.thatmg393.legacyvkm.vulkan.utils.ResultChecker;

import lombok.Builder;
import lombok.Getter;

public class VMAManager {
    @Getter
    private static final VMAManager instance = new VMAManager();

    private VMAManager() { }

    private final HashMap<Long, BaseImage> images = new HashMap<>();

    public void createBuffer(long size, int usage, int memoryFlags, LongBuffer bufferPtr, PointerBuffer memoryAllocPtr) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo vbci = VkBufferCreateInfo.calloc(stack);
            vbci.sType(VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            vbci.size(size);
            vbci.usage(usage);

            VmaAllocationCreateInfo vaci = VmaAllocationCreateInfo.calloc(stack);
            vaci.requiredFlags(memoryFlags);

            ResultChecker.checkResult(
                vmaCreateBuffer(
                    getVMA(), vbci, vaci, bufferPtr, memoryAllocPtr, null
                ),
                "Failed to create buffer"
            );
        }
    }

    public void createBuffer(BaseBuffer buffer, int size, int memoryFlags) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer bufferPtr = stack.mallocLong(1);
            PointerBuffer allocationPtr = stack.pointers(0);

            buffer.setMaxBufferSize(size);
            buffer.setId(bufferPtr.get(0));
            buffer.setAllocation(allocationPtr.get(0));

            createBuffer(size, buffer.getUsage(), memoryFlags, bufferPtr, allocationPtr);
        }
    }

    public void mapBuffer(long allocation, PointerBuffer data) {
        vmaMapMemory(getVMA(), allocation, data);
    }

    public void addImage(BaseImage image) {
        images.putIfAbsent(image.getId(), image);
    }

    public void createImage(
        int width, int height, 
        int mipLevels, int format, int tiling, int usage, int memoryFlags,
        IntBuffer queueFamily, LongBuffer pImage, PointerBuffer pImageMemory
    ) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkImageCreateInfo vici = VkImageCreateInfo.calloc(stack);
            vici.sType$Default();
            vici.imageType(VK_IMAGE_TYPE_2D);
            vici.mipLevels(mipLevels);
            vici.format(format);
            vici.tiling(tiling);
            vici.usage(usage);
            vici.arrayLayers(1);
            vici.initialLayout(VK_IMAGE_LAYOUT_UNDEFINED);
            vici.samples(VK_SAMPLE_COUNT_1_BIT);
            vici.sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            vici.pQueueFamilyIndices(queueFamily);
            vici.extent((e) -> {
                e.width(width); e.height(height); e.depth(1);
            });

            VmaAllocationCreateInfo vaci = VmaAllocationCreateInfo.calloc(stack);
            vaci.requiredFlags(memoryFlags);

            ResultChecker.checkResult(
                vmaCreateImage(
                    getVMA(), vici, vaci, pImage, pImageMemory, null
                ),
                "Failed to create image"
            );
        }
    }

    private final long getVMA() {
        return GPUManager.getInstance().getSelectedGPU().getVmaPtr();
    }
}
