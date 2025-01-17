package com.thatmg393.vulkan.buffer;

public class BufferManager {
    private static final BufferManager INSTANCE = new BufferManager();

    public static BufferManager getInstance() {
        return INSTANCE;
    }

    private StagingBuffer[] stagingBuffers;

    public void initializeStagingBuffers() {
        if (stagingBuffers != null) return;

        stagingBuffers = new StagingBuffer[1 /* frameNum */];

        for (int i = 0; i < stagingBuffers.length; i++) {
            stagingBuffers[i] = new StagingBuffer(31457280);
        }
    }

    public StagingBuffer getStagingBuffer(int frameNum) {
        return stagingBuffers[frameNum];
    }
}
