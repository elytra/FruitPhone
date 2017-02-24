package com.elytradev.fruitphone;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FruitSounds {

	public static SoundEvent DRILL;
	
	public static void register() {
		DRILL = ev(new ResourceLocation("fruitphone", "drill"));
		GameRegistry.register(DRILL);
	}

	private static SoundEvent ev(ResourceLocation loc) {
		SoundEvent ev = new SoundEvent(loc);
		ev.setRegistryName(loc);
		return ev;
	}

}
