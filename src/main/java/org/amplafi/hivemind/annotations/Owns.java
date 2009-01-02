
package org.amplafi.hivemind.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used on the getter method of a property. Indicates the property's value should not be shared.
 * If the owning object is cloned, the property must also be cloned.
 *
 * @author Patrick Moore
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Owns {
}
