package com.elytradev.fruitphone.repackage.com.elytradev.concrete;

import io.netty.buffer.ByteBuf;

/**
 * Handles the serializing and deserializing of a type. Should define a static
 * field named "INSTANCE" containing a singleton.
 */
public interface Marshaller<T> {
	T unmarshal(ByteBuf in);
	void marshal(ByteBuf out, T t);
}
