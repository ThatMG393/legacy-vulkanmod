package com.thatmg393.vulkan.gpu;

import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import com.thatmg393.vulkan.Vulkan;
import com.thatmg393.vulkan.queue.QueueFamilyIndices;
import com.thatmg393.vulkan.utils.*;
import com.thatmg393.vulkan.vma.VMAManager;

public class GPU {
    public final String name;
    public final String vendorName;
    public final int apiVersion;
    
    public final VkPhysicalDeviceProperties phyDevProperties;
    public final VkPhysicalDeviceMemoryProperties phyDevMemProperties;

    private VkPhysicalDevice physicalDevice;
    private VkDevice logicalDevice;
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
        if (logicalDevice != null) return this.logicalDevice;
        
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int[] families = QueueFamilyIndices.uniqueFamily();
            VkDeviceQueueCreateInfo.Buffer vdqcib = VkDeviceQueueCreateInfo.calloc(families.length, stack);

            for (int i = 0; i < families.length; i++) {
                VkDeviceQueueCreateInfo vdqci = vdqcib.get(i);
                vdqci.sType$Default();
                vdqci.queueFamilyIndex(families[i]);
                vdqci.pQueuePriorities(stack.floats(1.0f));
            }
            
            VkPhysicalDeviceVulkan11Features vpdv11f = VkPhysicalDeviceVulkan11Features.calloc(stack);
            vpdv11f.sType$Default();

            VkPhysicalDeviceFeatures2 vpdf2 = VkPhysicalDeviceFeatures2.calloc(stack);
            vpdf2.sType$Default();

            VkDeviceCreateInfo vdci = VkDeviceCreateInfo.calloc(stack);
            vdci.sType$Default();
            vdci.pQueueCreateInfos(vdqcib);
            vdci.pEnabledFeatures(vpdf2.features());
            vdci.pNext(vpdv11f);
            vdci.ppEnabledExtensionNames(PointerBufferUtils.asPointerBuffer(Vulkan.REQUIRED_EXTENSIONS));

            PointerBuffer logicalDevicePtr = stack.pointers(VK_NULL_HANDLE);
            ResultChecker.checkResult(
                vkCreateDevice(physicalDevice, vdci, null, logicalDevicePtr),
                "Failed to create logical device."
            );

            this.logicalDevice = new VkDevice(logicalDevicePtr.get(0), physicalDevice, vdci, VK11.VK_API_VERSION_1_1);
        }

        return this.logicalDevice;
    }

    protected void init() {
        VMAManager.getInstance().initializeVMA(physicalDevice);
        QueueFamilyIndices.findQueueFamilies(physicalDevice);
    }
}
