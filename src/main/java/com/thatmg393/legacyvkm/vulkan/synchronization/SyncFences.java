package com.thatmg393.legacyvkm.vulkan.synchronization;

import com.thatmg393.legacyvkm.vulkan.gpu.GPUManager;
import com.thatmg393.legacyvkm.vulkan.synchronization.base.Synchronization;

import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryUtil;

public class SyncFences extends Synchronization<Long> {
    public static final int MAX_FENCES = 64; // TODO: just a warning -> ALWAYS CHANGE THIS!
    private static final SyncFences INSTANCE = new SyncFences(MAX_FENCES);

    public static synchronized SyncFences getInstance() {
        return INSTANCE;
    }

    private final LongBuffer fences;

    private int index;

    public SyncFences(int maxFences) {
        fences = MemoryUtil.memAllocLong(maxFences);
        fences.limit(maxFences);
    }

    @Override
    public synchronized void add(Long obj) {
        if (index == MAX_FENCES) waitAll();
        fences.put(index++, obj);
    }

    public synchronized void waitAll() {
        if (index == 0) return;
        vkWaitForFences(
            GPUManager.getInstance().getSelectedGPU().asLogicalDevice(), 
            fences, true, Long.MAX_VALUE
        );
        index = 0;
    }
}
