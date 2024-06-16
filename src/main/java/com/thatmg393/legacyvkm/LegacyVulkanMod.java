package com.thatmg393.legacyvkm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;

public class LegacyVulkanMod implements ClientModInitializer {
	public static final String MOD_ID = "legacyvkm";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		LOGGER.info("Is this really worth it?");
	}
}
