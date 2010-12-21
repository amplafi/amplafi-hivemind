package org.amplafi.hivemind.factory;

import org.apache.hivemind.Location;
import org.apache.hivemind.internal.Module;
import org.apache.hivemind.service.ObjectProvider;

/**
 * For some reason cannot get a null injected via hivemind.
 * @author patmoore
 *
 */
public class NullProvider implements ObjectProvider {

	@Override
	public Object provideObject(Module contributingModule, Class propertyType,
			String locator, Location location) {
		return null;
	}

}
