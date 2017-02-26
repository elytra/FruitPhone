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

import java.util.List;

import com.elytradev.concrete.reflect.accessor.Accessor;
import com.elytradev.concrete.reflect.accessor.Accessors;
import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.UnitDictionary;
import com.elytradev.probe.api.impl.ProbeData;

import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.text.TextComponentTranslation;

public class FurnaceDataProvider implements VanillaDataProvider<TileEntityFurnace> {

	private Accessor<Integer> cookTime = Accessors.findField(TileEntityFurnace.class, "field_174906_k", "cookTime", "k");
	private Accessor<Integer> totalCookTime = Accessors.findField(TileEntityFurnace.class, "field_174905_l", "totalCookTime", "l");
	private Accessor<Integer> furnaceBurnTime = Accessors.findField(TileEntityFurnace.class, "field_145956_a", "furnaceBurnTime", "a");
	private Accessor<Integer> currentItemBurnTime = Accessors.findField(TileEntityFurnace.class, "field_145963_i", "currentItemBurnTime", "i");
	
	@Override
	public void provideProbeData(TileEntityFurnace te, List<IProbeData> li) {
		li.add(new ProbeData()
				.withLabel(new TextComponentTranslation("fruitphone.furnace.fuel"))
				.withBar(0, furnaceBurnTime.get(te), currentItemBurnTime.get(te), UnitDictionary.TICKS));
		float curCook = cookTime.get(te);
		float maxCook = totalCookTime.get(te);
		li.add(new ProbeData()
				.withLabel(new TextComponentTranslation("fruitphone.furnace.progress"))
				.withBar(0, maxCook == 0 ? 0 : (curCook/maxCook)*100, 100, UnitDictionary.PERCENT));
	}

}
