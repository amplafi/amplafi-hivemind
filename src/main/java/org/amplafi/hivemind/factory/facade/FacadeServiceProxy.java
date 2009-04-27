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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handles situation where underlying service does not actually implement
 * the facade's interface but does implement methods with the same signature.
 *
 * Handles case where hivemind service has as it's 'interface' a class not a true interface.
 *
 * @author Patrick Moore
 */
public class FacadeServiceProxy implements InvocationHandler {

    private Object underlyingService;
    private ConcurrentMap<Class<?>, ConcurrentMap<Method, Method>> callingMethodInvokedMethodMap = new ConcurrentHashMap<Class<?>, ConcurrentMap<Method, Method>>();
    public FacadeServiceProxy() {

    }
    public FacadeServiceProxy(Object underlyingService) {
        this.underlyingService = underlyingService;
    }
    @Override
    @SuppressWarnings("unused")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object underlyingObject = getUnderlyingService();
        // get fresh because the getUnderlyingService() method may be overridden by subclasses
        // that alter the class based on circumstances.
        Class<?> underlyingClass = underlyingObject.getClass();
        Method m = getMethodToUse(method, underlyingClass);
        try {
            return m.invoke(underlyingObject, args);
        } catch (InvocationTargetException e ) {
            // to reduce unnecessary nesting.
            throw e.getCause();
        }
    }
    /**
     * Look for any cached method matching the signature requested for the class.
     *
     * Enables handling the "duck-typing". Underlying object may not actually implement the interface/extend the class that 'method'
     * applies to. So need to find the method object with same signature within underlyingClass.
     * @param method
     * @param underlyingClass
     * @return the method that should be used.
     */
    protected Method getMethodToUse(Method method, Class<?> underlyingClass) {
        ConcurrentMap<Method, Method> methodMap = callingMethodInvokedMethodMap.get(underlyingClass);
        if ( methodMap == null) {
            callingMethodInvokedMethodMap.putIfAbsent(underlyingClass, new ConcurrentHashMap<Method, Method>());
            // because another thread might beat this thread to inserting a new map.
            methodMap = callingMethodInvokedMethodMap.get(underlyingClass);
        }
        Method m = methodMap.get(method);
        if (m == null ) {
            methodMap.putIfAbsent(method, getActualMethod(method, underlyingClass));
            m = methodMap.get(method);
        }
        return m;
    }
    /**
     * @param method
     * @param underlyingClass
     * @return method or the underlyingClass's method with the same signatures.
     */
    protected Method getActualMethod(Method method, Class<?> underlyingClass) {
        Method m;
        if ( method.getDeclaringClass().isAssignableFrom(underlyingClass) ) {
            m = method;
        } else {
            // handle the case where hivemind has generated a interface because
            // the service interface was really a class.
            try {
                m = underlyingClass.getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException(
                    underlyingClass.getName()+" does not have a "
                    +method.getName()
                    +"("+Arrays.deepToString(method.getParameterTypes())+")");
            } catch(SecurityException e) {
                throw new IllegalArgumentException("Not permitted access to "+
                    underlyingClass.getName()+"."+method.getName()
                    +"("+Arrays.deepToString(method.getParameterTypes())+")", e);
            }
        }
        return m;
    }
    public void setUnderlyingService(Object underlyingService) {
        this.underlyingService = underlyingService;
    }

    public Object getUnderlyingService() {
        if ( this.underlyingService == null) {
            this.underlyingService = createUnderlyingService();
        }
        return underlyingService;
    }
    /**
     * @return by default return null if trying to create the service.
     */
    protected Object createUnderlyingService() {
        return null;
    }
}
