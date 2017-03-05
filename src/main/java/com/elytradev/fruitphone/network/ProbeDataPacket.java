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

import java.util.List;

import com.elytradev.fruitphone.FruitPhone;
import com.elytradev.fruitphone.FruitRenderer;
import com.elytradev.fruitphone.WailaProbeData;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.Message;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.NetworkContext;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.annotation.field.MarshalledAs;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.annotation.type.ReceivedOn;
import com.elytradev.probe.api.IProbeData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ReceivedOn(Side.CLIENT)
public class ProbeDataPacket extends Message {

	@MarshalledAs(ProbeDataListMarshaller.NAME)
	private List<IProbeData> data;
	@MarshalledAs("i32")
	private int x;
	@MarshalledAs("u8")
	private int y;
	@MarshalledAs("i32")
	private int z;
	private NBTTagCompound wailaData;
	
	public ProbeDataPacket(NetworkContext ctx) {
		super(ctx);
	}
	
	public ProbeDataPacket(int x, int y, int z, List<IProbeData> data, NBTTagCompound wailaData) {
		super(FruitPhone.inst.NETWORK);
		this.x = x;
		this.y = y;
		this.z = z;
		this.data = data;
		this.wailaData = wailaData;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer sender) {
		if (wailaData.getKeySet().size() > 0) {
			data.add(0, new WailaProbeData(wailaData));
		}
		FruitRenderer.hasData = true;
		FruitRenderer.currentDataPosX = x;
		FruitRenderer.currentDataPosY = y;
		FruitRenderer.currentDataPosZ = z;
		FruitRenderer.currentRawData = data;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ProbeDataPacket other = (ProbeDataPacket) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		if (x != other.x) return false;
		if (y != other.y) return false;
		if (z != other.z) return false;
		return true;
	}
	
	
	
	
	
}
