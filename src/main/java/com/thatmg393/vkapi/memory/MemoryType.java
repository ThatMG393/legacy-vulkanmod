package com.thatmg393.vkapi.memory;

import static org.lwjgl.vulkan.VK10.*;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.vulkan.VkMemoryHeap;
import org.lwjgl.vulkan.VkMemoryType;
import org.lwjgl.vulkan.VkPhysicalDeviceMemoryProperties;

import com.thatmg393.vkapi.buffer.BufferManager;
import com.thatmg393.vkapi.buffer.StagingBuffer;
import com.thatmg393.vkapi.buffer.base.BaseBuffer;
import com.thatmg393.vkapi.gpu.GPUManager;
import com.thatmg393.vkapi.vma.VMAManager;

public enum MemoryType {
    GPU_MEM(
        true,
        VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
        VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT | VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT,
        VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
    ),
    BAR_MEM(
        true,
        VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT | VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
        VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT
    ),
    RAM_MEM(
        false,
        VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT | VK_MEMORY_PROPERTY_HOST_COHERENT_BIT,
        0
    );

    private final long maxMemory;
    private long usedMemory;

    private final int memoryFlags;

    MemoryType(boolean useVRAM, int... flags) {
        VkPhysicalDeviceMemoryProperties vpdmp = GPUManager.getInstance().getSelectedGPU().phyDevMemProperties;
        final int VRAMFlag = useVRAM ? VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT : (hasHeapFlag(0) ? 0 : VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT);
        for (int flagMask : flags) {
            for (VkMemoryType memType : vpdmp.memoryTypes()) {
                VkMemoryHeap memHeap = vpdmp.memoryHeaps(memType.heapIndex());
                int availableFlags = memType.propertyFlags();

                if ((flagMask & availableFlags) == flagMask && (useVRAM == ((availableFlags & VRAMFlag) != 0))) {
                    this.maxMemory = memHeap.size();
                    this.memoryFlags = flagMask;

                    return;
                }
            }
        }

        throw new RuntimeException("Unsupported memory type " + this.name());
    }

    public void createBuffer(BaseBuffer buffer, int size) {
        buffer.setUsage(buffer.getUsage() | (this.equals(BAR_MEM) ? 0 : VK_BUFFER_USAGE_TRANSFER_DST_BIT | VK_BUFFER_USAGE_TRANSFER_SRC_BIT));
        VMAManager.getInstance().createBuffer(buffer, size, this.memoryFlags);

        usedMemory += size;
    }

    public void copyToBuffer(BaseBuffer target, ByteBuffer data) {
        if (isMappable()) {
            StagingBuffer stagingBuf = BufferManager.getInstance().getStagingBuffer(1 /* currentFrame */);
            stagingBuf.copyBuffer(data);
            // transfer.copyBufCmd
        } else MemoryUtil.memCopy(data, target.getData().getByteBuffer(0, target.getMaxBufferSize()));
    }

    public void freeBuffer(BaseBuffer buffer) {
        usedMemory -= buffer.getMaxBufferSize();
    }

    public boolean isMappable() {
        return (this.memoryFlags & VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT) != 0;
    }

    public int getMaxMemory() {
        return (int) (maxMemory >> 20);
    }

    public int getUsedMemory() {
        return (int) (usedMemory >> 20);
    }

    private boolean hasHeapFlag(int heapFlag) {
        return GPUManager.getInstance().getSelectedGPU().phyDevMemProperties.memoryHeaps().parallelStream().anyMatch(e -> e.flags() == heapFlag);
    }
}
