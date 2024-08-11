package com.thatmg393.vkapi.buffer.base;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

import com.thatmg393.vkapi.memory.MemoryType;
import com.thatmg393.vkapi.vma.VMAManager;

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

    public long getId() {
        return id;
    }

    public long getAllocation() {
        return allocation;
    }

    public int getMaxBufferSize() {
        return maxBufferSize;
    }

    public int getUsedBufferSize() {
        return usedBufferSize;
    }

    public int getOffset() {
        return offset;
    }

    public MemoryType getType() {
        return type;
    }

    public int getUsage() {
        return usage;
    }

    public PointerBuffer getData() {
        return data;
    }

    public void setId(long newId) {
        id = newId;
    }

    public void setAllocation(long newAllocation) {
        allocation = newAllocation;
    }

    public void setMaxBufferSize(int newSize) {
        maxBufferSize = newSize;
    }

    public void setUsedBufferSize(int newSize) {
        usedBufferSize = newSize;
    }

    public void increaseUsedBufferSize(int size) {
        usedBufferSize += size;
    }

    public void setOffset(int newOffset) {
        offset = newOffset;
    }

    public void setUsage(int newUsage) {
        usage = newUsage;
    }

    public BufferInfo getBufferInfo() {
        return new BufferInfo(id, allocation, maxBufferSize);
    }
    
    public record BufferInfo(long id, long allocation, long bufferSize) { }
}
