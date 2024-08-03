package com.thatmg393.legacyvkm.config;

import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.glfw.GLFW;

import lombok.Getter;

import static com.thatmg393.legacyvkm.LegacyVulkanMod.LOGGER;

public class Platform {
    @Getter
    private static final int currentPlatform = getPlatform();

    public static void initialize() {
        GLFW.glfwInitHint(GLFW.GLFW_PLATFORM, currentPlatform);
        LOGGER.info("Current Platform: " + getPlatformAsString());
        LOGGER.info("GLFW" + GLFW.glfwGetVersionString());
        GLFW.glfwInit();
    }

    private static int getPlatform() {
        return switch (SystemUtils.OS_NAME) {
            case "Windows" -> GLFW.GLFW_PLATFORM_WIN32;
            case "Mac OS X" -> GLFW.GLFW_PLATFORM_COCOA;
            case "Linux" -> getPlatformLinux();
            default -> GLFW.GLFW_ANY_PLATFORM;
        };
    }

    private static int getPlatformLinux() {
        return switch (System.getenv("XDG_SESSION_TYPE")) {
            case "wayland" -> GLFW.GLFW_PLATFORM_WAYLAND;
            case "x11" -> GLFW.GLFW_PLATFORM_X11;
            default -> GLFW.GLFW_ANY_PLATFORM;
        };
    }

    private static String getPlatformAsString() {
        return switch (currentPlatform) {
            case GLFW.GLFW_PLATFORM_WIN32 -> "WIN32";
            case GLFW.GLFW_PLATFORM_WAYLAND -> "WAYLAND";
            case GLFW.GLFW_PLATFORM_X11 -> "X11";
            case GLFW.GLFW_PLATFORM_COCOA -> "MACOS";
            case GLFW.GLFW_ANY_PLATFORM -> "ANDROID";
            default -> "UNKNOWN";
        };
    }
}
