package com.elytradev.fruitphone.item;

import java.util.List;
import java.util.Locale;

import com.elytradev.fruitphone.recipe.FruitRecipes;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants.NBT;

public class ItemFruit extends Item {

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("fruitphone:color", NBT.TAG_INT)) {
			String colorHex = Integer.toHexString(getColor(stack)).substring(2).toUpperCase(Locale.ROOT);
			String colorString;
			if (I18n.hasKey("fruitphone.color."+colorHex)) {
				colorString = I18n.format("fruitphone.color."+colorHex);
				if (advanced) {
					colorString = colorString+" \u00A78(#"+colorHex+")";
				}
			} else {
				colorString = "#"+colorHex;
			}
			tooltip.add(I18n.format("tooltip.fruitphone.color", colorString));
		}
	}

	public int getColor(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("fruitphone:color", NBT.TAG_INT)) {
			return stack.getTagCompound().getInteger("fruitphone:color") | 0xFF000000;
		}
		return 0xFFE1E1E1;
	}
	
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		addSubItems(itemIn, 0, subItems);
	}
	
	protected void addSubItems(Item itemIn, int meta, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, meta));
		for (Integer color : FruitRecipes.craftableColors) {
			ItemStack is = new ItemStack(itemIn, 1, meta);
			is.setTagCompound(new NBTTagCompound());
			is.getTagCompound().setInteger("fruitphone:color", color);
			subItems.add(is);
		}
	}
	
}
