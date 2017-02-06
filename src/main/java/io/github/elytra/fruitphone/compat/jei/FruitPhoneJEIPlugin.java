package io.github.elytra.fruitphone.compat.jei;

import io.github.elytra.fruitphone.FruitItems;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;

@JEIPlugin
public class FruitPhoneJEIPlugin extends BlankModPlugin {
	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(FruitItems.HANDHELD, FruitItems.PASSIVE);
	}
}
