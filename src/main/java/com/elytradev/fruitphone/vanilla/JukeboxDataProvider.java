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

import java.util.Collections;
import java.util.List;

import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.impl.ProbeData;
import io.github.elytra.concrete.accessor.Accessor;
import io.github.elytra.concrete.accessor.Accessors;
import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

public class JukeboxDataProvider implements VanillaDataProvider<TileEntityJukebox> {
	
	private Accessor<String> displayName = Accessors.findField(ItemRecord.class, "field_185077_c", "displayName", "c");
	
	@Override
	public void provideProbeData(TileEntityJukebox te, List<IProbeData> li) {
		ItemStack record = te.getRecord();
		if (record == null) {
			li.add(new ProbeData()
					.withLabel(new TextComponentTranslation("fruitphone.jukebox.noRecord")));
			return;
		}
		String song = "fruitphone.jukebox.noRecord";
		if (record.getItem() instanceof ItemRecord) {
			try {
				song = displayName.get(record.getItem());
			} catch (Throwable t) {
				song = "Darude - Sandstorm";
			}
		} else {
			song = record.getUnlocalizedName();
		}
		li.add(new ProbeData()
				.withInventory(Collections.singletonList(record))
				.withLabel(new TextComponentTranslation(song)));
	}
}
