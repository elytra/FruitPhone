package com.elytradev.fruitphone.vanilla;

import java.util.List;

import com.elytradev.probe.api.IProbeData;

import net.minecraft.tileentity.TileEntity;

public interface VanillaDataProvider<T extends TileEntity> {
	void provideProbeData(T te, List<IProbeData> li);
}