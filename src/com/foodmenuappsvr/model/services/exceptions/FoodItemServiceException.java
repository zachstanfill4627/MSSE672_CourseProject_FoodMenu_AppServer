package com.foodmenuappsvr.model.services.exceptions;

import org.apache.log4j.Logger;

public class FoodItemServiceException extends Exception {
	
	static Logger LOGGER = Logger.getLogger(FoodItemServiceException.class);

	private static final long serialVersionUID = 1234567L;
	
	public FoodItemServiceException(final String eMessage)  {
		super(eMessage);
		LOGGER.trace("FoodItemServiceException(String) Called");
	}
	
	public FoodItemServiceException(final String eMessage, final Throwable eNestedException)  {
		super(eMessage, eNestedException);
		LOGGER.trace("FoodItemServiceException(String, String) Called");
	}

}
