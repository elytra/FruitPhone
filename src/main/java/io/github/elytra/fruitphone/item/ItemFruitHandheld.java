package io.github.elytra.fruitphone.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemFruitHandheld extends ItemFruit {
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.fruitphone.handheld"+(stack.getMetadata() == 1 ? "_mini" : "");
	}
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		addSubItems(itemIn, 0, subItems);
		addSubItems(itemIn, 1, subItems);
	}
	
}
