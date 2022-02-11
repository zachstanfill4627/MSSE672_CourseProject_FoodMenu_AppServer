package com.foodmenuappsvr.model.business.exceptions;

import org.apache.log4j.Logger;

public class UserPrivilegesException extends Exception {
	
	private static Logger LOGGER = Logger.getLogger(ServiceLoadException.class);

	private static final long serialVersionUID = 1234567L;
	
	public UserPrivilegesException(final String eMessage)  {
		super(eMessage);
		LOGGER.trace("UserPrivilegesException(String) Called");
	}
	
	public UserPrivilegesException(final String eMessage, final Throwable eNestedException)  {
		super(eMessage, eNestedException);
		LOGGER.trace("UserPrivilegesException(String, Throwable) Called");
	}

}
