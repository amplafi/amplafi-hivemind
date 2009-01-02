package org.amplafi.hivemind.factory.servicessetter;

import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;

import org.amplafi.hivemind.factory.servicessetter.ServicesSetter;
import org.amplafi.hivemind.util.HivemindHelper;
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
