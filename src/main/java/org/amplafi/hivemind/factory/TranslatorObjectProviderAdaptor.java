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
package org.amplafi.hivemind.factory;

import org.apache.commons.logging.Log;
import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.schema.Translator;
import org.apache.hivemind.service.ObjectProvider;

/**
 * Bridge between the ObjectProvider interface which can be used to set
 * a service properties, and Translator which is used to translate values in contributions.
 *
 * Seems very odd that a bridge does not already exist. (so it probably does :-P )
 *
 * To use:
 * <set-object translator:uri:member/Home"/>
 *
 * where "uri" is the translator selected. If no translator is provided then the "smart" translator is used.
 * (but this is the equivalent of <set property="propertyName"  value="member/Home"/>
 *
 * @author patmoore
 *
 */
public class TranslatorObjectProviderAdaptor implements Translator, ObjectProvider {
    private Log log;
    /**
     * @see org.apache.hivemind.schema.Translator#translate(org.apache.hivemind.internal.Module, java.lang.Class, java.lang.String, org.apache.hivemind.Location)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object translate(Module contributingModule, Class propertyType, String inputValue, Location location) {
        if ( inputValue == null ) {
            return null;
        }
        int index = inputValue.indexOf(":");
        String translatorPrefix;
        String translatedValue;
        if ( index <0 ) {
            translatorPrefix = "smart";
            translatedValue = inputValue;
        } else {
            translatorPrefix  = inputValue.substring(0, index);
            translatedValue = inputValue.substring(index+1);
        }
        Translator translator = contributingModule.getTranslator(translatorPrefix);
        Object result = null;
        if ( translator == null) {
            contributingModule.getErrorHandler().error(getLog(), translatorPrefix+": is not a registered translator. Check the hivemind.Translators contributions. Full input string "+inputValue, location, null);
        } else {
            result = translator.translate(contributingModule, propertyType, translatedValue, location);
        }
        return result;
    }

    /**
     * @see org.apache.hivemind.service.ObjectProvider#provideObject(org.apache.hivemind.internal.Module, java.lang.Class, java.lang.String, org.apache.hivemind.Location)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object provideObject(Module contributingModule, Class propertyType, String locator, Location location) {
        return this.translate(contributingModule, propertyType, locator, location);
    }

    /**
     * @param log the log to set
     */
    public void setLog(Log log) {
        this.log = log;
    }

    /**
     * @return the log
     */
    public Log getLog() {
        return log;
    }

}
