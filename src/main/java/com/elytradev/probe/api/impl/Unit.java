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

import com.elytradev.probe.api.IUnit;

public class Unit implements IUnit {
	public static final NumberFormat FORMAT_STANDARD = NumberFormat.getNumberInstance();
	static {
		FORMAT_STANDARD.setMinimumFractionDigits(0);
		FORMAT_STANDARD.setMaximumFractionDigits(2);
	}
	
	
	private final String name;
	private final String abbreviation;
	private final int barColor;
	protected final NumberFormat format;
	protected final boolean spaceAfterNumber;
	
	public Unit(String name, String abbreviation) {
		this(name, abbreviation, 0xAAAAAA, FORMAT_STANDARD, true);
	}
	
	public Unit(String name, String abbreviation, int barColor) {
		this(name, abbreviation, barColor, FORMAT_STANDARD, true);
	}
	
	public Unit(String name, String abbreviation, int barColor, NumberFormat nfmt, boolean spaceAfterNumber) {
		this.name = name;
		this.abbreviation = abbreviation;
		this.barColor = barColor;
		this.format = nfmt;
		this.spaceAfterNumber = spaceAfterNumber;
	}
	
	@Override
	public String getFullName() {
		return name;
	}

	@Override
	public String getAbbreviation() {
		return abbreviation;
	}

	@Override
	public int getBarColor() {
		return barColor;
	}

	@Override
	public String format(double d) {
		String space = (spaceAfterNumber) ? " " : "";
		if (d == Double.POSITIVE_INFINITY) {
			return "∞"+space+getAbbreviation();
		} else if (d == Double.NEGATIVE_INFINITY) {
			return "-∞"+space+getAbbreviation();
		} else if (Double.isNaN(d)) {
			return "NaN"+space+getAbbreviation();
		}
		return format.format(d)+space+getAbbreviation();
	}
	
}
