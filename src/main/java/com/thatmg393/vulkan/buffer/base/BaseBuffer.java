package com.thatmg393.vulkan.buffer.base;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

import com.thatmg393.vulkan.memory.MemoryType;
import com.thatmg393.vulkan.vma.VMAManager;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseBuffer {
    private long id;
    private long allocation;

    private int maxBufferSize;
    private int usedBufferSize;
    private int offset;

    private MemoryType type;
    private int usage;

    private PointerBuffer data;

    public BaseBuffer(int usage, MemoryType type) {
        this.usage = usage;
        this.type = type;
        this.data = type.isMappable() ? MemoryUtil.memAllocPointer(1) : null;

        setUsedBufferSize(0);
        setOffset(0);
    }

    public void create(int size) {
        type.createBuffer(this, size);
        
        if (type.isMappable()) VMAManager.getInstance().mapBuffer(allocation, data);
    }

    public void free() {
        type.freeBuffer(this);
    }

    public void reset() {
        usedBufferSize = 0;
    }

    public void increaseUsedBufferSize(int size) {
        usedBufferSize += size;
    }

    public BufferInfo getBufferInfo() {
        return new BufferInfo(id, allocation, maxBufferSize);
    }
}
