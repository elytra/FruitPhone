package com.elytradev.fruitphone.repackage.com.elytradev.concrete.annotation.type;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cpw.mods.fml.relauncher.Side;

/**
 * Specifies the side that a Message will be received on.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReceivedOn {
	public Side value();
}
