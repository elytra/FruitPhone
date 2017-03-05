package com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor;

import java.lang.reflect.Method;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor.Accessor;
import com.google.common.base.Throwables;

class ReflectionMethodAccessor<T> implements Accessor<T> {
	private Method get;
	private Method set;
	
	public ReflectionMethodAccessor(Method get, Method set) {
		get.setAccessible(true);
		set.setAccessible(true);
		this.get = get;
		this.set = set;
	}
	
	@Override
	public T get(Object owner) {
		try {
			return (T)get.invoke(owner);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}
	
	@Override
	public void set(Object owner, T value) {
		try {
			set.invoke(owner, value);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}
}