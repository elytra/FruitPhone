package io.github.elytra.fruitphone.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemFruitHandheld extends ItemFruit {
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.fruitphone.handheld"+(stack.getMetadata() == 1 ? "_mini" : "");
	}
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		addSubItems(itemIn, 0, subItems);
		addSubItems(itemIn, 1, subItems);
	}
	
}
