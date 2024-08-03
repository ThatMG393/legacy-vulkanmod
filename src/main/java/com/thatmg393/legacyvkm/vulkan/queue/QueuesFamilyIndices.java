package com.thatmg393.legacyvkm.vulkan.queue;

import static org.lwjgl.vulkan.VK10.*;

import java.nio.IntBuffer;
import java.util.stream.IntStream;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueueFamilyProperties;

import lombok.Getter;

public class QueuesFamilyIndices {
    @Getter
    private static int graphicsFamily = VK_QUEUE_FAMILY_IGNORED, presentFamily = VK_QUEUE_FAMILY_IGNORED, transferFamily = VK_QUEUE_FAMILY_IGNORED;
    private static boolean hasDedicatedTransferQueue = false;
                     
    public static boolean findQueueFamilies(VkPhysicalDevice physicalDevice) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer familyCountBuf = stack.ints(0);
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, familyCountBuf, null);

            VkQueueFamilyProperties.Buffer queueFamilies = VkQueueFamilyProperties.malloc(familyCountBuf.get(0), stack);
            vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, familyCountBuf, queueFamilies);
            if (queueFamilies.capacity() == 1) {
                graphicsFamily = presentFamily = transferFamily = 0;
                return isFamilyComplete();
            }

            for (int i = 0; i < queueFamilies.capacity(); ++i) {
                int queueFlags = queueFamilies.get(i).queueFlags();

                if ((queueFlags & VK_QUEUE_GRAPHICS_BIT) != 0) {
                    graphicsFamily = i;
                    if ((queueFlags & VK_QUEUE_COMPUTE_BIT) != 0) {
                        transferFamily = i;
                    }
                }

                if ((queueFlags & (VK_QUEUE_COMPUTE_BIT | VK_QUEUE_GRAPHICS_BIT)) == 0
                && (queueFlags & VK_QUEUE_TRANSFER_BIT) != 0) {
                    transferFamily = i;
                }

                if (presentFamily == VK_QUEUE_FAMILY_IGNORED && ((queueFlags & VK_QUEUE_COMPUTE_BIT) != 0)) {
                    presentFamily = i;
                }

                if (isFamilyComplete()) return true;
            }

            if (transferFamily == VK_QUEUE_FAMILY_IGNORED) {
                int fallback = VK_QUEUE_FAMILY_IGNORED;
                for (int i = 0; i < queueFamilies.capacity(); i++) {
                    int queueFlags = queueFamilies.get(i).queueFlags();
                    if ((queueFlags & VK_QUEUE_TRANSFER_BIT) != 0) {
                        if (fallback == VK_QUEUE_FAMILY_IGNORED)
                            fallback = i;
                        
                        if ((queueFlags & (VK_QUEUE_GRAPHICS_BIT)) == 0)
                            fallback = i;

                        if (fallback == VK_QUEUE_FAMILY_IGNORED)
                            throw new RuntimeException("No queue family with transfer support");

                        transferFamily = fallback;
                    }
                }
            }

            hasDedicatedTransferQueue = graphicsFamily != transferFamily;

            if (graphicsFamily == VK_QUEUE_FAMILY_IGNORED)
                throw new RuntimeException("No queue family with graphics support");
            if (presentFamily == VK_QUEUE_FAMILY_IGNORED)
                throw new RuntimeException("No queue family with present support");

            return isFamilyComplete();
        }
    }

    public static boolean isFamilyComplete() {
        return graphicsFamily != VK_QUEUE_FAMILY_IGNORED
        && presentFamily != VK_QUEUE_FAMILY_IGNORED
        && transferFamily != VK_QUEUE_FAMILY_IGNORED;
    }

    public static int[] uniqueFamily() {
        return IntStream.of(graphicsFamily, presentFamily, transferFamily).distinct().toArray();
    }

    public static boolean hasDedicatedTransferQueue() {
        return hasDedicatedTransferQueue;
    }
}
