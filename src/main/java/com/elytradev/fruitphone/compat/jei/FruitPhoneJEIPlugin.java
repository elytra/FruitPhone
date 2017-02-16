package com.elytradev.fruitphone.compat.jei;

import com.elytradev.fruitphone.item.FruitItems;
import com.elytradev.fruitphone.item.ItemFruit;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class FruitPhoneJEIPlugin extends BlankModPlugin {
	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		ISubtypeInterpreter interpreter = new ISubtypeInterpreter() {
			@Override
			public String getSubtypeInfo(ItemStack itemStack) {
				return itemStack.getItem().getRegistryName()+"~"+itemStack.getMetadata()+"~"+((ItemFruit)itemStack.getItem()).getColor(itemStack);
			}
		};
		subtypeRegistry.registerSubtypeInterpreter(FruitItems.HANDHELD, interpreter);
		subtypeRegistry.registerSubtypeInterpreter(FruitItems.PASSIVE, interpreter);
	}
}
