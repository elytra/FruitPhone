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

package com.elytradev.fruitphone.vanilla;

import java.util.List;

import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.impl.ProbeData;

import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.text.TextComponentTranslation;

public class BeaconDataProvider implements VanillaDataProvider<TileEntityBeacon> {
	
	@Override
	public void provideProbeData(TileEntityBeacon te, List<IProbeData> li) {
		int levels = te.getField(0);
		int range = (levels*10)+10;
		Potion primary = Potion.getPotionById(te.getField(1));
		Potion secondary = Potion.getPotionById(te.getField(2));
		int amplifier = 0;
		if (levels >= 4) {
			if (primary == secondary) {
				amplifier = 1;
				secondary = null;
			}
		} else {
			secondary = null;
		}
		li.add(new ProbeData()
				.withLabel(new TextComponentTranslation("fruitphone.beacon.range", range)));
		if (primary != null) {
			li.add(new ProbeData()
					.withLabel(new TextComponentTranslation("fruitphone.beacon.primary", new TextComponentTranslation(primary.getName()), new TextComponentTranslation("potion.potency."+amplifier))));
		} else {
			li.add(new ProbeData()
					.withLabel(new TextComponentTranslation("fruitphone.beacon.primary", new TextComponentTranslation("fruitphone.beacon.none"))));
		}
		if (secondary != null) {
			li.add(new ProbeData()
					.withLabel(new TextComponentTranslation("fruitphone.beacon.secondary", new TextComponentTranslation(secondary.getName()), new TextComponentTranslation("potion.potency.0"))));
		} else {
			li.add(new ProbeData()
					.withLabel(new TextComponentTranslation("fruitphone.beacon.secondary", new TextComponentTranslation("fruitphone.beacon.none"))));
		}
	}
}
