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

import com.elytradev.fruitphone.FruitPhone;

import cpw.mods.fml.common.registry.GameRegistry;

public class FruitItems {

	public static ItemFruitHandheld HANDHELD;
	public static ItemFruitPassive PASSIVE;
	public static ItemDrill REMOVER;
	
	public static void register() {
		HANDHELD = new ItemFruitHandheld();
		HANDHELD.setCreativeTab(FruitPhone.tab);
		HANDHELD.setUnlocalizedName("fruitphone.handheld");
		HANDHELD.setMaxStackSize(1);
		HANDHELD.setHasSubtypes(true);
		GameRegistry.registerItem(HANDHELD, "handheld");
		
		PASSIVE = new ItemFruitPassive();
		PASSIVE.setCreativeTab(FruitPhone.tab);
		PASSIVE.setUnlocalizedName("fruitphone.passive");
		PASSIVE.setMaxStackSize(1);
		PASSIVE.setHasSubtypes(true);
		GameRegistry.registerItem(PASSIVE, "passive");
		
		REMOVER = new ItemDrill();
		REMOVER.setCreativeTab(FruitPhone.tab);
		REMOVER.setUnlocalizedName("fruitphone.remover");
		REMOVER.setMaxStackSize(1);
		GameRegistry.registerItem(REMOVER, "remover");
	}

}
