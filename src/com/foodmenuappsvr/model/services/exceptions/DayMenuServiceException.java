package com.foodmenuappsvr.model.services.exceptions;

import org.apache.log4j.Logger;

public class DayMenuServiceException extends Exception {
	
	static Logger LOGGER = Logger.getLogger(DayMenuServiceException.class);

	private static final long serialVersionUID = 1234567L;
	
	public DayMenuServiceException(final String eMessage)  {
		super(eMessage);
		LOGGER.trace("DayMenuServiceException(String) Called");
	}
	
	public DayMenuServiceException(final String eMessage, final Throwable eNestedException)  {
		super(eMessage, eNestedException);
		LOGGER.trace("DayMenuServiceException(String, String) Called");
	}

}
