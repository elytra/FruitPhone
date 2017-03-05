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

import java.text.NumberFormat;

/**
 * A Unit that uses binary prefixes to format numbers, turning things like
 * "1200" into "1.17Ki"
 */
public class BinaryUnit extends Unit {

	public BinaryUnit(String name, String abbreviation, int barColor, NumberFormat nfmt, boolean spaceAfterNumber) {
		super(name, abbreviation, barColor, nfmt, spaceAfterNumber);
	}

	public BinaryUnit(String name, String abbreviation, int barColor) {
		super(name, abbreviation, barColor);
	}

	public BinaryUnit(String name, String abbreviation) {
		super(name, abbreviation);
	}
	
	
	private static final double KIBI = 1_024D;
	private static final double MEBI = KIBI*1024D;
	private static final double GIBI = MEBI*1024D;
	private static final double TEBI = GIBI*1024D;
	private static final double PEBI = TEBI*1024D;
	private static final double EXBI = PEBI*1024D;
	private static final double ZEBI = EXBI*1024D;
	private static final double YOBI = ZEBI*1024D;
	private static final double HEBI = YOBI*1024D;
	
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
		
		double magnitude = Math.abs(d);
		
		if (magnitude>HEBI) {
			return format.format(d/HEBI)+space+"Xi"+getAbbreviation();
		} else if (magnitude>YOBI) {
			return format.format(d/YOBI)+space+"Yi"+getAbbreviation();
		} else if (magnitude>ZEBI) {
			return format.format(d/ZEBI)+space+"Zi"+getAbbreviation();
		} else if (magnitude>EXBI) {
			return format.format(d/EXBI)+space+"Ei"+getAbbreviation();
		} else if (magnitude>PEBI) {
			return format.format(d/PEBI)+space+"Pi"+getAbbreviation();
		} else if (magnitude>TEBI) {
			return format.format(d/TEBI)+space+"Ti"+getAbbreviation();
		} else if (magnitude>GIBI) {
			return format.format(d/GIBI)+space+"Gi"+getAbbreviation();
		} else if (magnitude>MEBI) {
			return format.format(d/MEBI)+space+"Mi"+getAbbreviation();
		} else if (magnitude>KIBI) {
			return format.format(d/KIBI)+space+"Ki"+getAbbreviation();
		} else {
			return format.format(d)+space+getAbbreviation();
		}
	}
	
}
