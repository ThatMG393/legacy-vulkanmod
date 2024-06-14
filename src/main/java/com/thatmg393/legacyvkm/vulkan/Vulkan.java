package com.thatmg393.legacyvkm.vulkan;

import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWNativeGLX;
import org.lwjgl.glfw.GLFWVulkan;
import org.lwjgl.opengl.Display;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK11;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;

import com.thatmg393.legacyvkm.vulkan.gpu.GPUManager;
import com.thatmg393.legacyvkm.vulkan.utils.ResultChecker;

public class Vulkan {
    private static Vulkan SG_INSTANCE = new Vulkan();

    public static Vulkan getInstance() {
        return SG_INSTANCE;
    }

    private Vulkan() { }

    private VkInstance instance;
    private long surfacePtr;

    public void initialize(long windowPtr) {
        createVkInstance();
        setupSurface(Display.getHandle());
        
        GPUManager.getInstance().initAndSelectDevice(instance);
    }

    private void createVkInstance() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkApplicationInfo vai = VkApplicationInfo.calloc(stack);

            vai.sType(VK_STRUCTURE_TYPE_APPLICATION_INFO);
            vai.pApplicationName(stack.UTF8("Legacy VulkanMod"));
            vai.applicationVersion(VK_MAKE_VERSION(0, 0, 1));
            vai.pEngineName(stack.UTF8("OpenGL")); // lmao
            vai.engineVersion(VK_MAKE_VERSION(4, 6, 0));
            vai.apiVersion(VK11.VK_API_VERSION_1_1);

            VkInstanceCreateInfo vici = VkInstanceCreateInfo.calloc(stack);
            vici.sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO);
            vici.pApplicationInfo(vai);

            PointerBuffer instancePtr = stack.mallocPointer(1);
            ResultChecker.checkResult(vkCreateInstance(vici, null, instancePtr), "Failed to create a Vulkan instance");

            instance = new VkInstance(instancePtr.get(0), vici);
        }
    }

    private void setupSurface(long windowPtr) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer surfacePtr = stack.longs(VK10.VK_NULL_HANDLE);

            ResultChecker.checkResult(GLFWVulkan.glfwCreateWindowSurface(instance, windowPtr, null, surfacePtr), "Failed to create a Vulkan window");

            this.surfacePtr = surfacePtr.get(0);
        }
    }
}
