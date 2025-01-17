package com.thatmg393.legacyvkm.mixins.startup;

import java.nio.ByteBuffer;
import org.apache.commons.lang3.Validate;
import org.lwjgl.LWJGLException;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Display.class)
public class DisplayM {
    @Shadow(remap = false) private static boolean resizable;
    @Shadow(remap = false) private static DisplayMode displayMode;
    @Shadow(remap = false) private static GLFWWindowSizeCallback sizeCallback;
    @Shadow(remap = false) private static ByteBuffer[] cached_icons;
    @Shadow(remap = false) private static boolean window_resized;

    /** Worst case scenario, manually forking legacy-lwjgl3 :skull:
     * @reason vulkan
     * @author ThatMG393
     */
    @Overwrite(remap = false)
    public static void create(PixelFormat pixelFormat) {
        GLFWErrorCallback.createPrint(System.err).set();
        Validate.isTrue(GLFW.glfwInit());

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);

        if (resizable) GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        else GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);

        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API);
        Display.setHandle(
            GLFW.glfwCreateWindow(
                Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight(), 
                Display.getTitle(), MemoryUtil.NULL, MemoryUtil.NULL));
        Display.setWidth(Display.getDisplayMode().getWidth());
        Display.setHeight(Display.getDisplayMode().getHeight());

        sizeCallback = GLFWWindowSizeCallback.create((window, width, height) -> {
            if (window != Display.getHandle()) return;
            window_resized = true;
            Display.setWidth(width);
            Display.setHeight(height);
        });
        GLFW.glfwSetWindowSizeCallback(Display.getHandle(), sizeCallback);
        try {
            Mouse.create();
            Keyboard.create();
        } catch (LWJGLException e) { }

        GLFW.glfwShowWindow(Display.getHandle());
        if (cached_icons == null) {
            Display.setIcon(cached_icons);
        }
    }
}
