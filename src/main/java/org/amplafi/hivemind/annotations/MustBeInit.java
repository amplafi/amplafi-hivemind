/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
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
