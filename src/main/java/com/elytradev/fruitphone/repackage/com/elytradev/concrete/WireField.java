package com.elytradev.fruitphone.repackage.com.elytradev.concrete;

import java.lang.reflect.Field;

import com.elytradev.fruitphone.repackage.com.elytradev.concrete.DefaultMarshallers;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.Marshallable;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.MarshallableMarshaller;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.Marshaller;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.annotation.field.MarshalledAs;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.annotation.field.Optional;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.exception.BadMessageException;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor.Accessor;
import com.elytradev.fruitphone.repackage.com.elytradev.concrete.reflect.accessor.Accessors;
import com.google.common.base.Throwables;

import io.netty.buffer.ByteBuf;

class WireField<T> {
	private Field f;
	private Accessor<T> accessor;
	private Marshaller<T> marshaller;
	private Class<T> type;
	private boolean optional;
	
	public WireField(Field f) {
		f.setAccessible(true);
		this.f = f;
		accessor = Accessors.from(f);
		try {
			type = (Class<T>) f.getType();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
		MarshalledAs ma = f.getAnnotation(MarshalledAs.class);
		if (ma != null) {
			marshaller = DefaultMarshallers.getByName(ma.value());
		} else if (Marshallable.class.isAssignableFrom(type)) {
			marshaller = new MarshallableMarshaller(type);
		} else {
			marshaller = DefaultMarshallers.getByType(type);
		}
		optional = f.getAnnotation(Optional.class) != null;
		if (marshaller == null && type != Boolean.TYPE) {
			String annot = "";
			if (ma != null) {
				annot = "@MarshalledAs(\""+ma.value().replace("\"", "\\\"")+"\") ";
			}
			if (optional) {
				annot = annot+"@Optional ";
			}
			throw new BadMessageException("Cannot find an appropriate marshaller for field "+annot+type+" "+f.getDeclaringClass().getName()+"."+f.getName());
		}
	}
	
	public T get(Object owner) {
		return accessor.get(owner);
	}
	
	public void set(Object owner, T value) {
		accessor.set(owner, value);
	}
	
	
	public void marshal(Object owner, ByteBuf out) {
		T value = accessor.get(owner);
		if (value == null) throw new BadMessageException("Wire fields cannot be null (in "+type+" "+f.getDeclaringClass().getName()+"."+f.getName()+")");
		marshaller.marshal(out, value);
	}
	public void unmarshal(Object owner, ByteBuf in) {
		accessor.set(owner, marshaller.unmarshal(in));
	}
	
	
	public boolean isOptional() {
		return optional;
	}
	
	
	public Class<? extends T> getType() {
		return type;
	}
}
