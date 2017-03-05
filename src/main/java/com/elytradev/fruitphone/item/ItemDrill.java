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

import com.elytradev.fruitphone.FruitEquipmentProperties;
import com.elytradev.fruitphone.FruitPhone;
import com.elytradev.fruitphone.network.EquipmentDataPacket;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemDrill extends Item {

	public ItemDrill() {
		setTextureName("fruitphone:drill");
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		if (playerIn.isSneaking()) {
			FruitEquipmentProperties props = (FruitEquipmentProperties)playerIn.getExtendedProperties("fruitphone:equipment");
			if (props == null) return itemStackIn;
			ItemStack oldGlasses = props.glasses;
			props.glasses = null;
			if (oldGlasses != null) {
				playerIn.playSound("fruitphone:drill", 0.33f, 0.875f+(itemRand.nextFloat()*0.25f));
				EquipmentDataPacket.forEntity(playerIn).transform((m) -> {m.sendToAllWatching(playerIn); return Void.TYPE;});
				if (!playerIn.inventory.addItemStackToInventory(oldGlasses)) {
					playerIn.entityDropItem(oldGlasses, 0.2f);
				}
			}
		} else {
			FruitPhone.proxy.configureGlasses();
		}
		return itemStackIn;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
		int i = 0;
		while (StatCollector.canTranslate("item.fruitphone.remover.hint."+i)) {
			tooltip.add("\u00A77"+I18n.format("item.fruitphone.remover.hint."+i));
			i++;
		}
	}
	
}
