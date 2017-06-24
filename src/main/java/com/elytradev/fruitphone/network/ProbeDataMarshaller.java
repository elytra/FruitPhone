/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2017 William Thompson (unascribed)
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

package com.elytradev.fruitphone.network;

import com.google.common.collect.ImmutableList;
import com.elytradev.concrete.network.Marshaller;
import com.elytradev.probe.api.IProbeData;
import com.elytradev.probe.api.UnitDictionary;
import com.elytradev.probe.api.impl.ProbeData;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class ProbeDataMarshaller implements Marshaller<IProbeData> {
	
	public static final String NAME = "com.elytradev.fruitphone.network.ProbeDataMarshaller";
	
	public static final ProbeDataMarshaller INSTANCE = new ProbeDataMarshaller();
	
	private static final int BAR_BIT       = 0b00000001;
	private static final int LABEL_BIT     = 0b00000010;
	private static final int INVENTORY_BIT = 0b00000100;
	private static final int BAR_UNIT_BIT  = 0b00001000;
	
	
	@Override
	public void marshal(ByteBuf out, IProbeData t) {
		int bits = 0;

		boolean bar = t.hasBar();
		boolean label = t.hasLabel();
		boolean inventory = t.hasInventory();
		boolean barHasUnit = false;
		if (bar) {
			barHasUnit = t.getBarUnit() != null;
		}
		
		if (bar) bits |= BAR_BIT;
		if (label) bits |= LABEL_BIT;
		if (inventory) bits |= INVENTORY_BIT;
		if (barHasUnit) bits |= BAR_UNIT_BIT;
		out.writeByte(bits);
		
		if (bar) {
			out.writeDouble(t.getBarMinimum());
			out.writeDouble(t.getBarCurrent());
			out.writeDouble(t.getBarMaximum());
			if (barHasUnit) {
				ByteBufUtils.writeUTF8String(out, t.getBarUnit().getFullName());
			}
		}
		if (label) {
			ByteBufUtils.writeUTF8String(out, ITextComponent.Serializer.componentToJson(t.getLabel()));
		}
		if (inventory) {
			ImmutableList<ItemStack> inv = t.getInventory();
			ByteBufUtils.writeVarInt(out, inv.size(), 5);
			for (ItemStack is : inv) {
				// I have seen people ASM writeItemStack before, so we write the
				// extended stack size out-of-band, rather than serializing the
				// stack ourselves
				ByteBufUtils.writeItemStack(out, is);
				ByteBufUtils.writeVarInt(out, is.getCount(), 5);
			}
		}
	}
	
	@Override
	public IProbeData unmarshal(ByteBuf in) {
		int bits = in.readUnsignedByte();
	
		boolean bar = (bits & BAR_BIT) != 0;
		boolean label = (bits & LABEL_BIT) != 0;
		boolean inventory = (bits & INVENTORY_BIT) != 0;
		boolean barHasUnit = (bits & BAR_UNIT_BIT) != 0;
		
		ProbeData pd = new ProbeData();
		
		if (bar) {
			pd.withBar(in.readDouble(),
					in.readDouble(),
					in.readDouble(),
					barHasUnit ? UnitDictionary.getInstance().getUnit(ByteBufUtils.readUTF8String(in)) : null);
		}
		if (label) {
			pd.withLabel(ITextComponent.Serializer.jsonToComponent(ByteBufUtils.readUTF8String(in)));
		}
		if (inventory) {
			int size = ByteBufUtils.readVarInt(in, 5);
			ItemStack[] stacks = new ItemStack[size];
			for (int i = 0; i < size; i++) {
				ItemStack is = ByteBufUtils.readItemStack(in);
				is.setCount(ByteBufUtils.readVarInt(in, 5));
				stacks[i] = is;
			}
			pd.withInventory(ImmutableList.copyOf(stacks));
		}
		return pd;
	}
	
}
