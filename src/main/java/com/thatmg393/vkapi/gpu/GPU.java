package com.thatmg393.vkapi.gpu;

import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocatorCreateInfo;
import org.lwjgl.util.vma.VmaVulkanFunctions;
import org.lwjgl.vulkan.*;

import com.thatmg393.vkapi.Vulkan;
import com.thatmg393.vkapi.queue.QueueFamilyIndices;
import com.thatmg393.vkapi.utils.GPUPropertiesUtil;
import com.thatmg393.vkapi.utils.ResultChecker;

import lombok.Getter;

public class GPU {
    public final String name;
    public final String vendorName;
    public final int apiVersion;
    
    public final VkPhysicalDeviceProperties phyDevProperties;
    public final VkPhysicalDeviceMemoryProperties phyDevMemProperties;

    @Getter
    private long vmaPtr;

    private VkDevice logicalDevice;
    private VkPhysicalDevice physicalDevice;
    private int depthFormat = -69420;

    public GPU(VkPhysicalDevice physicalDevice) {
        this.physicalDevice = physicalDevice;

        this.phyDevProperties = VkPhysicalDeviceProperties.malloc();
        vkGetPhysicalDeviceProperties(physicalDevice, phyDevProperties);

        this.phyDevMemProperties = VkPhysicalDeviceMemoryProperties.malloc();
        vkGetPhysicalDeviceMemoryProperties(physicalDevice, phyDevMemProperties);

        this.name = phyDevProperties.deviceNameString();
        this.vendorName = GPUPropertiesUtil.vendorIDToString(phyDevProperties.vendorID());
        this.apiVersion = phyDevProperties.apiVersion();
    }

    public int getOptimalDepthFormat() {
        if (depthFormat != -69420) return depthFormat;

        int[] optimalDepthFormat = null;
        
        if (vendorName.toLowerCase().equals("nvidia")) optimalDepthFormat = new int[] { VK_FORMAT_D24_UNORM_S8_UINT, VK_FORMAT_X8_D24_UNORM_PACK32, VK_FORMAT_D32_SFLOAT, VK_FORMAT_D32_SFLOAT_S8_UINT };
        optimalDepthFormat = new int[] { VK_FORMAT_D32_SFLOAT, VK_FORMAT_D32_SFLOAT_S8_UINT };
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkFormatProperties vfp = VkFormatProperties.calloc(stack);

            for (int format : optimalDepthFormat) {
                vkGetPhysicalDeviceFormatProperties(physicalDevice, format, vfp);

                if ((vfp.optimalTilingFeatures() & VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT) == VK_FORMAT_FEATURE_DEPTH_STENCIL_ATTACHMENT_BIT) {
                    depthFormat = format;
                    return depthFormat;
                }
            }
        }

        ResultChecker.checkResult(VK_ERROR_FORMAT_NOT_SUPPORTED, "Failed to find a supported depth format");
        return 0;
    }

    public VkDevice asLogicalDevice() {
        return this.logicalDevice;
    }

    protected void init() {
        initializeVMA();
        QueueFamilyIndices.findQueueFamilies(physicalDevice);
    }

    private void initializeVMA() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VmaVulkanFunctions vvf = VmaVulkanFunctions.calloc(stack);
            vvf.set(Vulkan.getInstance().getVkInstance(), asLogicalDevice());

            VmaAllocatorCreateInfo vai = VmaAllocatorCreateInfo.calloc(stack);
            vai.physicalDevice(physicalDevice);
            vai.device(asLogicalDevice());
            vai.pVulkanFunctions(vvf);
            vai.instance(Vulkan.getInstance().getVkInstance());
            vai.vulkanApiVersion(VK11.VK_API_VERSION_1_1);

            PointerBuffer vmaPtr = stack.pointers(VK_NULL_HANDLE);

            ResultChecker.checkResult(Vma.vmaCreateAllocator(vai, vmaPtr), "Failed to create VMA");

            this.vmaPtr = vmaPtr.get(0);
        }
    }
}
