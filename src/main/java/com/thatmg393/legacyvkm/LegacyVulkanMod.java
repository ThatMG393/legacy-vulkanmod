package com.thatmg393.legacyvkm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class LegacyVulkanMod implements ClientModInitializer {
	public static final String MOD_ID = "legacyvkm";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
	public static final String VERSION = FabricLoader.getInstance()
										.getModContainer(MOD_ID)
										.map(mod -> mod.getMetadata().getVersion().getFriendlyString())
										.orElse("0.0.0");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Is this really worth it?");
	}
}
