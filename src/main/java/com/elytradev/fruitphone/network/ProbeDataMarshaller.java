package com.elytradev.fruitphone.network;

import com.elytradev.fruitphone.MoreByteBufUtils;
import com.google.common.collect.ImmutableList;
import io.github.elytra.concrete.Marshaller;
import io.github.elytra.probe.api.IProbeData;
import io.github.elytra.probe.api.impl.ProbeData;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class ProbeDataMarshaller implements Marshaller<IProbeData> {
	
	public static final String NAME = "com.elytradev.fruitphone.network.ProbeDataMarshaller";
	
	private static final int BAR_BIT       = 0b00000001;
	private static final int LABEL_BIT     = 0b00000010;
	private static final int INVENTORY_BIT = 0b00000100;
	
	
	@Override
	public void marshal(ByteBuf out, IProbeData t) {
		boolean bar = t.hasBar();
		boolean label = t.hasLabel();
		boolean inventory = t.hasInventory();
		
		int bits = 0;
		if (bar) bits |= BAR_BIT;
		if (label) bits |= LABEL_BIT;
		if (inventory) bits |= INVENTORY_BIT;
		out.writeByte(bits);
		
		if (bar) {
			MoreByteBufUtils.writeZigZagVarInt(out, t.getBarMinimum(), 5);
			MoreByteBufUtils.writeZigZagVarInt(out, t.getBarCurrent(), 5);
			MoreByteBufUtils.writeZigZagVarInt(out, t.getBarMaximum(), 5);
			ByteBufUtils.writeUTF8String(out, t.getBarUnit());
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
		
		ProbeData pd = new ProbeData();
		
		if (bar) {
			pd.withBar(MoreByteBufUtils.readZigZagVarInt(in, 5),
					MoreByteBufUtils.readZigZagVarInt(in, 5),
					MoreByteBufUtils.readZigZagVarInt(in, 5),
					ByteBufUtils.readUTF8String(in));
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
