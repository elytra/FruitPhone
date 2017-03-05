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

package com.elytradev.probe.api.impl;

import java.text.NumberFormat;

/**
 * A Unit that uses SI prefixes to format numbers, turning things like "1200"
 * into "1.2k".
 */
public class SIUnit extends Unit {
	
	public SIUnit(String name, String abbreviation, int barColor, NumberFormat nfmt, boolean spaceAfterNumber) {
		super(name, abbreviation, barColor, nfmt, spaceAfterNumber);
	}

	public SIUnit(String name, String abbreviation, int barColor) {
		super(name, abbreviation, barColor);
	}

	public SIUnit(String name, String abbreviation) {
		super(name, abbreviation);
	}

	private static final double HELLA = 1_000_000_000_000_000_000_000_000_000D;
	private static final double YOTTA = 1_000_000_000_000_000_000_000_000D;
	private static final double ZETTA = 1_000_000_000_000_000_000_000D;
	private static final double EXA   = 1_000_000_000_000_000_000D;
	private static final double PETA  = 1_000_000_000_000_000D;
	private static final double TERA  = 1_000_000_000_000D;
	private static final double GIGA  = 1_000_000_000D;
	private static final double MEGA  = 1_000_000D;
	private static final double KILO  = 1_000D;
	private static final double MILLI = 1/1_000D;
	private static final double MICRO = 1/1_000_000D;
	private static final double NANO  = 1/1_000_000_000D;
	private static final double PICO  = 1/1_000_000_000_000D;
	
	@Override
	public String format(double d) {
		String space = (spaceAfterNumber) ? " " : "";
		
		if (d == 0) {
			return format.format(d)+space+getAbbreviation();
		} else if (d == Double.POSITIVE_INFINITY) {
			return "∞"+space+getAbbreviation();
		} else if (d == Double.NEGATIVE_INFINITY) {
			return "-∞"+space+getAbbreviation();
		} else if (Double.isNaN(d)) {
			return "NaN"+space+getAbbreviation();
		}
		
		double magnitude = Math.abs(d);
		
		if (magnitude>HELLA) {
			return format.format(d/HELLA)+space+"X"+getAbbreviation();
		} else if (magnitude>YOTTA) {
			return format.format(d/YOTTA)+space+"Y"+getAbbreviation();
		} else if (magnitude>ZETTA) {
			return format.format(d/ZETTA)+space+"Z"+getAbbreviation();
		} else if (magnitude>EXA) {
			return format.format(d/EXA)+space+"E"+getAbbreviation();
		} else if (magnitude>PETA) {
			return format.format(d/PETA)+space+"P"+getAbbreviation();
		} else if (magnitude>TERA) {
			return format.format(d/TERA)+space+"T"+getAbbreviation();
		} else if (magnitude>GIGA) {
			return format.format(d/GIGA)+space+"G"+getAbbreviation();
		} else if (magnitude>MEGA) {
			return format.format(d/MEGA)+space+"M"+getAbbreviation();
		} else if (magnitude>KILO) {
			return format.format(d/KILO)+space+"k"+getAbbreviation();
			
		//if we ever added femto/atto/zepto/yocto they'd go here
		//dividing by the reciprocal down there should totally work. It's not the most efficient way, but it's consistent.
			
		} else if (magnitude<NANO) {
			return format.format(d/PICO)+space+"p"+getAbbreviation();
		} else if (magnitude<MICRO) {
			return format.format(d/NANO)+space+"n"+getAbbreviation();
		} else if (magnitude<MILLI) {
			return format.format(d/MICRO)+space+"µ"+getAbbreviation();
		} else if (magnitude<1.0) {
			return format.format(d/MILLI)+space+"m"+getAbbreviation();
		} else {
			return format.format(d)+space+getAbbreviation();
		}
	}
	
}
