package com.foodmenuappsvr.model.services.exceptions;

import org.apache.log4j.Logger;

public class UserServiceException extends Exception {
	
	static Logger LOGGER = Logger.getLogger(UserServiceException.class);

	private static final long serialVersionUID = 1234567L;
	
	public UserServiceException(final String eMessage)  {
		super(eMessage);
		LOGGER.trace("UserServiceException(String) Called");
	}
	
	public UserServiceException(final String eMessage, final Throwable eNestedException)  {
		super(eMessage, eNestedException);
		LOGGER.trace("UserServiceException(String, String) Called");
	}

}
