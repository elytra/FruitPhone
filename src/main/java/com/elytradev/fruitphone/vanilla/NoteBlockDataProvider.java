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

import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.impl.ProbeData;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntityNote;
import net.minecraft.util.text.TextComponentTranslation;

public class NoteBlockDataProvider implements VanillaDataProvider<TileEntityNote> {
	
	@Override
	public void provideProbeData(TileEntityNote te, List<IProbeData> li) {
		li.add(new ProbeData()
				.withLabel(new TextComponentTranslation("fruitphone.note", new TextComponentTranslation("fruitphone.note."+te.note))));
		IBlockState below = te.getWorld().getBlockState(te.getPos().down());
		Material m = below.getMaterial();
		String instrument = "piano";
		if (m == Material.ROCK) {
			instrument = "drum";
		}
		if (m == Material.SAND) {
			instrument = "snare";
		}
		if (m == Material.GLASS) {
			instrument = "click";
		}
		if (m == Material.WOOD) {
			instrument = "guitar";
		}
		
		String s = "fruitphone.note.instrument."+instrument;
		li.add(new ProbeData()
				.withLabel(new TextComponentTranslation("fruitphone.note.instrument", new TextComponentTranslation(s))));
	}
}
