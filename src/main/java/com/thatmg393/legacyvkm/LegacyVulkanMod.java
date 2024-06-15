package com.thatmg393.legacyvkm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thatmg393.legacyvkm.vulkan.Vulkan;

import net.fabricmc.api.ModInitializer;

public class LegacyVulkanMod implements ModInitializer {
	public static final String MOD_ID = "legacyvkm";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Is this really worth it?");
		// Vulkan.getInstance().initialize();
	}
}
