/*
 * Created on Oct 19, 2007
 * Copyright 2006 by Patrick Moore
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
        try {
            return m.invoke(underlyingObject, args);
        } catch (InvocationTargetException e ) {
            // to reduce unnecessary nesting.
            throw e.getCause();
        }
    }
    /**
     * @param method
     * @param underlyingClass
     * @return method or the underlyingClass's method with the same signatures.
     */
    private Method getActualMethod(Method method, Class<?> underlyingClass) {
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
        return underlyingService;
    }
}
