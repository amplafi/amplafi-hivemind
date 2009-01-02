package org.amplafi.hivemind.factory.servicessetter;

import org.apache.hivemind.ServiceImplementationFactory;
import org.apache.hivemind.ServiceImplementationFactoryParameters;

/**
 * Factory for {@link ServicesSetter}.
 * 
 * @author andyhot
 */
public class ServicesSetterFactory implements ServiceImplementationFactory {

    public Object createCoreServiceImplementation(
            ServiceImplementationFactoryParameters params) {
        ServicesSetter ss = new ServicesSetterImpl();
        ss.setModule(params.getInvokingModule());
        return ss;
    }

}
