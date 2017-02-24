package com.elytradev.fruitphone.vanilla;

import java.util.List;

import com.elytradev.concrete.reflect.accessor.Accessor;
import com.elytradev.concrete.reflect.accessor.Accessors;
import com.elytradev.fruitphone.StagingUnits;
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
				.withBar(0, furnaceBurnTime.get(te), currentItemBurnTime.get(te), StagingUnits.TICKS));
		float curCook = cookTime.get(te);
		float maxCook = totalCookTime.get(te);
		li.add(new ProbeData()
				.withLabel(new TextComponentTranslation("fruitphone.furnace.progress"))
				.withBar(0, maxCook == 0 ? 0 : (curCook/maxCook)*100, 100, UnitDictionary.PERCENT));
	}

}
