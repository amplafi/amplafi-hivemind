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
package org.amplafi.hivemind.factory.servicessetter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.amplafi.hivemind.annotations.InjectService;
import org.amplafi.hivemind.annotations.NotService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.util.PropertyAdaptor;

import static org.apache.commons.lang.StringUtils.*;
import static org.apache.hivemind.util.PropertyUtils.*;


/**
 * Utility class that allows wiring up existing services (from a hivemind
 * registry) into a given object.
 *
 * @author andyhot
 */
public class ServicesSetterImpl implements ServicesSetter {

    private Module module;
    private Log log;
    /**
     * Key is concatenation of class and the excluded method names.
     */
    private ConcurrentMap<Class<?>, Set<String>> cachedAlwaysExcludedMap = new ConcurrentHashMap<Class<?>, Set<String>>();
    private ConcurrentMap<Class<?>, ConcurrentMap<String, String>> serviceMap = new ConcurrentHashMap<Class<?>, ConcurrentMap<String,String>>();

    public ServicesSetterImpl() {
    }

    public void setModule(Module module) {
        this.module = module;
    }

    @SuppressWarnings("unchecked")
    public void wire(Object obj) {
        wire(obj, Collections.EMPTY_LIST);
    }

    public void wire(Object obj, String... excludedProperties) {
        wire(obj, Arrays.asList(excludedProperties));
    }

    /**
     *
     * @see org.amplafi.hivemind.factory.servicessetter.ServicesSetter#wire(java.lang.Object, java.lang.Iterable)
     */
    @SuppressWarnings("unchecked")
    public void wire(Object obj, Iterable<String> excludedProperties) {
        if ( obj == null ) {
            return;
        }
        List<String> props = getWriteableProperties(obj);
        Set<String> alwaysExcludedCollection = cachedAlwaysExcludedMap.get(obj.getClass());

        if ( alwaysExcludedCollection != null) {
            props.removeAll(alwaysExcludedCollection);
            if ( getLog().isDebugEnabled()) {
                getLog().debug(obj.getClass()+": autowiring. Class has already been filtered down to "+props.size()+"properties. props={"+join(props, ",")+"}");
            }
        } else {
            if ( getLog().isDebugEnabled()) {
                getLog().debug(obj.getClass()+": autowiring for the first time. Class has at most "+props.size()+"properties. props={"+join(props, ",")+"}");
            }
            alwaysExcludedCollection = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
            cachedAlwaysExcludedMap.putIfAbsent(obj.getClass(), alwaysExcludedCollection);
            alwaysExcludedCollection = cachedAlwaysExcludedMap.get(obj.getClass());
        }
        for(String exclude: excludedProperties) {
            props.remove(exclude);
        }
        int wiredCount = 0;

        for (String prop : props) {
            PropertyAdaptor type = getPropertyAdaptor(obj, prop);
            Class propertyType = type.getPropertyType();
            if ( !isWireableClass(propertyType)) {
                // if it is a standard java class then lets exclude it.
                alwaysExcludedCollection.add(prop);
                continue;
            }
            // if it is not readable then, then we can't verify that
            // we are not overwriting non-null property.
            if ( !type.isReadable() || !type.isWritable()) {
                alwaysExcludedCollection.add(prop);
                continue;
            }
            // check to see if we have a service to offer before bothering
            // to checking if the property can be set. This avoids triggering
            // actions caused by calling the get/setters.
            Object srv = null;
            if ( type.getPropertyType() == Log.class) {
                // log is special.
                srv = LogFactory.getLog(obj.getClass());
            } else {
                ConcurrentMap<String, String> classServiceMap = serviceMap.get(obj.getClass());
                if (classServiceMap == null) {
                    serviceMap.putIfAbsent(obj.getClass(), new ConcurrentHashMap<String, String>());
                    classServiceMap = serviceMap.get(obj.getClass());
                }
                String serviceName = classServiceMap.get(prop);
                if ( serviceName == null) {
                    InjectService service;
                    try {
                        service = findInjectService(obj, type);
                    } catch(DontInjectException e) {
                        // do nothing
                        alwaysExcludedCollection.add(prop);
                        continue;
                    }

                    if ( service != null ) {
                        serviceName = service.value();
                        if ( StringUtils.isNotBlank(serviceName)) {
                            for (String attempt: new String[] {
                                serviceName,
                                serviceName +'.' +type.getPropertyName(),
                                serviceName +'.' +StringUtils.capitalize(type.getPropertyName())
                            }) {
                                try {
                                    srv = module.getService(attempt, propertyType);
                                    if ( srv != null ) {
                                        serviceName = attempt;
                                        break;
                                    }
                                }catch(Exception e) {
                                    // oh well... not around.
                                }
                            }
                        }
                    }
                    if ( srv != null ) {
                        classServiceMap.putIfAbsent(prop, serviceName);
                    } else {
                        // we looked but did not find... no need to look again.
                        classServiceMap.putIfAbsent(prop, "");
                    }
                } else if ( !serviceName.isEmpty()){
                    // we already found the service.
                    srv = module.getService(serviceName, propertyType);
                }
                if ( srv == null) {
                    try {
                        srv = module.getService(propertyType);
                    } catch (Exception e) {
                    }
                }
            }
            if (srv == null) {
                alwaysExcludedCollection.add(prop);
            } else if ( type.read(obj) == null) {
                // Doing the read check last avoids
                // triggering problems caused by lazy initialization and read-only properties.
                if ( type.getPropertyType().isAssignableFrom(srv.getClass())) {
                    type.write(obj, srv);
                    wiredCount++;
                } else {
                    // this is probably an error so we do not just add to the exclude list.
                    throw new ApplicationRuntimeException("Trying to set property "+obj.getClass()+"."+prop+" however, the property type="+type.getPropertyType()+
                        " is not a superclass or same class as "+srv.getClass()+". srv="+srv);
                }
            }
        }
        if ( getLog().isDebugEnabled()) {
            getLog().debug(obj.getClass()+": done autowiring. actual number of properties wired="+wiredCount+" excluded properties="+alwaysExcludedCollection);
        }
    }

