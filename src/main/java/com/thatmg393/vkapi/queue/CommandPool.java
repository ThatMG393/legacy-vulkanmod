package com.thatmg393.vkapi.queue;

import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkFenceCreateInfo;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;

import com.thatmg393.vkapi.utils.ResultChecker;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;

public class CommandPool implements AutoCloseable {
    private static final int DEFAULT_COMMAND_BUFFER_SIZE = 10;

    private List<CommandBuffer> allCommandBuffers = new ObjectArrayList<>();
    private Queue<CommandBuffer> availableCommandBuffers = new ArrayDeque<>();

    private final VkDevice currentDevice;
    private final long commandPoolPtr;

    public CommandPool(VkDevice currentDevice, int familyIndex) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandPoolCreateInfo vcpci = VkCommandPoolCreateInfo.calloc(stack);
            vcpci.sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO);
            vcpci.queueFamilyIndex(familyIndex);
            vcpci.flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT);

            LongBuffer commandPoolPtr = stack.mallocLong(1);
            ResultChecker.checkResult(
                vkCreateCommandPool(currentDevice, vcpci, null, commandPoolPtr),
                "Failed to create a command pool."
            );

            this.commandPoolPtr = commandPoolPtr.get(0);
            this.currentDevice = currentDevice;

            initializeCommandPools();
        }
    }

    public void initializeCommandPools() {
        if (!availableCommandBuffers.isEmpty()) return;
        try (MemoryStack stack = MemoryStack.stackPush()) {
                VkCommandBufferAllocateInfo vcbai = VkCommandBufferAllocateInfo.calloc(stack);
                vcbai.sType$Default();
                vcbai.level(VK_COMMAND_BUFFER_LEVEL_PRIMARY);
                vcbai.commandPool(commandPoolPtr);
                vcbai.commandBufferCount(DEFAULT_COMMAND_BUFFER_SIZE);

                PointerBuffer commandBufferPtrs = stack.mallocPointer(DEFAULT_COMMAND_BUFFER_SIZE);
                vkAllocateCommandBuffers(
                    currentDevice,
                    vcbai,
                    commandBufferPtrs
                );

                VkFenceCreateInfo vfci = VkFenceCreateInfo.calloc(stack);
                vfci.sType$Default();
                vfci.flags(VK_FENCE_CREATE_SIGNALED_BIT);

                LongBuffer fencePtr = stack.mallocLong(1);

                for (int i = 0; i < DEFAULT_COMMAND_BUFFER_SIZE; i++) {
                    vkCreateFence(
                        currentDevice,
                        vfci,
                        null,
                        fencePtr
                    );

                    CommandBuffer cb = new CommandBuffer(
                        new VkCommandBuffer(
                            commandBufferPtrs.get(i),
                            currentDevice
                        ), fencePtr.get(0)
                    );

                    allCommandBuffers.add(cb);
                    addToAvailableBuffers(cb);
                }
        }
    }

    public CommandBuffer beginCommands() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            CommandBuffer cb = availableCommandBuffers.poll();
            
            VkCommandBufferBeginInfo vcbbi = VkCommandBufferBeginInfo.calloc(stack);
            vcbbi.sType(VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);
            vcbbi.flags(VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
            
            vkBeginCommandBuffer(cb.cmdBufHandle, vcbbi);

            return cb;
        }
    }

    public long submitCommands(CommandBuffer cb, VkQueue queue) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            vkEndCommandBuffer(cb.cmdBufHandle);
            vkResetFences(currentDevice, cb.fenceHandle);

            VkSubmitInfo vsi = VkSubmitInfo.calloc(stack);
            vsi.sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
            vsi.pCommandBuffers(stack.pointers(cb.fenceHandle));

            vkQueueSubmit(queue, vsi, cb.fenceHandle);

            return cb.fenceHandle;
        }
    }

    @Override
    public void close() {
        allCommandBuffers.forEach((cb) -> vkDestroyFence(currentDevice, cb.fenceHandle, null));
        vkResetCommandPool(currentDevice, commandPoolPtr, VK_COMMAND_POOL_RESET_RELEASE_RESOURCES_BIT);
        vkDestroyCommandPool(currentDevice, commandPoolPtr, null);
    }

    private void addToAvailableBuffers(CommandBuffer availCb) {
        availableCommandBuffers.add(availCb);
    }

    @Getter
    public class CommandBuffer {
        private final VkCommandBuffer cmdBufHandle;
        private final long fenceHandle;

        private boolean isSubmitted;
        private boolean isRecording;

        CommandBuffer(VkCommandBuffer cmdBufHandle, long fenceHandle) {
            this.cmdBufHandle = cmdBufHandle;
            this.fenceHandle = fenceHandle;
        }

        public void reset() {
            isSubmitted = false;
            isRecording = false;
            addToAvailableBuffers(this);
        }
    }
}
