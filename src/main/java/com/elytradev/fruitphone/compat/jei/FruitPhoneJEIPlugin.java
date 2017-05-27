/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 William Thompson (unascribed)
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

package com.elytradev.fruitphone.compat.jei;

import com.elytradev.fruitphone.item.FruitItems;
import com.elytradev.fruitphone.item.ItemFruit;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class FruitPhoneJEIPlugin extends BlankModPlugin {
	@Override
	public void register(IModRegistry registry) {
		ISubtypeRegistry subtypeRegistry = registry.getJeiHelpers().getSubtypeRegistry();
		
		ISubtypeInterpreter interpreter = new ISubtypeInterpreter() {
			@Override
			public String getSubtypeInfo(ItemStack itemStack) {
				return itemStack.getItem().getRegistryName()+"~"+itemStack.getMetadata()+"~"+((ItemFruit)itemStack.getItem()).getColor(itemStack);
			}
		};
		subtypeRegistry.registerNbtInterpreter(FruitItems.HANDHELD, interpreter);
		subtypeRegistry.registerNbtInterpreter(FruitItems.PASSIVE, interpreter);
	}
}
