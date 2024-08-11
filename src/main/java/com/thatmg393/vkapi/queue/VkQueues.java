package com.thatmg393.vkapi.queue;

import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import com.thatmg393.vkapi.Vulkan;
import com.thatmg393.vkapi.synchronization.SyncCommandBuffer;

import lombok.Getter;

public enum VkQueues implements AutoCloseable {
    GraphicsQueue(QueueFamilyIndices.getGraphicsFamily(), false),
    PresentQueue(QueueFamilyIndices.getPresentFamily(), false),
    TransferQueue(QueueFamilyIndices.getTransferFamily(), false);

    private final VkQueue queue;
    private final VkDevice currentDevice;
    private final CommandPool commandPool;

    @Getter
    private final int familyIndex;
    
    private CommandPool.CommandBuffer currentCmdBuf;

    VkQueues(int familyIndex, boolean initCommandPool) {
        this.currentDevice = Vulkan.getInstance().getCurrentGPU().asLogicalDevice();
        this.familyIndex = familyIndex;
        this.commandPool = initCommandPool ? new CommandPool(currentDevice, familyIndex) : null;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer queuePtr = stack.mallocPointer(1);
            vkGetDeviceQueue(
                currentDevice,
                familyIndex, 0, queuePtr
            );

            this.queue = new VkQueue(queuePtr.get(0), currentDevice);
        }
    }

    public CommandPool.CommandBuffer beginCommands() {
        return this.commandPool.beginCommands();
    }

    public long submitCommands(CommandPool.CommandBuffer cb) {
        return this.commandPool.submitCommands(cb, queue);
    }

    public long copyBufferCommand(long srcBuf, long srcOff, long dstBuf, long dstOff, int size) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            CommandPool.CommandBuffer cb = beginCommands();
            uploadBufferCmd(cb.getCmdBufHandle(), srcBuf, srcOff, dstBuf, dstOff, size);
            
            submitCommands(cb);
            SyncCommandBuffer.getInstance().add(cb);
            return cb.getFenceHandle();
        }
    }

    public void uploadCmdBufImmediate(long srcBuf, long srcOff, long dstBuf, long dstOff, int size) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            CommandPool.CommandBuffer cb = beginCommands();
            uploadBufferCmd(cb.getCmdBufHandle(), srcBuf, srcOff, dstBuf, dstOff, size);
            vkWaitForFences(currentDevice, cb.getFenceHandle(), true, Long.MAX_VALUE);
            cb.reset();
        }
    }

    public void uploadBufferCmd(VkCommandBuffer cmdBuf, long srcBuf, long srcOff, long dstBuf, long dstOff, long size) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCopy.Buffer vbcb = VkBufferCopy.malloc(1, stack);
            vbcb.size(size);
            vbcb.srcOffset(srcOff);
            vbcb.dstOffset(dstOff);

            vkCmdCopyBuffer(cmdBuf, srcBuf, dstBuf, vbcb);
        }
    }

    public void startRecording() {
        currentCmdBuf = beginCommands();
    }

    public void endRecordingAndSubmit() {
        submitCommands(currentCmdBuf);
        SyncCommandBuffer.getInstance().add(currentCmdBuf);
        currentCmdBuf = null;
    }

    public long endIfNeeded(CommandPool.CommandBuffer cb) {
        return currentCmdBuf != null ? VK_NULL_HANDLE : submitCommands(currentCmdBuf);
    }

    public void waitIdle() {
        vkQueueWaitIdle(queue);
    }

    @Override
    public void close() {
        if (commandPool != null) commandPool.close();
    }
}
