/*
 * Created on Feb 16, 2007
 * Copyright 2006 by Patrick Moore
 */
package org.amplafi.hivemind.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.*;

/**
 * used to signal an instance of a class should never be inject,
 * or
 * this field or method should never be set via the servicesSetter.
 * @author Patrick Moore
 */
@Target({TYPE,FIELD,METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotService {

}
