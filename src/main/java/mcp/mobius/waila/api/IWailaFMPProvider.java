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

package mcp.mobius.waila.api;

import java.util.List;

import net.minecraft.item.ItemStack;

/**
 * Callback class interface used to provide FMP tooltip informations to Waila.<br>
 * All methods in this interface shouldn't to be called by the implementing mod. An instance of the class is to be
 * registered to Waila via the {@link IWailaRegistrar} instance provided in the original registration callback method 
 * (cf. {@link IWailaRegistrar} documentation for more information).
 * @author ProfMobius
 *
 */
public interface IWailaFMPProvider {
	/**
	 * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).</br>
	 * Will be used if the implementing class is registered via {@link IWailaRegistrar}.{@link registerHeadProvider} client side.</br>
	 * You are supposed to always return the modified input currenttip.</br>
	 * 
	 * @param itemStack Current block scanned, in ItemStack form.
	 * @param currenttip Current list of tooltip lines (might have been processed by other providers and might be processed by other providers).
	 * @param accessor Contains most of the relevant information about the current environment.
	 * @param config Current configuration of Waila.
	 * @return Modified input currenttip
	 */
	List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor, IWailaConfigHandler config);
	
	/**
	 * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).</br>
	 * Will be used if the implementing class is registered via {@link IWailaRegistrar}.{@link registerBodyProvider} client side.</br>
	 * You are supposed to always return the modified input currenttip.</br>
	 * 
	 * @param itemStack Current block scanned, in ItemStack form.
	 * @param currenttip Current list of tooltip lines (might have been processed by other providers and might be processed by other providers).
	 * @param accessor Contains most of the relevant information about the current environment.
	 * @param config Current configuration of Waila.
	 * @return Modified input currenttip
	 */		
	List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor, IWailaConfigHandler config);
	
	/**
	 * Callback used to add lines to one of the three sections of the tooltip (Head, Body, Tail).</br>
	 * Will be used if the implementing class is registered via {@link IWailaRegistrar}.{@link registerTailProvider} client side.</br>
	 * You are supposed to always return the modified input currenttip.</br>
	 * 
	 * @param itemStack Current block scanned, in ItemStack form.
	 * @param currenttip Current list of tooltip lines (might have been processed by other providers and might be processed by other providers).
	 * @param accessor Contains most of the relevant information about the current environment.
	 * @param config Current configuration of Waila.
	 * @return Modified input currenttip
	 */		
	List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor, IWailaConfigHandler config);
}
