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

package org.amplafi.hivemind.factory.mock;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.amplafi.hivemind.factory.mock.MockBuilderFactory;
import org.amplafi.hivemind.factory.mock.MockBuilderFactoryImpl;
import org.amplafi.hivemind.factory.mock.MockBuilderFactoryImpl.MockSwitcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.InterceptorStack;
import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;
import org.apache.hivemind.impl.InterceptorStackImpl;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.internal.ServicePoint;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.getCurrentArguments;
import static org.easymock.EasyMock.isA;
import org.easymock.IAnswer;
import static org.easymock.classextension.EasyMock.createControl;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import org.easymock.classextension.IMocksControl;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * tests the {@link MockBuilderFactory} to make sure that test function works well.
 * @author Patrick Moore
 */
@Test
public class TestMockBuilderFactory extends Assert {
    @DataProvider(name="createMockFactory")
    protected Object[][] createMockFactory() {
        MockBuilderFactory factory = new MockBuilderFactoryImpl(false);
        factory.setBuilderFactory(createMock(ServiceImplementationFactory.class));
        return new Object[][] {
                new Object[] { factory }
        };
    }

    /**
     * simple test to make sure that the {@link MockBuilderFactory} can function in a minimal
     * way as an interceptor.
     * @param factory
     */
    @SuppressWarnings("unchecked")
    @Test(dataProvider="createMockFactory")
    public void testAsInterceptorFactory(MockBuilderFactory factory) {
        // TODO have way to get testing logger.
        Log log = LogFactory.getLog(this.getClass());
        ServicePoint servicePoint = getServicePoint();
        final Class dependentServiceClass = ServiceImplementationFactory.class;
        List parameters = createMock(List.class);
        Module invokingModule = getModule(dependentServiceClass);
        ServicePoint someServicePoint = createMock(ServicePoint.class);
        expect(invokingModule.getServicePoint("someService")).andReturn(someServicePoint);
        replay(invokingModule, someServicePoint);

        List factoryParametersList = new ArrayList();
        List fakeParameter = createMock(List.class);
        replay(fakeParameter);
        // add something to the list that will complain if used.
        factoryParametersList.add(fakeParameter);
        ServiceImplementationFactoryParameters factoryParameters = createMock(ServiceImplementationFactoryParameters.class);
        expect(factoryParameters.getInvokingModule()).andReturn(invokingModule).anyTimes();
        expect(factoryParameters.getFirstParameter()).andReturn(factoryParametersList.get(0)).anyTimes();
        expect(factoryParameters.getParameters()).andReturn(factoryParametersList).anyTimes();
        expect(factoryParameters.getServiceInterface()).andReturn(SomeService.class).anyTimes();
        expect(factoryParameters.getServiceId()).andReturn("someService");
        replay(factoryParameters);
        ServiceImplementationFactory rootService = createMock(ServiceImplementationFactory.class);
        final SomeService realCreatedService = createMock(SomeService.class);
        expect(rootService.createCoreServiceImplementation(isA(ServiceImplementationFactoryParameters.class))).
            andAnswer(new IAnswer<Object>() {

                public Object answer() throws Throwable {
                    ServiceImplementationFactoryParameters object = (ServiceImplementationFactoryParameters) getCurrentArguments()[0];

                    // make sure being passed the proxy
                    assertTrue(Proxy.isProxyClass(object.getInvokingModule().getClass()), "Did not get passed a module proxy");
                    assertTrue(object.getInvokingModule().containsService(dependentServiceClass),
                            dependentServiceClass+": module does not have service with this interface");
//        TODO            List dependentService = (List) object.getInvokingModule().getService(dependentServiceClass);
                    return realCreatedService;
                }

            });
        replay(rootService, realCreatedService);

        InterceptorStack stack = new InterceptorStackImpl(log, servicePoint, rootService);

        // create the interceptor
        factory.createInterceptor(stack, invokingModule, parameters);
        ServiceImplementationFactory intercepted = (ServiceImplementationFactory) stack.peek();

        assertNotNull(intercepted);

        SomeService result = (SomeService) intercepted.createCoreServiceImplementation(factoryParameters);
        MockSwitcher switcher = (MockSwitcher) Proxy.getInvocationHandler(result);
        assertSame(switcher.getRealService(), realCreatedService);
        assertSame(switcher.getUnderlyingService(), realCreatedService);

        // now tell factory that we always want the mock.
        factory.getMockOverride().add(SomeService.class);
        assertSame(switcher.getRealService(), realCreatedService);
        assertNotSame(switcher.getUnderlyingService(), realCreatedService);
    }
    /**
     * @return
     */
    private Module getModule(Class<?> dependentServiceClass) {
        Module invokingModule = createMock(Module.class);
        ClassResolver classResolver = createMock(ClassResolver.class);
        expect(classResolver.getClassLoader()).andStubReturn(this.getClass().getClassLoader());
        expect(invokingModule.getClassResolver()).andStubReturn(classResolver);
        expect(invokingModule.containsService(dependentServiceClass)).andStubReturn(false);
        replay(classResolver);
        return invokingModule;
    }
    /**
     * @return
     */
    private ServicePoint getServicePoint() {
        IMocksControl control = createControl();
        ServicePoint servicePoint = control.createMock(ServicePoint.class);
        expect(servicePoint.getServiceInterface()).andStubReturn(ServiceImplementationFactory.class);
        control.replay();
        return servicePoint;
    }
    /**
     * a fake service interface.
     * @author Patrick Moore
     */
    public interface SomeService {
    }
}
