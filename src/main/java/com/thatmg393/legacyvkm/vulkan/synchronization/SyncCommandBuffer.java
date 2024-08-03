package com.thatmg393.legacyvkm.vulkan.synchronization;

import com.thatmg393.legacyvkm.vulkan.queue.CommandPool;
import com.thatmg393.legacyvkm.vulkan.queue.CommandPool.CommandBuffer;
import com.thatmg393.legacyvkm.vulkan.synchronization.base.Synchronization;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class SyncCommandBuffer extends Synchronization<CommandPool.CommandBuffer> {
    private static final SyncCommandBuffer INSTANCE = new SyncCommandBuffer();

    public static SyncCommandBuffer getInstance() {
        return INSTANCE;
    }

    private final ObjectArrayList<CommandPool.CommandBuffer> commandBuffers = new ObjectArrayList<>();

    @Override
    public synchronized void add(CommandBuffer obj) {
        SyncFences.getInstance().add(obj.getFenceHandle());
        commandBuffers.add(obj);
    }

    public synchronized void waitAll() {
        SyncFences.getInstance().waitAll();
        commandBuffers.forEach(CommandPool.CommandBuffer::reset);
        commandBuffers.clear();
    }
}
