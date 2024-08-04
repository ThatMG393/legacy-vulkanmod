package com.thatmg393.legacyvkm.vulkan.queue;

import static org.lwjgl.vulkan.VK10.*;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkBufferCopy;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkQueue;

import com.thatmg393.legacyvkm.vulkan.gpu.GPUManager;

public enum VkQueues implements AutoCloseable {
    GraphicsQueue(QueueFamilyIndices.getGraphicsFamily(), false);

    private final int familyIndex;
    private final CommandPool commandPool;
    private final VkQueue queue;
    private CommandPool.CommandBuffer currentCmdBuf;

    VkQueues(int familyIndex, boolean initCommandPool) {
        this.familyIndex = familyIndex;
        this.commandPool = initCommandPool ? new CommandPool(GPUManager.getInstance().getSelectedGPU().asLogicalDevice(), familyIndex) : null;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer queuePtr = stack.mallocPointer(1);
            vkGetDeviceQueue(
                GPUManager.getInstance().getSelectedGPU().asLogicalDevice(),
                familyIndex, 0, queuePtr
            );

            this.queue = new VkQueue(queuePtr.get(0), GPUManager.getInstance().getSelectedGPU().asLogicalDevice());
        }
    }

    public CommandPool.CommandBuffer beginCommands() {
        return this.commandPool.beginCommands();
    }

    public long submitCommands(CommandPool.CommandBuffer cb) {
        return this.commandPool.submitCommands(cb, queue);
    }

    public void copyBufferCommand(long srcBuf, long srcOff, long dstBuf, long dstOff, int size) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            CommandPool.CommandBuffer cb = beginCommands();
            uploadBufferCmd(cb.cmdBufHandle, srcBuf, srcOff, dstBuf, dstOff, size);
            submitCommands(cb);
        }
    }

    public void uploadCmdBufImmediate(long srcBuf, long srcOff, long dstBuf, long dstOff, int size) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            CommandPool.CommandBuffer cb = beginCommands();
            uploadBufferCmd(cb.cmdBufHandle, srcBuf, srcOff, dstBuf, dstOff, size);
            vkWaitForFences(GPUManager.getInstance().getSelectedGPU().asLogicalDevice(), cb.fenceHandle, true, Long.MAX_VALUE);
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
        long fence = submitCommands(currentCmdBuf);
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

    public int getFamilyIndex() {
        return this.familyIndex;
    }
}
