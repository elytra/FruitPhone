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

package mcp.mobius.waila.api;

import net.minecraft.item.ItemStack;

/**
 * Callback class interface used to provide Block/TileEntity decorator.<br>
 * All methods in this interface shouldn't to be called by the implementing mod. An instance of the class is to be
 * registered to Waila via the {@link IWailaRegistrar} instance provided in the original registration callback method 
 * (cf. {@link IWailaRegistrar} documentation for more information).
 * @author ProfMobius
 *
 */
public interface IWailaBlockDecorator {

	/**
	 * Callback for the decorator. It provides a standard GL stack positioned on the block.</br>
	 * Will be used if the implementing class is registered via {@link IWailaRegistrar}.{@link registerDecorator}.</br>
	 * @param itemStack Current block scanned, in ItemStack form. 
	 * @param accessor Contains most of the relevant information about the current environment.
	 * @param config Current configuration of Waila.
	 */
	void decorateBlock(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config);	
	
}
