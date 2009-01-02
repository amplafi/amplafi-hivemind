
package org.amplafi.hivemind.factory.facade;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;

/**
 * factory that acts as a facade for another (real) service.
 * Enables one underlying service to implement 2+ interfaces and
 * enable hivemind to still do its autowire magic. Hivemind only looks at the interface
 * defined in the interface attribute on the service-point or implementation specification
 * when doing autowiring. FacadeImplementationFactory bypasses this limitation.
 *
 * Also useful for case where test code needs access to methods that should not be visible
 * to production code.
 *
 * Handles situation where underlying service does not actually implement
 * the facade's interface but does implement methods with the same signature.
 *
 * Sample configuration (real service is com.amplafi.core.security.AuthenticationService):
 * <pre>
 *  <implementation service-id="hivemind.acegi.dao.UserDetailsService">
 *       <invoke-factory service-id="amplafi.factory.facade.facadeServicesFactory">
 *          <service class="com.amplafi.core.security.AuthenticationService"/>
 *      </invoke-factory>
 *  </implementation>
 *
 *  or
 *
 *  <implementation service-id="hivemind.acegi.dao.UserDetailsService">
 *       <invoke-factory service-id="amplafi.factory.facade.facadeServicesFactory">
 *          <service service-id="amplafi.security.myAuthenticationServiceInstance"/>
 *      </invoke-factory>
 *  </implementation>
 * </pre>
 * @author Patrick Moore
 */
public class FacadeImplementationFactory implements
        ServiceImplementationFactory {

    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters factoryParameters) {
        FacadeImplementationFactoryParameter facadeImplementationFactoryParameter =
            (FacadeImplementationFactoryParameter) factoryParameters.getFirstParameter();
        if ( !facadeImplementationFactoryParameter.isValid()) {
            throw new ApplicationRuntimeException("service-id nor class attribute supplied.",
                    // don't know if this location value is correct -- but what else to use?
                    factoryParameters.getInvokingModule().getLocation(), null);
        }
        Module module = factoryParameters.getInvokingModule();
        Class<?> serviceInterface = facadeImplementationFactoryParameter.getImplementingClass();
        Object realService;
        ServicePoint realServicePoint = facadeImplementationFactoryParameter.getRealServicePoint();
        if ( realServicePoint != null ) {
            realService = realServicePoint.getService(Object.class);
        } else {
            realService = module.getService(serviceInterface);
        }
        Class<? extends Object> realServiceClass = realService.getClass();
        Class<?> facadeServiceInterface = factoryParameters.getServiceInterface();
        List<Class<?>> additionalImplementingClasses = getMissingClasses(facadeImplementationFactoryParameter, realServiceClass);
        if ( facadeServiceInterface.getClass().isAssignableFrom(realServiceClass)) {
                // the facade interface is a super interface, or the same interface as the real interface.
            if ( additionalImplementingClasses.isEmpty()) {
                return realService;
            }
        }

        additionalImplementingClasses.add(facadeServiceInterface);
        FacadeServiceProxy handler = new FacadeServiceProxy(realService);
            return Proxy.newProxyInstance(
                    facadeServiceInterface.getClassLoader(),
                    additionalImplementingClasses.toArray(new Class[additionalImplementingClasses.size()]),
                    handler);
    }

    /**
     * @param facadeImplementationFactoryParameter
     * @param realServicePoint
     * @return
     */
    private List<Class<?>> getMissingClasses(FacadeImplementationFactoryParameter facadeImplementationFactoryParameter, Class<?> realServiceClass) {
        List<Class<?>> additionalImplementingClasses = facadeImplementationFactoryParameter.getAdditionalImplementingClasses();
        Class<?> implementingClass = facadeImplementationFactoryParameter.getImplementingClass();
        if ( additionalImplementingClasses != null && !additionalImplementingClasses.isEmpty()) {
            additionalImplementingClasses = new ArrayList<Class<?>>(additionalImplementingClasses);
            for(Iterator<Class<?>> iter = additionalImplementingClasses.iterator(); iter.hasNext();) {
                if ( iter.next().isAssignableFrom(implementingClass)) {
                    // don't need this interface in the proxied interface list because this interface is implemented by implementingClass
                    iter.remove();
                }
            }
        } else {
            additionalImplementingClasses = new ArrayList<Class<?>>();
        }

        // check that the realService's interface is a not subinterface of the facade interface.
        if ( realServiceClass.isInterface() && !realServiceClass.isAssignableFrom(
                implementingClass)) {
            additionalImplementingClasses.add(realServiceClass);
        }
        return additionalImplementingClasses;
    }

}
