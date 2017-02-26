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
