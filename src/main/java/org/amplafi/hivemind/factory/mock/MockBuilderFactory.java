/*
 * Created on Jan 17, 2007
 * Copyright 2006 by Patrick Moore
 */
package org.amplafi.hivemind.factory.mock;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceInterceptorFactory;
import org.apache.hivemind.internal.Module;

/**
 * Builds mock services for testing, if the actual service does not exist.
 *
 * @author Patrick Moore
 */
public interface MockBuilderFactory
        extends ServiceImplementationFactory, ServiceInterceptorFactory {
    /**
     * @param builderFactory the builderFactory to set
     */
    public void setBuilderFactory(ServiceImplementationFactory builderFactory);

    /**
     * @return the builderFactory
     */
    public ServiceImplementationFactory getBuilderFactory();

    public Map<Class<?>, Object> getMockMap();

    /**
     * every class in the set will be mocked even if there is an existing
     * implementation.
     *
     * @param mockOverride
     */
    public void setMockOverride(Set<Class<?>> mockOverride);

    /**
     * every class in the set will be mocked even if there is an existing
     * implementation.
     *
     * @return set of classes that will always be mocked.
     */
    public Set<Class<?>> getMockOverride();

    public void setMockOverride(Class<?>... classes);

    public void addMockOverride(Class<?>... classes);

    /**
     * every class not in the set will be mocked.
     *
     * @param dontMockOverride
     */
    public void setDontMockOverride(Set<Class<?>> dontMockOverride);

    /**
     * every class not in the set will be mocked.
     * @return the set of classes not to mock
     */
    public Set<Class<?>> getDontMockOverride();

    public void setDontMockOverride(Class<?>... classes);

    public void addDontMockOverride(Class<?>... classes);

    /**
     * replay the EasyMock at the serviceInterface.
     *
     * @param serviceInterfaces
     */
    public void replay(Class<?>... serviceInterfaces);

    public void replay();

    /**
     * verify the EasyMock at the serviceInterface.
     *
     * @param serviceInterfaces
     */
    public void verify(Class<?>... serviceInterfaces);

    public void verify();

    /**
     * reset the EasyMock for the serviceInterface.
     *
     * @param serviceInterfaces
     */
    public void reset(Class<?>... serviceInterfaces);

    /**
     * reset all mocks and clear the dontMockOverride and the
     * mockOverride sets for this thread.
     */
    public void reset();

    /**
     * Used for classes that where not created by hivemind, but we still want to have all
     * service class objects accessed by this object be mocks.
     *
     * @param objectToMockWrap
     * @see #getDontMockOverride()
     * @see #getMockOverride()
     */
    public void wrapWithMocks(Object objectToMockWrap);

    /**
     * In interceptor factory mode, only knows how to create an interceptor for
     * BuilderFactory.
     * @param stack
     * @param invokingModule
     * @param parameters
     *
     * @see org.apache.hivemind.ServiceInterceptorFactory#createInterceptor(org.apache.hivemind.InterceptorStack,org.apache.hivemind.internal.Module,java.util.List)
     */
    public void createInterceptor(InterceptorStack stack, Module invokingModule, List<?> parameters);

    /**
     * used to get the mock objects so they can be programmed.
     *
     * @param <T>
     * @param implementationClass
     * @return the implementation instance (usually a mock object)
     */
    public <T> T getImplementation(Class<T> implementationClass);

    /**
     * Because of MockSwitcher the object that external tests have is not actually a mock in some cases.
     *
     * @param mock an EasyMock or a Proxy with a MockSwitcher as the Proxy handler.
     * @return actual mock object
     */
    public Object getMock(final Object mock);

    /**
     * @param serviceClass
     * @return if the interface is being mocked.
     */
    public boolean isBeingMocked(Class<?> serviceClass);

    /**
     * Sets the mode factory will work in.
     *
     * @param shareMocksAcrossThreads whether we have to share the same mocks across all threads
     *                                or hold to the thread-separate principle.
     */
    public void setShareMocksAcrossThreads(boolean shareMocksAcrossThreads);
}
