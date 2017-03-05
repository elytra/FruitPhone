/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 Una Thompson (unascribed)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.Constants.NBT;

public abstract class ItemFruit extends Item {

	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("fruitphone:color", NBT.TAG_INT)) {
			String colorHex = Integer.toHexString(getColor(stack)).substring(2).toUpperCase(Locale.ROOT);
			String colorString;
			if (StatCollector.canTranslate("fruitphone.color."+colorHex)) {
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
	
	@Override
	public int getColorFromItemStack(ItemStack p_82790_1_, int p_82790_2_) {
		return getColor(p_82790_1_);
	}

	public int getColor(ItemStack stack) {
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey("fruitphone:color", NBT.TAG_INT)) {
			return stack.getTagCompound().getInteger("fruitphone:color") | 0xFF000000;
		}
		return 0xFFE1E1E1;
	}
	
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
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
