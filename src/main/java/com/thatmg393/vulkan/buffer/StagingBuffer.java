package com.thatmg393.vulkan.buffer;

import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_SRC_BIT;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.libc.LibCString;

import com.thatmg393.vulkan.buffer.base.BaseBuffer;
import com.thatmg393.vulkan.buffer.utils.BufferUtils;
import com.thatmg393.vulkan.memory.MemoryType;

public class StagingBuffer extends BaseBuffer {
    public StagingBuffer(int size) {
        super(VK_BUFFER_USAGE_TRANSFER_SRC_BIT, MemoryType.BAR_MEM);
        create(size);
    }

    public void copyBuffer(ByteBuffer data) {
        BufferUtils.resizeIfNeeded(this, (getMaxBufferSize() + data.capacity()) * 2);

        LibCString.nmemcpy(getData().get(0) + getUsedBufferSize(), MemoryUtil.memAddress(data), data.capacity());

        setOffset(getUsedBufferSize());
        increaseUsedBufferSize(data.capacity());
    }

    public void alignBuffer(int alignment) {
        int alignBy = BufferUtils.alignBuffer(getUsedBufferSize(), alignment);

        if (alignBy > getMaxBufferSize()) {
            BufferUtils.resizeBuffer(this, getMaxBufferSize() * 2);
        }

        setUsedBufferSize(alignBy);
    }
}
