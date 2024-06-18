package com.thatmg393.legacyvkm.vulkan.buffer.utils;

import com.thatmg393.legacyvkm.vulkan.buffer.base.BaseBuffer;

public class BufferUtils {
    public static void resizeBuffer(BaseBuffer buffer, int newSize) {
        buffer.getType().freeBuffer(buffer);
        buffer.create(newSize);
    }

    public static void resizeIfNeeded(BaseBuffer buffer, int dataSize) {
        if (dataSize > (buffer.getMaxBufferSize() - buffer.getUsedBufferSize())) resizeBuffer(buffer, dataSize);
    }

    public static int alignBuffer(int usedBufferSize, int alignment) {
        return 0;
    }
}
