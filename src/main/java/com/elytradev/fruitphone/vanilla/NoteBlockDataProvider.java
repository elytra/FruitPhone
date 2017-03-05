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

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.ChatComponentTranslation;

public class NoteBlockDataProvider implements VanillaDataProvider<TileEntityNote> {
	
	@Override
	public void provideProbeData(TileEntityNote te, List<IProbeData> li) {
		li.add(new ProbeData()
				.withLabel(new ChatComponentTranslation("fruitphone.note", new ChatComponentTranslation("fruitphone.note."+te.note))));
		Block below = te.getWorld().getBlock(te.xCoord, te.yCoord-1, te.zCoord);
		Material m = below.getMaterial();
		String instrument = "piano";
		if (m == Material.rock) {
			instrument = "drum";
		}
		if (m == Material.sand) {
			instrument = "snare";
		}
		if (m == Material.glass) {
			instrument = "click";
		}
		if (m == Material.wood) {
			instrument = "guitar";
		}
		
		String s = "fruitphone.note.instrument."+instrument;
		li.add(new ProbeData()
				.withLabel(new ChatComponentTranslation("fruitphone.note.instrument", new ChatComponentTranslation(s))));
	}
}
