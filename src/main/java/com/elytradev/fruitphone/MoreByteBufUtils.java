package com.elytradev.fruitphone;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class MoreByteBufUtils {

	public static int toZigZag(int val) {
		return (val << 1) ^ (val >> 31);
	}
	
	public static int fromZigZag(int val) {
		return (val >> 1) ^ (-(val & 1));
	}
	
	public static void writeZigZagVarInt(ByteBuf out, int val, int maxSize) {
		ByteBufUtils.writeVarInt(out, toZigZag(val), maxSize);
	}
	
	public static int readZigZagVarInt(ByteBuf in, int maxSize) {
		return fromZigZag(ByteBufUtils.readVarInt(in, maxSize));
	}
	
}
