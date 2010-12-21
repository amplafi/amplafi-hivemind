/**
 * Copyright 2006-2011 by Amplafi. All rights reserved.
 * Confidential.
 */
package org.apache.hivemind.impl;

import org.apache.commons.logging.Log;
import org.apache.hivemind.Location;

/**
 * Logs message and EXITS to guarentee the error is noticed.
 * @author patmoore
 *
 */
public class HarshErrorHandler extends DefaultErrorHandler {

    /**
     * @see org.apache.hivemind.ErrorHandler#error(org.apache.commons.logging.Log, java.lang.String, org.apache.hivemind.Location, java.lang.Throwable)
     */
    @Override
    public void error(Log log, String message, Location location, Throwable cause) {
        super.error(log, message, location, cause);
        System.exit(1);
    }

}
