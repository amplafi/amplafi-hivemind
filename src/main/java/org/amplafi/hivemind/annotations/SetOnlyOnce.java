/*
 * Created on Sep 15, 2006
 * Copyright 2006 by Patrick Moore
 */
package org.amplafi.hivemind.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
/**
 * use to flag a method or field that should be
 * set only once. It is ok to set the value to the same value.
 * 
 * If a method is flagged, it must be the setter and the 
 * field must follow the bean pattern.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SetOnlyOnce {
}
