package com.thatmg393.legacyvkm.vulkan.shader.pipeline.utils;

import java.nio.LongBuffer;
import java.util.List;

import org.lwjgl.system.MemoryStack;

import com.thatmg393.legacyvkm.vulkan.shader.descriptor.UBO;

public class PipelineUtils {
    public static long createDescriptorSetsLayout(List<UBO> ubos, int bindingsSize) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer resultPtr = stack.mallocLong(0);

            return resultPtr.get(0);
        }
    }

    public static long createPipelineLayout() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            LongBuffer resultPtr = stack.mallocLong(0);

            return resultPtr.get(0);
        }
    }
}
