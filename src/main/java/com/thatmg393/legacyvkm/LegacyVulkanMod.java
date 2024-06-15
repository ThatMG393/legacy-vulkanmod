package com.thatmg393.legacyvkm;

import com.thatmg393.legacyvkm.vulkan.Vulkan;

import net.fabricmc.api.ModInitializer;

public class LegacyVulkanMod implements ModInitializer {

	@Override
	public void onInitialize() {
		System.out.println("Is this really worth it?");
		Vulkan.getInstance().initialize();
	}
}
