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
