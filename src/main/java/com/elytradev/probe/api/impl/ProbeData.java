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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.IUnit;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class ProbeData implements IProbeData {
	private IChatComponent label = null;
	private double barMin = Double.NaN;
	private double barCur = Double.NaN;
	private double barMax = Double.NaN;
	private IUnit barUnit = null;
	private List<ItemStack> inventory = null;
	
	/**
	 * Creates a blank data line
	 */
	public ProbeData() {}
	
	/**
	 * Creates a data line with text information
	 * @param label the information to be displayed on this line
	 */
	public ProbeData(String label) {
		this.label = new ChatComponentText(label);
	}
	
	/**
	 * Creates a data line with text information
	 * @param label the information to be displayed on this line
	 */
	public ProbeData(IChatComponent label) {
		this.label = label;
	}
	
	/**
	 * Adds an label to this ProbeData
	 * @param label the text to add to this ProbeData
	 * @return this ProbeData
	 */
	public ProbeData withLabel(String label) {
		this.label = new ChatComponentText(label);
		return this;
	}
	
	/**
	 * Adds an label to this ProbeData
	 * @param label the text to add to this ProbeData
	 * @return this ProbeData
	 */
	public ProbeData withLabel(IChatComponent label) {
			this.label = label;
		return this;
	}
	
	/**
	 * Adds a bar to this ProbeData
	 * @param minimum The lowest possible value for the bar
	 * @param current The current value of the bar. Must be between minimum and maximum, inclusive
	 * @param maximum The highest possible value for the bar
	 * @param unit The unit that the quantities in the bar are expressed in. Use an empty string to specify no units.
	 * @return this ProbeData
	 */
	public ProbeData withBar(double minimum, double current, double maximum, IUnit unit) {
		this.barMin = minimum;
		this.barCur = current;
		this.barMax = maximum;
		this.barUnit = unit;
		return this;
	}
	
	/**
	 * Adds an inventory to this ProbeData
	 * @param inventory the contents of all itemslots, full or empty, in this datum.
	 * @return this ProbeData
	 */
	public ProbeData withInventory(@Nonnull List<ItemStack> inventory) {
		this.inventory = inventory;
		return this;
	}

	@Override
	public boolean hasLabel() {
		return label!=null;
	}

	@Override
	public boolean hasBar() {
		return !Double.isNaN(barMin) && !Double.isNaN(barCur) && !Double.isNaN(barMax);
	}

	@Override
	@Nonnull
	public IChatComponent getLabel() {
		return label!=null ? label : new ChatComponentText("");
	}

	@Override
	public double getBarMinimum() {
		return barMin;
	}
	
	@Override
	public double getBarCurrent() {
		return barCur;
	}

	@Override
	public double getBarMaximum() {
		return barMax;
	}

	@Override
	@Nullable
	public IUnit getBarUnit() {
		return barUnit;
	}

	@Override
	public boolean hasInventory() {
		return inventory!=null;
	}

	@Override
	@Nullable
	public List<ItemStack> getInventory() {
		return inventory;
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(barCur);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(barMax);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(barMin);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((barUnit == null) ? 0 : barUnit.hashCode());
		result = prime * result + ((inventory == null) ? 0 : stackListHashCode(inventory));
		try {
			result = prime * result + ((label == null) ? 0 : label.hashCode());
		} catch (NullPointerException e) {
			// Some ChatComponent implementations have broken hashCode methods
			// Ignore Mojang's quality code
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ProbeData other = (ProbeData) obj;
		if (Double.doubleToLongBits(barCur) != Double
				.doubleToLongBits(other.barCur)) {
			return false;
		}
		if (Double.doubleToLongBits(barMax) != Double
				.doubleToLongBits(other.barMax)) {
			return false;
		}
		if (Double.doubleToLongBits(barMin) != Double
				.doubleToLongBits(other.barMin)) {
			return false;
		}
		if (barUnit == null) {
			if (other.barUnit != null) {
				return false;
			}
		} else if (!barUnit.equals(other.barUnit)) {
			return false;
		}
		if (inventory == null) {
			if (other.inventory != null) {
				return false;
			}
		} else if (!stackListsEqual(inventory, other.inventory)) {
			return false;
		}
		if (label == null) {
			if (other.label != null) {
				return false;
			}
		} else if (!label.equals(other.label)) {
			return false;
		}
		return true;
	}

	private static int stackListHashCode(List<ItemStack> li) {
		int result = 1;
		for (ItemStack is : li) {
			result = 31 * result + (is == null ? 0 : stackHashCode(is));
		}
		return result;
	}

	private static int stackHashCode(ItemStack is) {
		if (is == null) return 0;
		final int prime = 31;
		int result = 1;
		result = prime * result + is.getItem().hashCode();
		result = prime * result + is.stackSize;
		result = prime * result + is.getMetadata();
		result = prime * result + (is.hasTagCompound() ? is.getTagCompound().hashCode() : 0);
		return result;
	}

	private static boolean stackListsEqual(List<ItemStack> a, List<ItemStack> b) {
		if (a == b) return true;
		if (a == null || b == null) return false;
		if (a.size() != b.size()) return false;
		for (int i = 0; i < a.size(); i++) {
			ItemStack isa = a.get(i);
			ItemStack isb = b.get(i);
			if (!ItemStack.areItemStacksEqual(isa, isb)) {
				return false;
			}
		}
		return true;
	}
	

}
