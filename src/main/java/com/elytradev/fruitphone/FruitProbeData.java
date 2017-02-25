package com.elytradev.fruitphone;

import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.impl.ProbeData;

/**
 * Implementation detail class for passing data from format to render in
 * FruitRenderer.
 * <p>
 * <b>Using this in a ProbeDataProvider will strip the extra FruitPhone-specific
 * data during network sync.</b> This class is not useful for outside use.
 */
public class FruitProbeData extends ProbeData {
	private String barLabel;
	public FruitProbeData() {
	}
	public FruitProbeData(IProbeData base) {
		if (base.hasBar()) {
			withBar(base.getBarMinimum(), base.getBarCurrent(), base.getBarMaximum(), base.getBarUnit());
		}
		if (base.hasInventory()) {
			withInventory(base.getInventory());
		}
		if (base.hasLabel()) {
			withLabel(base.getLabel());
		}
	}
	
	public String getBarLabel() {
		return barLabel;
	}
	
	public FruitProbeData setBarLabel(String barLabel) {
		this.barLabel = barLabel;
		return this;
	}
}
