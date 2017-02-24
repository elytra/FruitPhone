package com.elytradev.fruitphone;

import com.elytradev.fruitphone.unit.TicksUnit;
import com.elytradev.probe.api.UnitDictionary;

/**
 * Experimental units not yet promoted to ProbeDataProviderAPI.
 */
public class StagingUnits {
	public static TicksUnit TICKS = new TicksUnit("fruitphone:ticks");
	
	static {
		UnitDictionary.getInstance().register(TICKS);
	}
}
