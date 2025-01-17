package com.thatmg393.vulkan.utils;

public class ResultChecker {
    public static void checkResult(int code, CharSequence failMessage) {
        if (code != VkResult.VK_SUCCESS) {
            throw new RuntimeException(
                "Fatal error: " + code + " (" + VkResult.decode(code) + ") " + failMessage
            );
        }
    }
}
