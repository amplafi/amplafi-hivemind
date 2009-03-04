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

package org.amplafi.hivemind.util;

import org.apache.hivemind.Element;
import org.apache.hivemind.schema.SchemaProcessor;
import org.apache.hivemind.schema.rules.BaseRule;
import org.apache.hivemind.util.InstanceCreationUtils;

/**
 * Hivemind rule to create instances of an object
 * based on an attribute in the hivemind schema rule.
 * @author Patrick Moore
 */
public class AttributeBasedCreateObjectRule extends BaseRule {
    private String attribute;

    private String defaultClass;

    public AttributeBasedCreateObjectRule() {
    }

    public AttributeBasedCreateObjectRule(String attribute) {
        this.attribute = attribute;
    }

    /**
     * Creates the new object and pushes it onto the processor's stack. If the
     * object implement {@link org.apache.hivemind.LocationHolder} then the
     * {@link org.apache.hivemind.Location} of the element is assigned to the
     * object.
     */
    @Override
    public void begin(SchemaProcessor processor, Element element) {
        String className = element.getAttributeValue(attribute);
        if (className == null) {
            className = defaultClass;
        }
        Object object = InstanceCreationUtils.createInstance(processor
                .getDefiningModule(), className, element.getLocation());

        processor.push(object);
    }

    /**
     * Pops the object off of the processor's stack.
     */
    @Override
    @SuppressWarnings("unused")
    public void end(SchemaProcessor processor, Element element) {
        processor.pop();
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setDefaultClass(String defaultClass) {
        this.defaultClass = defaultClass;
    }

    public String getDefaultClass() {
        return defaultClass;
    }
}
