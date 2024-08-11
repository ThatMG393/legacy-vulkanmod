package com.thatmg393.vkapi.synchronization;

import com.thatmg393.vkapi.queue.CommandPool;
import com.thatmg393.vkapi.queue.CommandPool.CommandBuffer;
import com.thatmg393.vkapi.synchronization.base.Synchronization;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;

public class SyncCommandBuffer extends Synchronization<CommandPool.CommandBuffer> {
    @Getter
    private static final SyncCommandBuffer instance = new SyncCommandBuffer();

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
