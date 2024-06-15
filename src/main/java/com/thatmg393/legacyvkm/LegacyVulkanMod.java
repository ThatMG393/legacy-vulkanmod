package com.thatmg393.legacyvkm;

import com.thatmg393.legacyvkm.vulkan.Vulkan;

import net.fabricmc.api.ClientModInitializer;

public class LegacyVulkanMod implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		System.out.println("Is this really worth it?");
		Vulkan.getInstance().initialize();
	}
}
