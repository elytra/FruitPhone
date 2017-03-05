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

package com.elytradev.probe.api;

/**
 * Represents information about a quantity being expressed. Nominally, the unit of measurement, but also the kind of
 * thing being measured.
 */
public interface IUnit {
	public String getFullName();
	public String getAbbreviation();
	/**
	 * @return a color to represent this unit, in srgb "00RRGGBB" format
	 */
	public int getBarColor();
	/**
	 * Take a quantity expressed in this unit, and produce a human-readable display, such as 200.0D becoming "200 kFU"
	 * @param d the quantity being represented
	 * @return A full String expressing the number and abbreviated unit, with any SI prefixing needed.
	 */
	public String format(double d);
}
