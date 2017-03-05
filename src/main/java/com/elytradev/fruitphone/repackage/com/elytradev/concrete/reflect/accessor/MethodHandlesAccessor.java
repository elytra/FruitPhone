package com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor.Accessor;
import com.google.common.base.Throwables;

class MethodHandlesAccessor<T> implements Accessor<T> {
	
	private MethodHandle getter;
	private MethodHandle setter;
	
	public MethodHandlesAccessor(Field f) {
		try {
			f.setAccessible(true);
			getter = MethodHandles.lookup().unreflectGetter(f);
			setter = MethodHandles.lookup().unreflectSetter(f);
		} catch (IllegalAccessException e) {
			Throwables.propagate(e);
		}
	}
	
	public MethodHandlesAccessor(Method get, Method set) {
		try {
			get.setAccessible(true);
			set.setAccessible(true);
			getter = MethodHandles.lookup().unreflect(get);
			setter = MethodHandles.lookup().unreflect(set);
		} catch (IllegalAccessException e) {
			Throwables.propagate(e);
		}
	}
	
	@Override
	public T get(Object owner) {
		try {
			return (T)getter.invoke(owner);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	@Override
	public void set(Object owner, T value) {
		try {
			setter.invoke(owner, value);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
}