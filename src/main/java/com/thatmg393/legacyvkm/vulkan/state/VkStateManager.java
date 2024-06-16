package com.thatmg393.legacyvkm.vulkan.state;

public abstract class VkStateManager {
    private static boolean depthTest = true;

    public static void enableDepthTest() {
        depthTest = true;
    }

    public static void disableDepthTest() {
        depthTest = false;
    }

    public static boolean getDepthTestState() {
        return depthTest;
    }
}
