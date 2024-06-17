package com.thatmg393.legacyvkm.vulkan.memory;

import static org.lwjgl.vulkan.VK10.*;

import java.nio.ByteBuffer;

import org.lwjgl.vulkan.VkMemoryHeap;
import org.lwjgl.vulkan.VkMemoryType;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

import com.thatmg393.legacyvkm.vulkan.buffer.StagingBuffer;
import com.thatmg393.legacyvkm.vulkan.buffer.base.BaseBuffer;
import com.thatmg393.legacyvkm.vulkan.gpu.GPUManager;

public enum MemoryType {
    GPU_MEM(0, 1, 2),
    BAR_MEM();

    private final long maxMemory;
    private long usedMemory;

    private final int memoryFlags;

    MemoryType(int... flags) {
        VkPhysicalDeviceMemoryProperties vpdmp = GPUManager.getInstance().getSelectedGPU().phyDevMemProperties;

        for (int flagMask : flags) {
            for (VkMemoryType memType : vpdmp.memoryTypes()) {
                VkMemoryHeap memHeap = vpdmp.memoryHeaps(memType.heapIndex());
                if ((flagMask & memType.propertyFlags()) == flagMask) {
                    this.maxMemory = memHeap.size();
                    this.memoryFlags = flagMask;

                    return;
                }
            }
        }

        throw new RuntimeException("Unsupported memory type " + this.name());
    }

    void createBuffer(BaseBuffer buffer, int size) {
        final int usage = buffer.usage | (this.equals(BAR_MEM) ? 0 : VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_TRANSFER_SRC_BIT);
        // MemoryManager

        usedMemory += size;
    }

    void copyToBuffer(BaseBuffer target, ByteBuffer data) {
        if (mappable()) {
            StagingBuffer stagingBuf = null;
        }
    }

    boolean mappable() {
        return true;
    }

    public int getMaxMemory() {
        return (int) (maxMemory >> 20);
    }

    public int getUsedMemory() {
        return (int) (usedMemory >> 20);
    }
}
