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
