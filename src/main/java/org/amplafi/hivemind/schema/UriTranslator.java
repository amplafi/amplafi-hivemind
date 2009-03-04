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
