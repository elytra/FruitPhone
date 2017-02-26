package com.elytradev.fruitphone.vanilla;

import java.util.List;

import com.elytradev.concrete.reflect.accessor.Accessor;
import com.elytradev.concrete.reflect.accessor.Accessors;
import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.impl.ProbeData;
import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockJukebox.TileEntityJukebox;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;

public class JukeboxDataProvider implements VanillaDataProvider<TileEntityJukebox> {
	
	private Accessor<String> displayName = Accessors.findField(ItemRecord.class, "field_185077_c", "displayName", "c");
	
	@Override
	public void provideProbeData(TileEntityJukebox te, List<IProbeData> li) {
		ItemStack record = te.getRecord();
		if (record.isEmpty()) {
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
				.withInventory(ImmutableList.of(record))
				.withLabel(new TextComponentTranslation(song)));
	}
}
