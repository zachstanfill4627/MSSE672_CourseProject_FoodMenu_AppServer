package com.foodmenuappsvr.model.business.exceptions;

import org.apache.log4j.Logger;

/**
 * @author Zach Stanfill
 * Adapted from Prof. Ishmael, MSSE670, Regis University
 */
@SuppressWarnings("serial")
public class ServiceLoadException extends Exception {
	
	private static Logger LOGGER = Logger.getLogger(ServiceLoadException.class);
	
	public ServiceLoadException(final String svcMessage, final Throwable svcNestedException) {
        super(svcMessage, svcNestedException);
        LOGGER.trace("ServiceLoadException(String, Throwable) Called");
    }

}
