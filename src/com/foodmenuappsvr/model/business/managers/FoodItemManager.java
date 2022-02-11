package com.foodmenuappsvr.model.business.managers;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.foodmenuappsvr.model.business.exceptions.*;
import com.foodmenuappsvr.model.business.factory.ServiceFactory;
import com.foodmenu.model.domain.FoodItem;
import com.foodmenu.model.domain.User;
import com.foodmenuappsvr.model.services.exceptions.FoodItemServiceException;
import com.foodmenuappsvr.model.services.fooditemservice.IFoodItemService;

/**
 * @author Zach Stanfill
 * Adapted from Prof. Ishmael, MSSE670, Regis University
 */
public class FoodItemManager {
	
	private static Logger  LOGGER = Logger.getLogger(FoodItemManager.class);

	private User user;
	
	public FoodItemManager() {
		LOGGER.trace("MenuItemManager Default Constructor Called");
	}
	
	public FoodItemManager(User user) {
		LOGGER.trace("MenuItemManager Overloaded Constructor Called");
		this.user = user;
	}
	
	/** 
	 * Use Case : FoodItem-100
	 * Add New Food Item
	 */
	public boolean addNewFoodItem(FoodItem foodItem) throws ServiceLoadException, 
		FoodItemServiceException {
		LOGGER.trace("addNewFoodItem Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IFoodItemService foodItemSvc = (IFoodItemService)serviceFactory.getService("IFoodItemService");
		if(foodItemSvc.createFoodItemData(foodItem)) {
			return true;
		} else {
			return false;
		}
	}
	
	/** 
	 * Use Case : FoodItem-110
	 * Delete Existing Food Item
	 * @throws UserPrivilegesException 
	 */
	public boolean deleteFoodItem(FoodItem foodItem) throws ServiceLoadException, 
		FoodItemServiceException, UserPrivilegesException {
		LOGGER.trace("deleteFoodItem Called");
		
		if(this.user.getRole().equals("admin")) {
			ServiceFactory serviceFactory = new ServiceFactory();
			IFoodItemService foodItemSvc = (IFoodItemService)serviceFactory.getService("IFoodItemService");
			if(foodItemSvc.deleteFoodItemData(foodItem)) {
				return true;
			} else {
				return false;
			}
		} else {
			LOGGER.error(String.format("User %s isn't an admin, and therefore does not have the appropriate privileges to perform delete task!", user.getEmailAddress()));
			throw new UserPrivilegesException(String.format("User %s isn't an admin, and therefore does not have the \nappropriate privileges to perform delete task!", user.getEmailAddress()));
		}
	}
	
	/** 
	 * Use Case : FoodItem-120
	 * Retrieve All Food Item
	 */
	public ArrayList<FoodItem> retrieveAllFoodItems() throws ServiceLoadException, 
		FoodItemServiceException {
		LOGGER.trace("retrieveAllFoodItems Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IFoodItemService foodItemSvc = (IFoodItemService)serviceFactory.getService("IFoodItemService");
		return foodItemSvc.retrieveAllFoodItemData();
	}	
	
	/** 
	 * Use Case : FoodItem-130
	 * Retrieve Food Item
	 */
	public FoodItem retrieveFoodItem(String foodName) throws ServiceLoadException, 
		FoodItemServiceException {
		LOGGER.trace("retrieveFoodItem Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IFoodItemService foodItemSvc = (IFoodItemService)serviceFactory.getService("IFoodItemService");
		return foodItemSvc.retrieveFoodItemData(foodName);
	}	

}
