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
