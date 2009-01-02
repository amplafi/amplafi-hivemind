package org.amplafi.hivemind.factory.servicessetter;

import org.amplafi.hivemind.annotations.InjectService;

/**
 * Test bean for autowiring.
 * @author andyhot
 */
public interface DummyBean {
    public FakeService getFactory1();
    public void setFactory1(FakeService factory1);

    public FakeService getFactory2();
    public void setFactory2(FakeService factory2);

    public FakeService getFactory3();

    public void setFactory3(FakeService factory3);

    /**
     * should not be called.
     * @param factory3
     */
    public void setNoGetter(FakeService factory3);

    public FakeService2 getTestService2();
    @InjectService("amplafi.testService2")
    public void setTestService2(FakeService2 factory);

    public FakeService2 getTestService3();
    @InjectService("amplafi.TestService3")
    public void setTestService3(FakeService2 factory);
}
