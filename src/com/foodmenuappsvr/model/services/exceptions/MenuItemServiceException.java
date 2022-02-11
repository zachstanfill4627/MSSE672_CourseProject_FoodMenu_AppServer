package com.foodmenuappsvr.model.services.exceptions;

import org.apache.log4j.Logger;

public class MenuItemServiceException extends Exception {
	
	static Logger LOGGER = Logger.getLogger(MenuItemServiceException.class);

	private static final long serialVersionUID = 1234567L;
	
	public MenuItemServiceException(final String eMessage)  {
		super(eMessage);
		LOGGER.trace("MenuItemServiceException(String) Called");
	}
	
	public MenuItemServiceException(final String eMessage, final Throwable eNestedException)  {
		super(eMessage, eNestedException);
		LOGGER.trace("MenuItemServiceException(String, String) Called");
	}

}
