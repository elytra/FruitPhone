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

package com.elytradev.fruitphone.item;

import com.elytradev.fruitphone.FruitPhone;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FruitItems {

	public static ItemFruitHandheld HANDHELD;
	public static ItemFruitPassive PASSIVE;
	public static ItemDrill REMOVER;

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> registryEvent) {
		HANDHELD = new ItemFruitHandheld();
		HANDHELD.setRegistryName("handheld");
		HANDHELD.setCreativeTab(FruitPhone.tab);
		HANDHELD.setUnlocalizedName("fruitphone.handheld");
		HANDHELD.setMaxStackSize(1);
		HANDHELD.setHasSubtypes(true);
		registryEvent.getRegistry().register(HANDHELD);

		PASSIVE = new ItemFruitPassive();
		PASSIVE.setRegistryName("passive");
		PASSIVE.setCreativeTab(FruitPhone.tab);
		PASSIVE.setUnlocalizedName("fruitphone.passive");
		PASSIVE.setMaxStackSize(1);
		PASSIVE.setHasSubtypes(true);
		registryEvent.getRegistry().register(PASSIVE);

		REMOVER = new ItemDrill();
		REMOVER.setRegistryName("remover");
		REMOVER.setCreativeTab(FruitPhone.tab);
		REMOVER.setUnlocalizedName("fruitphone.remover");
		REMOVER.setMaxStackSize(1);
		registryEvent.getRegistry().register(REMOVER);
	}

}
