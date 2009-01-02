/*
 * Created on May 28, 2007
 * Copyright 2006 by Patrick Moore
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
