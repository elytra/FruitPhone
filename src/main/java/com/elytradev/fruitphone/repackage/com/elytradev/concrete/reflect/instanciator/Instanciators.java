package com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.instanciator;

import java.lang.reflect.Constructor;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.instanciator.Instanciator;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.instanciator.MethodHandlesInstanciator;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.instanciator.ReflectionInstanciator;

public final class Instanciators {
	private static final boolean methodHandlesAvailable;
	static {
		boolean hasMethodHandles;
		try {
			Class.forName("java.lang.invoke.MethodHandles");
			hasMethodHandles = true;
		} catch (Exception e) {
			hasMethodHandles = false;
		}
		methodHandlesAvailable = hasMethodHandles;
	}
	
	public static <T> Instanciator<T> from(Constructor<T> c) {
		if (methodHandlesAvailable) {
			return new MethodHandlesInstanciator<>(c);
		} else {
			return new ReflectionInstanciator<>(c);
		}
	}
	
	private Instanciators() {}
}
