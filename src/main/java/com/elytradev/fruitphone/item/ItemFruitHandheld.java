/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 Aesen 'unascribed' Vismea
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

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemFruitHandheld extends ItemFruit {
	
	private IIcon phone;
	private IIcon pad;
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.fruitphone.handheld"+(stack.getMetadata() == 1 ? "_mini" : "");
	}
	
	@Override
	public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
		addSubItems(itemIn, 0, subItems);
		addSubItems(itemIn, 1, subItems);
	}
	
	@Override
	public IIcon getIconFromDamage(int dmg) {
		return dmg == 1 ? phone : pad;
	}
	
	@Override
	public void registerIcons(IIconRegister register) {
		phone = register.registerIcon("fruitphone:handheld_mini");
		pad = register.registerIcon("fruitphone:handheld");
	}
	
}
