/*
 * Created on Jan 10, 2008
 * Copyright 2006 by Amplafi, Inc.
 */
package org.amplafi.hivemind.annotations;
import static java.lang.annotation.ElementType.*;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * supply name of service to inject with ServicesSetter.
 * @author Patrick Moore
 */
@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectService {
    String value();
}
