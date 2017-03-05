package com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.instanciator;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.instanciator.Instanciator;
import com.google.common.base.Throwables;

public class MethodHandlesInstanciator<T> implements Instanciator<T> {

	private MethodHandle handle;

	public MethodHandlesInstanciator(Constructor<T> c) {
		try {
			c.setAccessible(true);
			handle = MethodHandles.lookup().unreflectConstructor(c);
		} catch (IllegalAccessException e) {
			Throwables.propagate(e);
		}
	}

	@Override
	public T newInstance(Object... args) {
		try {
			return (T)handle.invokeWithArguments(args);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}

}
