package com.thatmg393.legacyvkm.vulkan.gpu;

import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkFormatProperties;
import org.lwjgl.vulkan.VkPhysicalDevice;

import com.thatmg393.legacyvkm.vulkan.utils.ResultChecker;

public class GPU {
    public final String name;
    public final String vendorName;
    public final int apiVersion;

    private VkPhysicalDevice physicalDevice;
    private long vmaPtr;

    private int depthFormat = -69420;

    public GPU(VkPhysicalDevice physicalDevice) {
        this.physicalDevice = physicalDevice;

        initializeVMA();
        initializeFinalVars();
    }

    public int getOptimalDepthFormat() {
        if (depthFormat != -69420) return depthFormat;

        int[] optimalDepthFormat = null;
        
        if (deviceVendor.toLowerCase().equals("nvidia")) optimalDepthFormat = new int[] { VK_FORMAT_D24_UNORM_S8_UINT, VK_FORMAT_X8_D24_UNORM_PACK32, VK_FORMAT_D32_SFLOAT, VK_FORMAT_D32_SFLOAT_S8_UINT };
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

    private void initializeVMA() {
        
    }

    private void initializeFinalVars() {

    }
}
