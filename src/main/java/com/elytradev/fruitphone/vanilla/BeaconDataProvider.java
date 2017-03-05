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
import net.minecraft.util.ChatComponentTranslation;

public class BeaconDataProvider implements VanillaDataProvider<TileEntityBeacon> {
	
	@Override
	public void provideProbeData(TileEntityBeacon te, List<IProbeData> li) {
		int levels = te.getLevels();
		int range = (levels*10)+10;
		Potion primary = Potion.potionTypes[te.getPrimaryEffect()];
		Potion secondary = Potion.potionTypes[te.getSecondaryEffect()];
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
				.withLabel(new ChatComponentTranslation("fruitphone.beacon.range", range)));
		if (primary != null) {
			li.add(new ProbeData()
					.withLabel(new ChatComponentTranslation("fruitphone.beacon.primary", new ChatComponentTranslation(primary.getName()), new ChatComponentTranslation("potion.potency."+amplifier))));
		} else {
			li.add(new ProbeData()
					.withLabel(new ChatComponentTranslation("fruitphone.beacon.primary", new ChatComponentTranslation("fruitphone.beacon.none"))));
		}
		if (secondary != null) {
			li.add(new ProbeData()
					.withLabel(new ChatComponentTranslation("fruitphone.beacon.secondary", new ChatComponentTranslation(secondary.getName()), new ChatComponentTranslation("potion.potency.0"))));
		} else {
			li.add(new ProbeData()
					.withLabel(new ChatComponentTranslation("fruitphone.beacon.secondary", new ChatComponentTranslation("fruitphone.beacon.none"))));
		}
	}
}
