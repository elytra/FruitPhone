package com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.invoker;

import java.lang.reflect.Method;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.invoker.Invoker;
import com.google.common.base.Throwables;

public class ReflectionInvoker implements Invoker {

	private final Method m;
	
	public ReflectionInvoker(Method m) {
		m.setAccessible(true);
		this.m = m;
	}

	@Override
	public Object invoke(Object owner, Object... args) {
		try {
			return m.invoke(owner, args);
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

}
