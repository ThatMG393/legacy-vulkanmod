package com.thatmg393.legacyvkm.mixins.render.gui.debug;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.Sys;
import org.slf4j.helpers.MessageFormatter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.thatmg393.legacyvkm.LegacyVulkanMod;
import com.thatmg393.vulkan.gpu.GPU;
import com.thatmg393.vulkan.gpu.GPUManager;
import com.thatmg393.vulkan.memory.MemoryType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

@Mixin(DebugHud.class)
public class DebugHudM {
    @Shadow @Final private MinecraftClient client;

    /** Converts bytes to MiB using bitwise operations
     * @param bytes
     * @return bytes in MiB
     * 
     * @reason Division begone!
     * @author ThatMG393
     */
    @Overwrite
    private static long toMiB(long bytes) {
        return bytes >> 20;
    }

    @Redirect(method = "renderRightText", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/DebugHud;getRightText()Ljava/util/List"))
    protected List<String> getRightText() {
        ArrayList<String> rt = new ArrayList<>();

        rt.add(fastFormat("{0} Java {1} {2}-bit", System.getProperty("java.vm.vendor"), System.getProperty("java.version"), System.getProperty("sun.arch.data.model")));
        rt.add(fastFormat("Memory: {0}MB/{1}MB", toMiB(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()), toMiB(Runtime.getRuntime().maxMemory())));
        
        rt.add("");

        rt.add("VulkanMod v" + LegacyVulkanMod.VERSION);
        rt.add("LWJGL Version: " + Sys.VERSION);

        GPU gpu = GPUManager.getInstance().getSelectedGPU();
        rt.add("-- GPU INFO --");
        rt.add("Name: : " + gpu.name);
        rt.add("Vendor: " + gpu.vendorName);
        rt.add("Vulkan Version: " + gpu.apiVersion);

        rt.add("");
        rt.add(fastFormat("GPU Memory: {0}MB/{1}MB", MemoryType.GPU_MEM.getUsedMemory(), MemoryType.GPU_MEM.getMaxMemory()));
        rt.add(fastFormat("BAR Memory: {0}MB/{1}MB", MemoryType.BAR_MEM.getUsedMemory(), MemoryType.BAR_MEM.getMaxMemory()));

        rt.add("");
        
        BlockPos bp = client.result.getBlockPos();
        BlockState bs = client.world.getBlockState(bp);

        rt.add(Block.REGISTRY.getIdentifier(bs.getBlock()).toString());
        bs.getPropertyMap().forEach((p, c) -> {
            rt.add(p.getName() + ": " + ((c == Boolean.TRUE) ? Formatting.GREEN + c.toString() : Formatting.RED + c.toString()));
        });

        return rt;
    }

    private final String fastFormat(String format, Object... args) {
        return MessageFormatter.arrayFormat(format, args).getMessage();
    }
}
