package com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor.Accessor;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor.MethodHandlesAccessor;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor.ReflectionFieldAccessor;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor.ReflectionMethodAccessor;

import cpw.mods.fml.relauncher.ReflectionHelper;

public final class Accessors {
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
	
	public static <T> Accessor<T> from(Field f) {
		if (methodHandlesAvailable) {
			return new MethodHandlesAccessor<>(f);
		} else {
			return new ReflectionFieldAccessor<>(f);
		}
	}
	
	public static <T> Accessor<T> findField(Class<?> clazz, String... names) {
		return from(ReflectionHelper.findField(clazz, names));
	}
	
	public static <T> Accessor<T> from(Method get, Method set) {
		if (methodHandlesAvailable) {
			return new MethodHandlesAccessor<>(get, set);
		} else {
			return new ReflectionMethodAccessor<>(get, set);
		}
	}
	
	private Accessors() {}
}
