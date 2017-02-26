package com.elytradev.fruitphone.unit;

import com.elytradev.probe.api.impl.Unit;

import net.minecraft.client.Minecraft;

public class TicksUnit extends Unit {

	public TicksUnit(String name, int barColor) {
		super(name, "t", barColor);
	}

	public TicksUnit(String name) {
		super(name, "t");
	}
	
	@Override
	public String format(double ticks) {
		ticks -= Minecraft.getMinecraft().getRenderPartialTicks();
		if (ticks < 0) ticks = 0;
		int millisrem = (int)((ticks*50D)%1000D);
		long sec = (long)(ticks/20D);
		int secrem = (int)(sec%60L);
		long min = (long)(ticks/1200L);
		
		String secstr = (secrem < 10 ? "0" : "")+secrem;
		String millisstr = (millisrem < 100 ? millisrem < 10 ? "00" : "0" : "")+millisrem;
		
		return min+":"+secstr+"."+millisstr;
	}

}
