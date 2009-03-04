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

package org.amplafi.hivemind.factory.facade;

import java.util.ArrayList;
import java.util.List;

import org.apache.hivemind.internal.ServicePoint;

/**
 * Factory parameter class for {@link FacadeImplementationFactory}.
 *
 * ImplementingClass - the interface that is the "facade".
 * RealServicePoint - used when there are multiple implementors of 'implementingClass'
 *
 * @author Patrick Moore
 */
public class FacadeImplementationFactoryParameter {
    private Class<?> implementingClass;
    private ServicePoint realServicePoint;
    private List<Class<?>> additionalImplementingClasses = new ArrayList<Class<?>>();
    /**
     * @param implementingClass the implementingClass to set
     */
    public void setImplementingClass(Class<?> implementingClass) {
        this.implementingClass = implementingClass;
    }
    /**
     * Hivemind registry is searched for an implementor of 'implementingClass'.
     * @return the implementingClass
     */
    public Class<?> getImplementingClass() {
        return implementingClass;
    }
    /**
     * @param realServicePoint the realServicePoint to set
     */
    public void setRealServicePoint(ServicePoint realServicePoint) {
        this.realServicePoint = realServicePoint;
    }
    /**
     * @return the realServicePoint
     */
    public ServicePoint getRealServicePoint() {
        return realServicePoint;
    }
    public boolean isValid() {
        return this.implementingClass != null || this.realServicePoint != null;
    }
    /**
     * @param additionalImplementingClasses the additionalImplementingClasses to set
     */
    public void setAdditionalImplementingClasses(List<Class<?>> additionalImplementingClasses) {
        this.additionalImplementingClasses.clear();
        if ( additionalImplementingClasses != null ) {
            this.additionalImplementingClasses.addAll(additionalImplementingClasses);
        }
    }
    /**
     * @return the additionalImplementingClasses
     */
    public List<Class<?>> getAdditionalImplementingClasses() {
        return additionalImplementingClasses;
    }

    public void addInterface(Class<?> additionalInterface) {
        this.additionalImplementingClasses.add(additionalInterface);
    }
}
