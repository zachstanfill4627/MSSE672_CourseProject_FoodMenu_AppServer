package com.foodmenuappsvr.model.business.managers;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.foodmenuappsvr.model.business.exceptions.*;
import com.foodmenuappsvr.model.business.factory.ServiceFactory;
import com.foodmenu.model.domain.MenuItem;
import com.foodmenu.model.domain.User;
import com.foodmenuappsvr.model.services.exceptions.*;
import com.foodmenuappsvr.model.services.menuitemservice.IMenuItemService;

/**
 * @author Zach Stanfill
 * Adapted from Prof. Ishmael, MSSE670, Regis University
 */
public class MenuItemManager {
	
	private static Logger  LOGGER = Logger.getLogger(MenuItemManager.class);

	private User user;
	
	public MenuItemManager() {
		LOGGER.trace("MenuItemManager Default Constructor Called");
	}
	
	public MenuItemManager(User user) {
		LOGGER.trace("MenuItemManager Oveerloaded Constructor Called");
		this.user = user;
	}
	
	/** 
	 * Use Case : MenuItem-200
	 * Add New Menu Item
	 */
	public boolean addNewMenuItem(MenuItem menuItem) throws ServiceLoadException, 
		MenuItemServiceException {
		LOGGER.trace("addNewMenuItem Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IMenuItemService menuItemSvc = (IMenuItemService)serviceFactory.getService("IMenuItemService");
		if(menuItemSvc.createMenuItemData(menuItem)) {
			return true;
		} else {
			return false;
		}
	}

	/** 
	 * Use Case : MenuItem-210
	 * Delete Existing Menu Item
	 * @throws UserPrivilegesException 
	 */
	public boolean deleteMenuItem(MenuItem menuItem) throws ServiceLoadException, 
		MenuItemServiceException, UserPrivilegesException {
		LOGGER.trace("deleteMenuItem Called");
		
		if(this.user.getRole().equals("admin")) {
			ServiceFactory serviceFactory = new ServiceFactory();
			IMenuItemService menuItemSvc = (IMenuItemService)serviceFactory.getService("IMenuItemService");
			if(menuItemSvc.deleteMenuItemData(menuItem)) {
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
	 * Use Case : MenuItem-220
	 * Retrieve All Menu Items
	 * @throws FoodItemServiceException 
	 */
	public ArrayList<MenuItem> retrieveAllMenuItems() throws ServiceLoadException, 
		MenuItemServiceException, FoodItemServiceException {
		LOGGER.trace("retrieveAllMenuItems Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IMenuItemService menuItemSvc = (IMenuItemService)serviceFactory.getService("IMenuItemService");
		return menuItemSvc.retrieveAllMenuItemData();
	}
	
	/** 
	 * Use Case : MenuItem-230
	 * Retrieve Menu Item
	 * @throws FoodItemServiceException 
	 */
	public MenuItem retrieveMenuItem(String mealName) throws ServiceLoadException, 
		MenuItemServiceException, FoodItemServiceException {
		LOGGER.trace("retrieveMenuItem Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IMenuItemService menuItemSvc = (IMenuItemService)serviceFactory.getService("IMenuItemService");
		return menuItemSvc.retrieveMenuItemData(mealName);
	}
}
