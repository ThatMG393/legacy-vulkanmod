package com.thatmg393.vkapi.utils;

public class ResultChecker {
    public static void checkResult(int code, CharSequence failMessage) {
        if (code != VkResult.VK_SUCCESS) {
            throw new RuntimeException(
                "Fatal error: " + code + " (" + codeToString(code) + ") " + failMessage
            );
        }
    }

    public static String codeToString(int code) {
        return VkResult.decode(code);
    }
}
