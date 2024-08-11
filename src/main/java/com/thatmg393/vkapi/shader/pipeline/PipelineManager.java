package com.thatmg393.vkapi.shader.pipeline;

import static org.lwjgl.vulkan.VK10.*;

import java.nio.LongBuffer;
import java.util.LinkedList;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineCacheCreateInfo;

import com.thatmg393.vkapi.gpu.GPUManager;
import com.thatmg393.vkapi.shader.pipeline.base.BasePipeline;
import com.thatmg393.vkapi.utils.ResultChecker;

public class PipelineManager {
    private static final PipelineManager INSTANCE = new PipelineManager();

    public static PipelineManager getInstance() {
        return INSTANCE;
    }

    private final LinkedList<BasePipeline> PIPELINES = new LinkedList<>();
    private final long PIPELINE_CACHE = initializePipelineCache();

    private long initializePipelineCache() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkPipelineCacheCreateInfo vpcci = VkPipelineCacheCreateInfo.calloc(stack);
            vpcci.sType(VK_STRUCTURE_TYPE_PIPELINE_CACHE_CREATE_INFO);

            LongBuffer pipelineCachePtr = stack.mallocLong(1);

            ResultChecker.checkResult(
                vkCreatePipelineCache(GPUManager.getInstance().getSelectedGPU().asLogicalDevice(), vpcci, null, pipelineCachePtr),
                "Failed to create graphics pipeline cache."
            );

            return pipelineCachePtr.get(0);
        }
    }
}
