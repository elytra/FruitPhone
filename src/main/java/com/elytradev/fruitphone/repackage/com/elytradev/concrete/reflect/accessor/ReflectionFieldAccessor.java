package com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor;

import java.lang.reflect.Field;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor.Accessor;
import com.google.common.base.Throwables;

class ReflectionFieldAccessor<T> implements Accessor<T> {
	private Field f;
	
	public ReflectionFieldAccessor(Field f) {
		f.setAccessible(true);
		this.f = f;
	}
	
	@Override
	public T get(Object owner) {
		try {
			return (T)f.get(owner);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}
	
	@Override
	public void set(Object owner, T value) {
		try {
			f.set(owner, value);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}
}