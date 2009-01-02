/*
 * Created on Feb 16, 2007
 * Copyright 2006 by Patrick Moore
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
