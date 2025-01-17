package com.thatmg393.vulkan.utils;

import java.util.Collection;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

public class PointerBufferUtils {
    public static PointerBuffer asPointerBuffer(Collection<String> collection) {
        MemoryStack stack = MemoryStack.stackGet();
        PointerBuffer buffer = stack.mallocPointer(collection.size());
        collection.stream().map(stack::UTF8).forEach(buffer::put);
        return buffer.rewind();
    }

}