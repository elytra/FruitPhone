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

package com.elytradev.fruitphone.early;

import com.elytradev.fruitphone.FruitPhone;

import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;

public class WailaDummyModContainer extends FruitPhoneDummyModContainer {
	public WailaDummyModContainer() {
		super("Waila", "waila");
	}
	
	@Override
	public Object getMod() {
		return this;
	}
	

	@EventHandler
	public void processIMC(FMLInterModComms.IMCEvent event) {
		System.out.println("process imc");
		for (IMCMessage imc : event.getMessages()) {
			if (!imc.isStringMessage())
				continue;
			
			if (imc.key.equalsIgnoreCase("register")) {
				FruitPhone.log.info("Receiving legacy Waila registration request for {}::{}", imc.getSender(), imc.getStringValue());
				ModuleRegistrar.instance().addIMCRequest(imc.getStringValue(), imc.getSender());
			}
		}
	}
}
