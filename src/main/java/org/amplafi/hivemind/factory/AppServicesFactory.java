/*
 * Created on Jan 24, 2007
 * Copyright 2006 by Patrick Moore
 */
package org.amplafi.hivemind.factory;

import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;

/**
 * This is needed because adding interceptors to hivemind.BuilderFactory causes
 * too many issues with race conditions. 
 * @author Patrick Moore
 */
public class AppServicesFactory implements ServiceImplementationFactory {

    private ServiceImplementationFactory realServiceImplementationFactory;
    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters) {
        return getRealServiceImplementationFactory().createCoreServiceImplementation(factoryParameters);
    }
    /**
     * @param realServiceImplementationFactory the realServiceImplementationFactory to set
     */
    public void setRealServiceImplementationFactory(ServiceImplementationFactory realServiceImplementationFactory) {
        this.realServiceImplementationFactory = realServiceImplementationFactory;
    }
    /**
     * @return the realServiceImplementationFactory
     */
    public ServiceImplementationFactory getRealServiceImplementationFactory() {
        return realServiceImplementationFactory;
    }

}
