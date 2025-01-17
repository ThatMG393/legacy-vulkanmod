package com.thatmg393.vulkan.vma;

import static org.lwjgl.util.vma.Vma.*;
import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.*;
import org.lwjgl.vulkan.*;

import com.thatmg393.vulkan.Vulkan;
import com.thatmg393.vulkan.buffer.base.BaseBuffer;
import com.thatmg393.vulkan.gpu.GPU;
import com.thatmg393.vulkan.utils.ResultChecker;

import lombok.Getter;

public class VMAManager {
    @Getter
    private static final VMAManager instance = new VMAManager();

    private VMAManager() { }

    @Getter
    private long vmaInstance;

    public void initializeVMA(VkPhysicalDevice physicalDevice) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GPU device = Vulkan.getInstance().getCurrentGPU();

            VmaVulkanFunctions vvf = VmaVulkanFunctions.calloc(stack);
            vvf.set(Vulkan.getInstance().getVkInstance(), device.asLogicalDevice());

            VmaAllocatorCreateInfo vai = VmaAllocatorCreateInfo.calloc(stack);
            vai.physicalDevice(physicalDevice);
            vai.device(device.asLogicalDevice());
            vai.pVulkanFunctions(vvf);
            vai.instance(Vulkan.getInstance().getVkInstance());
            vai.vulkanApiVersion(VK11.VK_API_VERSION_1_1);

            PointerBuffer vmaPtr = stack.pointers(VK_NULL_HANDLE);

            ResultChecker.checkResult(Vma.vmaCreateAllocator(vai, vmaPtr), "Failed to create VMA");

            vmaInstance = vmaPtr.get(0);
        }
    }

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
                    getVmaInstance(), vbci, vaci, bufferPtr, memoryAllocPtr, null
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
        vmaMapMemory(getVmaInstance(), allocation, data);
    }
}
