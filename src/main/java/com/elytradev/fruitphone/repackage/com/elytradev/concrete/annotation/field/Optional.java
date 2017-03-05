package com.elytradev.fruitphone.repackage.com.elytradev.concrete.annotation.field;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies a Message field as optional, meaning it may be null. This will
 * incur a cost of one boolean, with the same bitfield optimization as normal
 * booleans. Null Message fields are normally an error.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {}
