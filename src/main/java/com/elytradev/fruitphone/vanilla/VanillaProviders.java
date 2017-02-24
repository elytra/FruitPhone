package com.elytradev.fruitphone.vanilla;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

import java.util.List;
import java.util.Map;

import com.elytradev.probe.api.IProbeData;
import com.google.common.collect.Maps;

@SuppressWarnings({"rawtypes","unchecked"})
public class VanillaProviders {

	public static final VanillaDataProvider EMPTY_PROVIDER = (te, li) -> {};
	
	private static final Map<Class<? extends TileEntity>, VanillaDataProvider> map = Maps.newHashMap();
	
	public static <T extends TileEntity> VanillaDataProvider<T> getProvider(T te) {
		if (te == null) return EMPTY_PROVIDER;
		if (map.containsKey(te.getClass())) {
			return map.get(te.getClass());
		} else {
			return EMPTY_PROVIDER;
		}
	}
	
	public static <T extends TileEntity> void provideProbeData(T te, List<IProbeData> li) {
		getProvider(te).provideProbeData(te, li);
	}
	
	static {
		map.put(TileEntityFurnace.class, new FurnaceDataProvider());
	}
	
}
