package com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.instanciator;

import java.lang.reflect.Constructor;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.instanciator.Instanciator;
import com.google.common.base.Throwables;

public class ReflectionInstanciator<T> implements Instanciator<T> {

	private final Constructor<T> c;
	
	public ReflectionInstanciator(Constructor<T> c) {
		c.setAccessible(true);
		this.c = c;
	}

	@Override
	public T newInstance(Object... args) {
		try {
			return c.newInstance(args);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

}
