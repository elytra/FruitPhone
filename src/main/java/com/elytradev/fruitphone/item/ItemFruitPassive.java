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

import com.elytradev.fruitphone.FruitEquipmentProperties;
import com.elytradev.fruitphone.network.EquipmentDataPacket;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemFruitPassive extends ItemFruit {

	private IIcon icon;
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
		FruitEquipmentProperties props = (FruitEquipmentProperties)playerIn.getExtendedProperties("fruitphone:equipment");
		if (props == null) return itemStackIn;
		ItemStack oldGlasses = props.glasses;
		props.glasses = itemStackIn;
		EquipmentDataPacket.forEntity(playerIn).transform((m) -> {m.sendToAllWatching(playerIn); return Void.TYPE;});
		if (oldGlasses == null) {
			oldGlasses = itemStackIn.copy();
			oldGlasses.stackSize = 0;
		}
		return oldGlasses;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
		int i = 0;
		while (StatCollector.canTranslate("item.fruitphone.passive.hint."+i)) {
			tooltip.add("\u00A77"+I18n.format("item.fruitphone.passive.hint."+i));
			i++;
		}
		super.addInformation(stack, playerIn, tooltip, advanced);
	}
	
	@Override
	public IIcon getIconFromDamage(int p_77617_1_) {
		return icon;
	}
	
	@Override
	public void registerIcons(IIconRegister register) {
		icon = register.registerIcon("fruitphone:passive");
	}
	
}
