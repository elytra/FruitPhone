package io.github.elytra.fruitphone;

import net.minecraftforge.fml.common.registry.GameRegistry;

public class FruitItems {

	public static ItemFruitHandheld HANDHELD;
	public static ItemFruitPassive PASSIVE;
	
	public static void register() {
		HANDHELD = new ItemFruitHandheld();
		HANDHELD.setRegistryName("handheld");
		HANDHELD.setCreativeTab(FruitPhone.tab);
		HANDHELD.setUnlocalizedName("fruitphone.handheld");
		HANDHELD.setMaxStackSize(1);
		HANDHELD.setHasSubtypes(true);
		GameRegistry.register(HANDHELD);
		
		PASSIVE = new ItemFruitPassive();
		PASSIVE.setRegistryName("passive");
		PASSIVE.setCreativeTab(FruitPhone.tab);
		PASSIVE.setUnlocalizedName("fruitphone.passive");
		PASSIVE.setMaxStackSize(1);
		PASSIVE.setHasSubtypes(true);
		GameRegistry.register(PASSIVE);
	}

}
