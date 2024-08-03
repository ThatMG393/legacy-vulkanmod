package com.thatmg393.legacyvkm.utils;

import static com.thatmg393.legacyvkm.LegacyVulkanMod.LOGGER;

import java.nio.file.Files;
import java.nio.file.Paths;

public class SystemInfo {
    public static final String CPU = getSystemCPU();

    private static final String getSystemCPU() {
        try {
            return new oshi.SystemInfo().getHardware().getProcessors()[0].getName();
        } catch (Exception e) {
            LOGGER.info("Failed to get CPU information using OSHI.");

            try {
                return Files.lines(Paths.get("/proc/cpuinfo"))
                    .filter(l -> l.contains("Hardware") || l.contains("model name"))
                    .reduce((f, s) -> f.startsWith("H") ? f : s)
                    .map(l -> {
                        String line = l.split(":")[1].trim();
                        return line;
                    }).orElse("Unknown CPU");
            } catch (Exception e2) {
                LOGGER.info("Failed to get CPU information using /proc/cpuinfo.");
                return "Unknown CPU";
            }
        }
    }
}
