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

package mcp.mobius.waila.api;

import java.util.LinkedHashMap;

import net.minecraft.item.ItemStack;

public interface IWailaSummaryProvider {
	/* This interface is used to control the display data in the description screen */
	
	/* BASIC TOOLS & ITEMS DATA */
	//EnumToolMaterial getMaterial(ItemStack stack);
	//String getMaterialName(ItemStack stack);
	//String getEffectiveBlock(ItemStack stack);
	//int getHarvestLevel(ItemStack stack);
	//float getEfficiencyOnProperMaterial(ItemStack stack);
	//int getEnchantability(ItemStack stack);
	//int getDamageVsEntity(ItemStack stack);
	//int getDurability(ItemStack stack);
	
	LinkedHashMap<String, String> getSummary(ItemStack stack, LinkedHashMap<String, String> currentSummary, IWailaConfigHandler config);
}
