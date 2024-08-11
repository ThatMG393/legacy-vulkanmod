package com.thatmg393.vkapi.image;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK12.VK_SAMPLER_REDUCTION_MODE_MAX;
import static org.lwjgl.vulkan.VK12.VK_SAMPLER_REDUCTION_MODE_MIN;

import java.nio.LongBuffer;

import org.apache.commons.lang3.Validate;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkSamplerCreateInfo;
import org.lwjgl.vulkan.VkSamplerReductionModeCreateInfo;

import com.thatmg393.vkapi.Vulkan;
import com.thatmg393.vkapi.utils.ResultChecker;

import it.unimi.dsi.fastutil.shorts.Short2LongMap;
import it.unimi.dsi.fastutil.shorts.Short2LongOpenHashMap;
import lombok.Getter;

public class SamplerManager {
    public static final float DEFAULT_MIP_BIAS = -0.5f;

    @Getter
    private static final SamplerManager instance = new SamplerManager();

    public static final byte LINEAR_FILTERING_BIT = 1;
    public static final byte CLAMP_BIT = 2;
    public static final byte USE_MIPMAPS_BIT = 4;
    public static final byte REDUCTION_MIN_BIT = 8;
    public static final byte REDUCTION_MAX_BIT = 16;

    private final Short2LongMap samplers = new Short2LongOpenHashMap();

    public long getOrCreateTextureSampler(byte maxLod, byte flags) {
        long sampler = getTextureSampler(maxLod, flags);
        if (sampler == 0) return createTextureSampler(maxLod, flags);
        return sampler;
    }

    public long getTextureSampler(byte maxLod, byte flags) {
        return getTextureSampler((short) (flags | (maxLod << 8)));
    }

    public long getTextureSampler(short key) {
        return samplers.getOrDefault(key, 0L);
    }

    public long createTextureSampler(byte maxLod, byte flags) {
        return createTextureSampler(maxLod, flags, DEFAULT_MIP_BIAS);
    }

    public long createTextureSampler(byte maxLod, byte flags, float mipBias) {
        Validate.isTrue(
            (flags & (REDUCTION_MIN_BIT | REDUCTION_MAX_BIT)) != (REDUCTION_MIN_BIT | REDUCTION_MAX_BIT)
        );

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkSamplerCreateInfo samplerInfo = VkSamplerCreateInfo.calloc(stack);
            samplerInfo.sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO);

            if ((flags & LINEAR_FILTERING_BIT) != 0) {
                samplerInfo.magFilter(VK_FILTER_LINEAR);
                samplerInfo.minFilter(VK_FILTER_LINEAR);
            } else {
                samplerInfo.magFilter(VK_FILTER_NEAREST);
                samplerInfo.minFilter(VK_FILTER_NEAREST);
            }

            if ((flags & CLAMP_BIT) != 0) {
                samplerInfo.addressModeU(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
                samplerInfo.addressModeV(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
                samplerInfo.addressModeW(VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
            } else {
                samplerInfo.addressModeU(VK_SAMPLER_ADDRESS_MODE_REPEAT);
                samplerInfo.addressModeV(VK_SAMPLER_ADDRESS_MODE_REPEAT);
                samplerInfo.addressModeW(VK_SAMPLER_ADDRESS_MODE_REPEAT);
            }

            samplerInfo.anisotropyEnable(false);
            samplerInfo.borderColor(VK_BORDER_COLOR_INT_OPAQUE_WHITE);
            samplerInfo.unnormalizedCoordinates(false);
            samplerInfo.compareEnable(false);
            samplerInfo.compareOp(VK_COMPARE_OP_ALWAYS);

            if ((flags & USE_MIPMAPS_BIT) != 0) {
                samplerInfo.mipmapMode(VK_SAMPLER_MIPMAP_MODE_LINEAR);
                samplerInfo.maxLod(maxLod);
                samplerInfo.minLod(0.0F);
                samplerInfo.mipLodBias(mipBias);
            } else {
                samplerInfo.mipmapMode(VK_SAMPLER_MIPMAP_MODE_NEAREST);
                samplerInfo.maxLod(0.0F);
                samplerInfo.minLod(0.0F);
            }

            if ((flags & (REDUCTION_MAX_BIT | REDUCTION_MIN_BIT)) != 0) {
                VkSamplerReductionModeCreateInfo reductionModeInfo = VkSamplerReductionModeCreateInfo.calloc(stack);
                reductionModeInfo.sType$Default();
                reductionModeInfo.reductionMode((flags & REDUCTION_MAX_BIT) != 0 ? VK_SAMPLER_REDUCTION_MODE_MAX
                        : VK_SAMPLER_REDUCTION_MODE_MIN);
                samplerInfo.pNext(reductionModeInfo.address());
            }

            LongBuffer textureSampler = stack.mallocLong(1);

            ResultChecker.checkResult(
                vkCreateSampler(
                    Vulkan.getInstance().getCurrentGPU().asLogicalDevice(), samplerInfo, null, textureSampler
                ), "Failed to create texture sampler."
            );

            return textureSampler.get(0);
        }
    }

    public void destroy() {
        samplers.values().forEach(
            e -> vkDestroySampler(
                    Vulkan.getInstance().getCurrentGPU().asLogicalDevice(),
                    e, null)
        );
    }
}
