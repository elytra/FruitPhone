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
