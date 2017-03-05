package com.elytradev.fruitphone.repackage.com.elytradev.concrete;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.Marshaller;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.NetworkContext;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.exception.BadMessageException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.network.ByteBufUtils;

/**
 * A set of default marshallers for common data types.
 * <p>
 * Any marshaller can be used for lists by putting "-list" after its name.
 */
public class DefaultMarshallers {
	
	/**
	 * Unsigned 8-bit (1 byte) integer.
	 * <p>
	 * Aliases: u8, uint8, ubyte
	 */
	public static final Marshaller<? extends Number> UINT8 = weld(ByteBuf::writeByte, ByteBuf::readUnsignedByte);
	/**
	 * Signed 8-bit (1 byte) integer.
	 * <p>
	 * Aliases: i8, int8, byte
	 */
	public static final Marshaller<? extends Number> INT8 = weld(ByteBuf::writeByte, ByteBuf::readByte);
	
	
	/**
	 * Unsigned 16-bit (2 byte) integer.
	 * <p>
	 * Aliases: u16, uint16, char, ushort
	 */
	public static final Marshaller<? extends Number> UINT16 = weld(ByteBuf::writeShort, ByteBuf::readUnsignedShort);
	/**
	 * Signed 16-bit (2 byte) integer.
	 * <p>
	 * Aliases: i16, int16, short
	 */
	public static final Marshaller<? extends Number> INT16 = weld(ByteBuf::writeShort, ByteBuf::readShort);
	
	
	/**
	 * Unsigned 24-bit (3 byte) integer.
	 * <p>
	 * Aliases: u24, uint24, umedium
	 */
	public static final Marshaller<? extends Number> UINT24 = weld(ByteBuf::writeMedium, ByteBuf::readUnsignedMedium);
	/**
	 * Signed 24-bit (3 byte) integer.
	 * <p>
	 * Aliases: i24, int24, medium
	 */
	public static final Marshaller<? extends Number> INT24 = weld(ByteBuf::writeMedium, ByteBuf::readMedium);
	
	
	/**
	 * Unsigned 32-bit (4 byte) integer.
	 * <p>
	 * Aliases: u32, uint32, uint, uinteger
	 */
	public static final Marshaller<? extends Number> UINT32 = weld(ByteBuf::writeInt, ByteBuf::readUnsignedInt);
	/**
	 * Signed 32-bit (4 byte) integer.
	 * <p>
	 * Aliases: i32, int32, int, integer
	 */
	public static final Marshaller<? extends Number> INT32 = weld(ByteBuf::writeInt, ByteBuf::readInt);
	
	
	/**
	 * Signed 64-bit (8 byte) integer.
	 * <p>
	 * Aliases: i64, int64, long
	 */
	public static final Marshaller<? extends Number> INT64 = weld(ByteBuf::writeLong, ByteBuf::readLong);
	
	
	/**
	 * 32-bit floating point.
	 * <p>
	 * Aliases: f32, float
	 */
	public static final Marshaller<? extends Number> FLOAT = weld(ByteBuf::writeFloat, ByteBuf::readFloat);
	
	
	/**
	 * 64-bit floating point.
	 * <p>
	 * Aliases: f64, double
	 */
	public static final Marshaller<? extends Number> DOUBLE = weld(ByteBuf::writeDouble, ByteBuf::readDouble);
	
	
	/**
	 * Protobuf variable sized integer.
	 * <p>
	 * Aliases: varint
	 */
	public static final Marshaller<? extends Number> VARINT = new VarIntMarshaller();
	
	
	/**
	 * Compound NBT tag.
	 */
	public static final Marshaller<NBTTagCompound> NBT = weld(ByteBufUtils::writeTag, ByteBufUtils::readTag);
	
	
	/**
	 * UTF-8 varint-length-prefixed string.
	 */
	public static final Marshaller<String> STRING = weld(ByteBufUtils::writeUTF8String, ByteBufUtils::readUTF8String);
	
	/**
	 * Packed ItemStack.
	 */
	public static final Marshaller<ItemStack> ITEMSTACK = weld(ByteBufUtils::writeItemStack, ByteBufUtils::readItemStack);
	
	
	/**
	 * A bytebuf, only writes the readable bytes.
	 */
	public static final Marshaller<? extends ByteBuf> BYTEBUF = new ByteBufMarshaller();
	
	
	
	private static final Map<String, Marshaller<?>> byName = Maps.newHashMap();
	
	
	static {
		put(UINT8, "u8", "uint8", "ubyte");
		put(INT8, "i8", "int8", "byte");
		
		put(UINT16, "u16", "uint16", "char", "ushort");
		put(INT16, "i16", "int16", "short");
		
		put(UINT24, "u24", "uint24", "umedium");
		put(INT24, "i24", "int24", "medium");
		
		put(UINT32, "u32", "uint32", "uint", "uinteger");
		put(INT32, "i32", "int32", "int", "integer");
		
		put(INT64, "i64", "int64", "long");
		
		put(FLOAT, "f32", "float");
		put(DOUBLE, "f64", "double");
		
		put(VARINT, "varint");
		
		put(NBT, "nbt");
		
		put(STRING, "string", "str", "utf8");
		
		put(ITEMSTACK, "item", "stack", "itemstack");
	}
	
	private static void put(Marshaller<?> m, String... names) {
		for (String name : names) {
			byName.put(name, m);
		}
	}
	
	
	
	public static class ListMarshaller<T> implements Marshaller<List<T>> {
		private Marshaller<T> underlying;
		
