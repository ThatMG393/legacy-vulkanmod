package com.thatmg393.vulkan.utils;

public class GPUPropertiesUtil {
    public static String vendorIDToString(int vendorID) {
        return switch (vendorID) {
            case (0x10DE) -> "Nvidia";
            case (0x1022) -> "AMD";
            case (0x8086) -> "Intel";
            case (0x13B5) -> "Arm";
            default -> "Unknown Vendor";
        };
    }
}
