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

package com.elytradev.fruitphone.network;

import com.elytradev.fruitphone.FruitEquipmentProperties;
import com.elytradev.fruitphone.FruitPhone;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.Message;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.NetworkContext;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.annotation.field.MarshalledAs;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.annotation.type.ReceivedOn;
import com.google.common.base.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ReceivedOn(Side.CLIENT)
public class EquipmentDataPacket extends Message {

	@MarshalledAs("i32")
	public int entityId;
	public NBTTagCompound tag;
	
	public EquipmentDataPacket(NetworkContext ctx) {
		super(ctx);
	}
	
	public EquipmentDataPacket(int entityId, NBTTagCompound tag) {
		super(FruitPhone.inst.NETWORK);
		this.entityId = entityId;
		this.tag = tag;
	}
	

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer sender) {
		Entity entity = sender.worldObj.getEntityByID(entityId);
		FruitEquipmentProperties props = (FruitEquipmentProperties)entity.getExtendedProperties("fruitphone:equipment");
		props.loadNBTData(tag);
	}
	
	public static Optional<EquipmentDataPacket> forEntity(Entity e) {
		FruitEquipmentProperties props = (FruitEquipmentProperties)e.getExtendedProperties("fruitphone:equipment");
		if (props == null) return Optional.absent();
		NBTTagCompound tag = new NBTTagCompound();
		props.saveNBTData(tag);
		return Optional.of(new EquipmentDataPacket(e.getEntityId(), tag));
	}

}
