package com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor;

public interface Accessor<T> {
	T get(Object owner);
	void set(Object owner, T value);
}