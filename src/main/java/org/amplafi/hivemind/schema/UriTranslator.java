package org.amplafi.hivemind.schema;

import java.net.URI;

import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;
/**
 * Translates strings in hivemind files into uris.
 * @author patmoore
 *
 */
public class UriTranslator implements Translator {

    /**
     * @see org.apache.hivemind.schema.Translator#translate(org.apache.hivemind.internal.Module, java.lang.Class, java.lang.String, org.apache.hivemind.Location)
     */
    @Override
    @SuppressWarnings({ "unused", "unchecked" })
    public Object translate(Module contributingModule, Class propertyType, String inputValue, Location location) {
        return URI.create(inputValue);
    }

}
