/*
 * Created on Oct 2, 2006
 * Copyright 2006 by Patrick Moore
 */
package org.amplafi.hivemind.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * used to flag methods that can only be used after the object has had it's
 * initialize method called.
 * @author Patrick Moore
 */

@Target({FIELD, METHOD})
@Retention(RUNTIME)
public @interface MustBeInit {
    /**
     * @return true if the init method should be called
     * if it hasn't been already.
     *
     */
    public boolean callInit() default true;
    /**
     * @return to use. The default is "init" if no initMethods have
     * been declared on the class.
     *
     */
    public String initMethod();
    public String[] initMethods();
}
