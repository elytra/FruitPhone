package com.elytradev.fruitphone;

import io.github.elytra.probe.api.IProbeData;
import io.github.elytra.probe.api.impl.ProbeData;

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
	
	public void setBarLabel(String barLabel) {
		this.barLabel = barLabel;
	}
}
