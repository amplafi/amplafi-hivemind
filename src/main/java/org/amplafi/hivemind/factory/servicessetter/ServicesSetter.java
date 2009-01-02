package org.amplafi.hivemind.factory.servicessetter;


import org.apache.hivemind.internal.Module;

/**
 * Utility class that allows wiring up existing services (from a hivemind
 * registry) into a given object.
 *
 * @author andyhot
 */
public interface ServicesSetter {
    public void setModule(Module module);

    public void wire(Object obj);

    public void wire(Object obj, String... excludedProperties);

    /**
     * wire object in question to hivemind services.
     *
     * Exclude:
     * <ul>
     * <li>properties listed,</li>
     * <li>properties whose class is annotated with @NotService,</li>
     * <li>properties whose class,</li>
     * is in a package starting with "java",</li>
     * <li>primitive properties</li>
     * <li>properties that are not both readable and writable</li>
     * <li>properties that are already set (getter returns non-null)</li>
     * </ul>
     *
     * If there is only one service that matches the property type then it is wired no problem.
     * If there is multiple services then if the service name matches the Property name
     * (ignoring the capitalization of the first letter) then the service with the matching
     * property name is injected.
     * @param obj
     * @param excludedProperties
     */
    public void wire(Object obj, Iterable<String> excludedProperties);
}
