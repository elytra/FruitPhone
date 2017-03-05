package com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.invoker;

import java.lang.reflect.Method;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.invoker.Invoker;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.invoker.MethodHandlesInvoker;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.invoker.ReflectionInvoker;

import cpw.mods.fml.relauncher.ReflectionHelper;

public final class Invokers {
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
	
	public static <T> Invoker findMethod(Class<T> clazz, T instance, String[] methodNames, Class<?>... methodTypes) {
		return from(ReflectionHelper.findMethod(clazz, instance, methodNames, methodTypes));
	}
	
	public static Invoker from(Method m) {
		if (methodHandlesAvailable) {
			return new MethodHandlesInvoker(m);
		} else {
			return new ReflectionInvoker(m);
		}
	}
	
	private Invokers() {}
}
