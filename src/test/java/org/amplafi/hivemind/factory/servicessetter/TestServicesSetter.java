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

import static org.easymock.EasyMock.*;

import org.amplafi.hivemind.util.HivemindHelper;

import com.sworddance.core.ServicesSetter;

import org.apache.hivemind.Registry;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


/**
 * Test for {@link ServicesSetter}.
 * @author andyhot
 */
@Test
public class TestServicesSetter extends Assert {
    private ServicesSetter setter;
    private Registry registry;

    @BeforeClass
    protected void setupRegistry() throws Exception {
        registry = HivemindHelper.createFrameworkRegistry("/org/amplafi/hivemind/factory/servicessetter/TestServicesSetter-hivemodule.xml");
        setter = (ServicesSetter) registry.getService(ServicesSetter.class);
    }

    @Test
    public void testWire() {
        DummyBean bean = createMock(DummyBean.class);
        FakeService fakeFake = createMock(FakeService.class);
        expect(bean.getFactory1()).andReturn(fakeFake);
        expect(bean.getFactory2()).andReturn(null);
        bean.setFactory2(isA(FakeService.class));
        expectLastCall();
        expect(bean.getFactory3()).andReturn(null);
        bean.setFactory3(isA(FakeService.class));
        expectLastCall();
        replay(fakeFake, bean);
        setter.wire(bean);

        verify(fakeFake, bean);
    }

    @Test
    public void testWireSome() {
        DummyBean bean = createMock(DummyBean.class);
        FakeService fakeFake = createMock(FakeService.class);
        expect(bean.getFactory1()).andReturn(fakeFake);
        expect(bean.getFactory2()).andReturn(null);
        bean.setFactory2(isA(FakeService.class));
        expectLastCall();
        replay(fakeFake, bean);
        setter.wire(bean, "factory3");

        verify(fakeFake, bean);
    }


}
