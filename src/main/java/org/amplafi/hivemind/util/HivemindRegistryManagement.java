/*
 * Created on Sep 26, 2006
 * Copyright 2006 by Patrick Moore
 */
package org.amplafi.hivemind.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.apache.hivemind.Registry;

/**
 * An {@link InvocationHandler} that makes sure that the {@link Registry Hivemind Registry}
 * is available.
 *
 * @author Patrick Moore
 */
public class HivemindRegistryManagement implements InvocationHandler {
    private static final String REGISTRY_KEY_PREFIX = "org.apache.tapestry.Registry:";
    private static final String TAPESTRY_SERVLET_NAME = "amplafi";
    private ServletContext context;
    private Registry registry;

    public static Registry getInstance(HttpServlet servlet) {
        return getInstance(servlet.getServletConfig());
    }

    public static Registry getInstance(ServletConfig servletConfig) {
        HivemindRegistryManagement inst = new HivemindRegistryManagement();
        inst.context = servletConfig.getServletContext();
        return (Registry) Proxy.newProxyInstance(Registry.class.getClassLoader(),
                new Class[] {Registry.class}, inst);
    }

    /**
     * @throws IllegalStateException
     */
    protected void initRegistry() throws IllegalStateException{
        registry = (Registry) this.context.getAttribute(REGISTRY_KEY_PREFIX+TAPESTRY_SERVLET_NAME);
        if ( registry == null ) {
            throw new IllegalStateException("No registry found named '"+
                    REGISTRY_KEY_PREFIX+TAPESTRY_SERVLET_NAME+"'");
        }
    }

    @SuppressWarnings("unused")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        initRegistry();
        return method.invoke(registry, args);
    }
    public static boolean isRegistryAvailable(Registry registry) {
        if ( registry == null) {
            return false;
        } else if ( Proxy.isProxyClass(registry.getClass())) {
            HivemindRegistryManagement reg = (HivemindRegistryManagement) Proxy.getInvocationHandler(registry);
            try {
                reg.initRegistry();
                return true;
            } catch (IllegalStateException e) {
                return false;
            }
        } else {
            return true;
        }
    }
}
