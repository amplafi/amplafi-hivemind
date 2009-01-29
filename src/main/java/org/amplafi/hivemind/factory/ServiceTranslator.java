package org.amplafi.hivemind.factory;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.HiveMind;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;

/**
 * existing ServiceTranslator is messed up and does not pass the propertyType
 * @author patmoore
 *
 */
public class ServiceTranslator implements Translator
{
    public static final ServiceTranslator INSTANCE = new ServiceTranslator();
    /**
     * Returns null if the input is null or empty. Returns the service with the given name
     * otherwise. Will log an error and return null if an exception is thrown. If the input value is
     * not qualified, the contributing module's id is added as a prefix.
     */
    public Object translate(Module contributingModule, Class propertyType, String inputValue,
            Location location)
    {
        Object service;
        if (HiveMind.isBlank(inputValue)) {
            return null;
        }

        try {
            service = contributingModule.getService(inputValue, propertyType);
        } catch (ApplicationRuntimeException e ) {
            try {
                service = contributingModule.getService(inputValue, Object.class);
            } catch ( Exception ne ) {
                throw e;
            }
        }
        return service;
    }

}