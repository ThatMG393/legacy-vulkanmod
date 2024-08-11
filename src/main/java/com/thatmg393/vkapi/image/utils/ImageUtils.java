package com.thatmg393.vkapi.image.utils;

import static org.lwjgl.vulkan.VK10.*;

public class ImageUtils {
    public static int getAspect(int format) {
        return switch (format) {
            case VK_FORMAT_D24_UNORM_S8_UINT, VK_FORMAT_D32_SFLOAT_S8_UINT ->
                    VK_IMAGE_ASPECT_DEPTH_BIT | VK_IMAGE_ASPECT_STENCIL_BIT;
            case VK_FORMAT_X8_D24_UNORM_PACK32, VK_FORMAT_D32_SFLOAT,
                 VK_FORMAT_D16_UNORM -> VK_IMAGE_ASPECT_DEPTH_BIT;
            default -> VK_IMAGE_ASPECT_COLOR_BIT;
        };
    }
}
