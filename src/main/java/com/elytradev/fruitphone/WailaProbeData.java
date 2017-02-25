package com.elytradev.fruitphone;

import com.elytradev.probe.api.impl.ProbeData;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Internal class for syncing Waila data over the network. Will be unpacked in
 * FruitRenderer's format method. Not designed for external use.
 */
public class WailaProbeData extends ProbeData {
	
	public final NBTTagCompound data;
	
	public WailaProbeData(NBTTagCompound data) {
		this.data = data;
	}
	
}
