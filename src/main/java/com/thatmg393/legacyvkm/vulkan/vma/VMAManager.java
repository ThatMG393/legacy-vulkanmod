package com.thatmg393.legacyvkm.vulkan.vma;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.util.vma.Vma.*;

import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VkBufferCreateInfo;

import com.thatmg393.legacyvkm.vulkan.buffer.base.BaseBuffer;
import com.thatmg393.legacyvkm.vulkan.gpu.GPUManager;
import com.thatmg393.legacyvkm.vulkan.utils.ResultChecker;

public class VMAManager {
    private static final VMAManager INSTANCE = new VMAManager();

    public static VMAManager getInstance() {
        return INSTANCE;
    }

    private VMAManager() { }

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
                    GPUManager.getInstance().getSelectedGPU().getVMA(),
                    vbci, vaci, bufferPtr, memoryAllocPtr, null
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
        vmaMapMemory(GPUManager.getInstance().getSelectedGPU().getVMA(), allocation, data);
    }
}
