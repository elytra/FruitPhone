package com.elytradev.fruitphone.repackage.com.elytradev.concrete;

import io.netty.buffer.ByteBuf;

public interface Marshallable {
	void writeToNetwork(ByteBuf buf);
	void readFromNetwork(ByteBuf buf);
}