		public ListMarshaller(Marshaller<T> underlying) {
			this.underlying = underlying;
		}
		
		@Override
		public List<T> unmarshal(ByteBuf in) {
			int size = ByteBufUtils.readVarInt(in, 5);
			List<T> li = Lists.newArrayListWithExpectedSize(size);
			for (int i = 0; i < size; i++) {
				li.add(underlying.unmarshal(in));
			}
			return li;
		}

		@Override
		public void marshal(ByteBuf out, List<T> li) {
			if (li == null) {
				ByteBufUtils.writeVarInt(out, 0, 5);
			} else {
				ByteBufUtils.writeVarInt(out, li.size(), 5);
				for (T t : li) {
					underlying.marshal(out, t);
				}
			}
		}
		
	}
	
	private static class VarIntMarshaller implements Marshaller<Number> {

		@Override
		public Number unmarshal(ByteBuf in) {
			return ByteBufUtils.readVarInt(in, 5);
		}

		@Override
		public void marshal(ByteBuf out, Number t) {
			ByteBufUtils.writeVarInt(out, t.intValue(), 5);
		}

	}
	
	private static class EnumMarshaller<T extends Enum<T>> implements Marshaller<T> {
		private final Class<T> clazz;
		private final T[] constants;
		
		public EnumMarshaller(Class<T> clazz) {
			this.clazz = clazz;
			this.constants = clazz.getEnumConstants();
		}
		
		@Override
		public T unmarshal(ByteBuf in) {
			int ordinal;
			if (constants.length < 256) {
				ordinal = in.readUnsignedByte();
			} else if (constants.length < 65536) {
				ordinal = in.readUnsignedShort();
			} else if (constants.length < 16777216) {
				ordinal = in.readUnsignedMedium();
			} else {
				ordinal = Ints.checkedCast(in.readUnsignedInt());
			}
			return constants[ordinal];
		}

		@Override
		public void marshal(ByteBuf out, T t) {
			if (constants.length < 256) {
				out.writeByte(t.ordinal());
			} else if (constants.length < 65536) {
				out.writeShort(t.ordinal());
			} else if (constants.length < 16777216) {
				out.writeMedium(t.ordinal());
			} else {
				out.writeInt(t.ordinal());
			}
		}
		
	}
	
	private static class ByteBufMarshaller implements Marshaller<ByteBuf> {
		
		@Override
		public ByteBuf unmarshal(ByteBuf in) {
			int length = ByteBufUtils.readVarInt(in, 5);
			
			return in.readBytes(length);
		}
		
		@Override
		public void marshal(ByteBuf out, ByteBuf t) {
			if (t != null) {
				ByteBufUtils.writeVarInt(out, t.readableBytes(), 5);
				out.writeBytes(t.readBytes(t.readableBytes()));
			} else {
				ByteBufUtils.writeVarInt(out, 0, 5);
			}
		}
		
	}
	
	
	private static <T> Marshaller<T> weld(Serializer<T> serializer, Deserializer deserializer) {
		return new Marshaller<T>() {
			@Override
			public void marshal(ByteBuf out, T t) {
				serializer.serialize(out, t);
			}
			@Override
			public T unmarshal(ByteBuf in) {
				return (T)deserializer.deserialize(in);
			}
		};
	}
	
	private interface Serializer<T> {
		void serialize(ByteBuf out, T t);
	}
	private interface Deserializer {
		Object deserialize(ByteBuf in);
	}
	
	public static <T> Marshaller<T> getByName(String name) {
		if (name.endsWith("-list")) {
			name = name.substring(0, name.length()-5);
			// lists of lists!
			Marshaller<T> m = getByName(name);
			if (m != null) {
				return new ListMarshaller(m);
			} else {
				return null;
			}
		} else {
			if (byName.containsKey(name.toLowerCase(Locale.ROOT))) {
				return (Marshaller<T>)byName.get(name.toLowerCase(Locale.ROOT));
			} else {
				Marshaller<T> marshaller = null;
				try {
					Class<?> clazz = Class.forName(name);
					if (Marshaller.class.isAssignableFrom(clazz)) {
						try {
							Field inst = clazz.getDeclaredField("INSTANCE");
							inst.setAccessible(true);
							marshaller = (Marshaller<T>) inst.get(null);
						} catch (Exception e) {
							NetworkContext.log.warn(clazz.getName()+" does not appear to define an INSTANCE field, but it should");
						}
						if (marshaller == null) {
							try {
								Constructor<?> cons = clazz.getConstructor();
								marshaller = (Marshaller<T>) cons.newInstance();
							} catch (Exception e) {
								throw new BadMessageException("Cannot instanciate marshaller class "+clazz.getName());
							}
						}
					}
				} catch (Exception e) {
					return null;
				}
				return marshaller;
			}
		}
	}

	public static <T> Marshaller<T> getByType(Class<T> type) {
		if (String.class.isAssignableFrom(type)) {
			return (Marshaller<T>)STRING;
		} else if (NBTTagCompound.class.isAssignableFrom(type)) {
			return (Marshaller<T>)NBT;
		} else if (type.isEnum()) {
			return new EnumMarshaller(type);
		} else if (ItemStack.class.isAssignableFrom(type)) {
			return (Marshaller<T>)ITEMSTACK;
		} else if (ByteBuf.class.isAssignableFrom(type)) {
			return (Marshaller<T>)BYTEBUF;
		}
		return null;
	}
}
