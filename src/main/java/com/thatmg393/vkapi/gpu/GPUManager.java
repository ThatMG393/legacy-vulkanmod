package com.thatmg393.vkapi.gpu;

import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;

import static org.lwjgl.vulkan.VK10.*;

import com.thatmg393.vkapi.Vulkan;
import com.thatmg393.vkapi.utils.ResultChecker;

public class GPUManager {
    private static GPUManager SG_INSTANCE = new GPUManager();

    public static GPUManager getInstance() {
        return SG_INSTANCE;
    }

    private GPUManager() { }

    private ArrayList<GPU> supportedGPUs = new ArrayList<>();
    private GPU selectedGPU;

    public void initAndSelectDevice(VkInstance instance) {
        populateSupportedGPUs(instance);
        selectGPUAndInit(0); // TODO: Get index from config
    }

    public void selectGPUAndInit(int index) {
        selectedGPU = supportedGPUs.get(index);
        selectedGPU.init();
    }

    public GPU getSelectedGPU() {
        return selectedGPU;
    }

    public void populateSupportedGPUs(VkInstance instance) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer deviceCount = stack.ints(0);

            ResultChecker.checkResult(vkEnumeratePhysicalDevices(instance, deviceCount, null), "Failed to get physical devices.");

            if (deviceCount.get(0) == 0) {
                return;
            }

            PointerBuffer physicalDevicesPtr = stack.mallocPointer(deviceCount.get(0));
            vkEnumeratePhysicalDevices(instance, deviceCount, physicalDevicesPtr);

            for (int i = 0; i < physicalDevicesPtr.capacity(); i++) {
                VkPhysicalDevice gpu = new VkPhysicalDevice(physicalDevicesPtr.get(i), instance);
                if (isGPUSupported(gpu)) supportedGPUs.add(new GPU(gpu));
            }
        }
    }

    public boolean isGPUSupported(VkPhysicalDevice device) {
        PointerBuffer glfwReqExt = Vulkan.getInstance().getRequiredDefaultExtensions();
        return true;
    }
}
