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

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

/**
 * <p>Represents a single "line item" of probe information. Usually a ProbeData represents the output from one
 * Capability, such as the energy stored in a battery, or the items stored in a hopper. Occasionally, an especially
 * complex Capability will require two lines, such as a machine with seperate input and output inventory pools.
 * 
 * <p>A ProbeData contains several "fields" of information of different types. Every field is optional, and probe
 * implementations are encouraged to rearrange or elide data in order to produce clearer or more visually appealing
 * layouts. Each ProbeData line is meant to say something *semantic* about the thing being queried, and ideally no
 * data is presented by a ProbeDataProvider in a particular way in order to provoke a particular layout.
 * 
 * <p>Probe implementations should only gather this information on the server. How this data is then serialized and sent
 * to the client is outside the scope of this API.
 * 
 * <h2>Label</h2>
 * <p><ul>
 * <li><b>Purpose</b>: Unavoidably textual information
 * <li><b>Examples</b>: The custom name of an inventory, the mode of a machine, text in a console, debug information
 * <li><b>Layout</b>: Typically displayed at the top-center of a data line, but can be layed out wherever most
 * visually appealing. Sometimes the label is unavoidably the only field present in a data line, and covers the whole
 * thing.
 * </ul></p>
 * 
 * <h2>Bar</h2>
 * <p><ul>
 * <li><b>Purpose</b>: Numeric quantities that have a predictable minimum and maximum
 * <li><b>Examples</b>: Energy storage or generation, liquid storage, machine progress, light levels
 * <li><b>Layout</b>: Implementors have latitude to abbreviate quantities using SI prefixes and whatever rounding mode
 * they (or users) choose. The bar can be styled based on its unit, its label, or even information only available to the
 * probe implementor, such as which Capability the bar comes from, or the color of the fluid represented. For
 * robustness, implementors should bounds-check numbers and prevent display of bars past their edges, even though
 * providers are forbidden from presenting them. If min, max, and cur are identical, an implementor may choose to forgo
 * the bar and render only the number and its units.
 * </ul></p>
 * 
 * <p>DataProviders are encouraged to present numbers in single whole-numbered units, so that the probe implementation
 * can respect user settings regarding abbreviation and SI prefixing. This means single RF even in blocks that store
 * quadrillions, and whole buckets of fluids instead of millibuckets. Providers should also strive not to change units
 * from frame to frame unless the bar actually refers to a new substance or quality (such as a different liquid in a
 * tank, or running out of one kind of power and switching to using a different one).
 * 
 * <p>When creating custom bar units, make sure to register them with {@link UnitDictionary} on both the client and the
 * server, and give the unit a unique fullName. Feel free to use the static units declared in that class as well, either
 * directly, or as a model for how custom units should look.
 * 
 * <h2>Inventory</h2>
 * <p><ul>
 * <li><b>Purpose</b>: any information about items
 * <li><b>Examples</b>: Block inventories, filter exemplar items
 * <li><b>Layout</b>: Implementors have extremely wide latitude on how to display this field. They can display empty
 * slots, or not. They can elide two stacks of the same item, or not. They can display textual information instead of
 * icons, they can omit items in their summary, and they can enlarge a one-item inventory and display it to the left.
 * Probe implementations have no requirement to display item fields as presented. Or at all.
 * </ul></p>
 * 
 * <p>This field is fairly straightforward, and DataProviders should expose straightforward views of themselves in this
 * respect. If an inventory has empty slots, it should report empty ItemStacks if possible (with slotless inventories
 * this is obviously not possible). If a block has two inventories, it should report two separate ProbeData lines, each
 * with its own inventory (which the probe implementation may choose to display side by side, because why not?). If
 * ItemStacks move from slot to slot, this should be reflected in the provider view. Providers should not change the
 * size of their reported inventories if possible (again, slotless inventories have no choice, and their size will
 * necessarily change based on the number of items contained). Most inventories in Minecraft have fixed sizes and should
 * be reported in full.
 */
public interface IProbeData {
	/**
	 * Determines whether or not this ProbeData contains textual information
	 * @return true if a 
	 */
	public boolean hasLabel();
	public boolean hasBar();
	public boolean hasInventory();
	
	/**
	 * Gets the label of this ProbeData. This field is for unavoidably textual data, such as the custom name of an
	 * inventory
	 * @return the textual label part of this ProbeData
	 */
	@Nonnull
	public IChatComponent getLabel();
	
	/**
	 * Gets the minimum quantity expressable on this ProbeData's bar. If the current and minimum of the bar are the same,
	 * the bar should be rendered "empty".
	 * @return The lower bound for the bar's numeric values.
	 */
	public double getBarMinimum();
	
	/**
	 * Gets the current value to display on this ProbeData's bar. This number must be between the bar's minimum and
	 * maximum, inclusive.
	 * @return The current numeric value of the bar.
	 */
	public double getBarCurrent();
	
	/**
	 * Gets the highest possible value expressable on this ProbeData's bar. Often this is the total capacity of a fluid
	 * or energy storage capability.
	 * @return The upper bound for the bar's numeric values.
	 */
	public double getBarMaximum();
	
	/**
	 * Gets the abbreviated proper unit for this ProbeData's bar. For example, something supplying 2200 Forge Units of
	 * power might return "FU" as the unit. The probe implementation is then free to abbreviate quantities, e.g.
	 * "2.2kFU".
	 * @return The unit for the bar, or an empty String if there's no unit.
	 */
	@Nonnull
	public IUnit getBarUnit();
	
	/**
	 * Gets any inventory associated with this ProbeData. In the case where only a single itemslot is exposed, a probe
	 * implementation may choose to enlarge the displayed item.
	 * @return a List containing all visible inventory slots, empty or filled, in this inventory. A probe
	 * implementation is then free to elide stacks or slots in order to present the inventory more succinctly, or
	 * depending on its configuration.
	 */
	@Nonnull
	public List<ItemStack> getInventory();
}
