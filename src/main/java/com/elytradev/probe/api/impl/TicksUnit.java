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

package com.elytradev.probe.api.impl;

import com.elytradev.fruitphone.proxy.ClientProxy;

public class TicksUnit extends Unit {

	public TicksUnit(String name, int barColor) {
		super(name, "t", barColor);
	}

	public TicksUnit(String name) {
		super(name, "t");
	}
	
	@Override
	public String format(double ticks) {
		if (ticks == Double.POSITIVE_INFINITY) {
			return "∞";
		} else if (ticks == Double.NEGATIVE_INFINITY) {
			return "-∞";
		} else if (Double.isNaN(ticks)) {
			return "NaN";
		}
		
		ticks -= ClientProxy.partialTicks;
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