    /**
     * @param propertyType
     * @return true class can be wired up as a service
     */
    public boolean isWireableClass(Class<?> propertyType) {
        if(
            // exclude primitives or other things that are not mockable in any form
            propertyType.isPrimitive() || propertyType.isAnnotation() || propertyType.isArray() || propertyType.isEnum()
            // generated classes
            || propertyType.getCanonicalName() == null
            // exclude java classes
            || propertyType.getPackage().getName().startsWith("java") ) {
            return false;
        } else {
            // exclude things that are explicitly labeled as not being injectable
            NotService notService = propertyType.getAnnotation(NotService.class);
            return notService == null;
        }
    }

    /**
     * @param obj
     * @param type
     * @param propertyType
     * @return
     * @throws DontInjectException
     */
    private InjectService findInjectService(Object obj, PropertyAdaptor type) throws DontInjectException {
        InjectService service;
        Class<?> propertyType = type.getPropertyType();
        String propertyAccessorMethodName = "set"+StringUtils.capitalize(type.getPropertyName());
        service = findServiceAnnotation(obj, propertyAccessorMethodName, propertyType);
        if ( service == null ) {
            propertyAccessorMethodName = "get"+StringUtils.capitalize(type.getPropertyName());
            service = findServiceAnnotation(obj, propertyAccessorMethodName);
        }
        if ( service == null && (propertyType == boolean.class || propertyType == Boolean.class)) {
            propertyAccessorMethodName = "is"+StringUtils.capitalize(type.getPropertyName());
            service = findServiceAnnotation(obj, propertyAccessorMethodName);
        }
        return service;
    }
    private NotService findNotService(Method m) throws DontInjectException {
        NotService notService = m.getAnnotation(NotService.class);
        if ( notService != null  ) {
            throw new DontInjectException();
        }
        return notService;
    }

    /**
     * @param obj
     * @param propertyAccessorMethodName
     * @param propertyType
     * @return
     * @throws DontInjectException
     */
    private InjectService findServiceAnnotation(Object obj, String propertyAccessorMethodName, Class<?>... propertyType) throws DontInjectException {
        // look for @InjectService
        InjectService service= null;

        try {
            Method m = obj.getClass().getMethod(propertyAccessorMethodName, propertyType);
            findNotService(m);
            service = m.getAnnotation(InjectService.class);
            if ( service == null ) {
                for(Class<?> cls: obj.getClass().getInterfaces()) {
                    try {
                        m = cls.getMethod(propertyAccessorMethodName, propertyType);
                        findNotService(m);
                        service = m.getAnnotation(InjectService.class);
                        if ( service != null ) {
                            break;
                        }
                    } catch(NoSuchMethodException e) {

                    }
                }
            }
        } catch(NoSuchMethodException e) {

        }
        return service;
    }
    /**
     * @param log the log to set
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * @return the log
     */
    public Log getLog() {
        return log;
    }
    private static class DontInjectException extends Exception {

    }
}
