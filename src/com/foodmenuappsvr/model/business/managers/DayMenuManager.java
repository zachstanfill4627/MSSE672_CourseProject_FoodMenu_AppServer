package com.foodmenuappsvr.model.business.managers;

import java.util.ArrayList;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.foodmenuappsvr.model.business.exceptions.*;
import com.foodmenuappsvr.model.business.factory.ServiceFactory;
import com.foodmenu.model.domain.DayMenu;
import com.foodmenu.model.domain.User;
import com.foodmenuappsvr.model.services.daymenuservice.IDayMenuService;
import com.foodmenuappsvr.model.services.exceptions.*;

/**
 * @author Zach Stanfill
 * Adapted from Prof. Ishmael, MSSE670, Regis University
 */
public class DayMenuManager {
	
	private static Logger  LOGGER = Logger.getLogger(DayMenuManager.class);

	private User user;
	
	public DayMenuManager() {
		LOGGER.trace("DayMenuManager Default Constructor Called");
	}
	
	public DayMenuManager(User user) {
		LOGGER.trace("DayMenuManager Overloaded Constructor Called");
		this.user = user;
		LOGGER.debug(String.format("User set to %s", user.getEmailAddress()));
	}
	
	/** 
	 * Use Case : DayMenu-300
	 * Add New Day Menu
	 */
	public boolean addNewDayMenu(DayMenu dayMenu) throws ServiceLoadException, 
		DayMenuServiceException {
		LOGGER.trace("addNewDayMenu Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IDayMenuService dayMenuSvc = (IDayMenuService)serviceFactory.getService("IDayMenuService");
		if(dayMenuSvc.createDayMenuData(dayMenu)) {
			return true;
		} else {
			return false;
		}
	}

	/** 
	 * Use Case : DayMenu-310
	 * Delete Existing Day Menu
	 * @throws UserPrivilegesException 
	 */
	public boolean deleteDayMenu(DayMenu dayMenu) throws ServiceLoadException, 
		DayMenuServiceException, UserPrivilegesException {
		LOGGER.trace("deleteNewDayMenu Called");
		
		if(this.user.getRole().equals("admin")) {
			ServiceFactory serviceFactory = new ServiceFactory();
			IDayMenuService dayMenuSvc = (IDayMenuService)serviceFactory.getService("IDayMenuService");
			if(dayMenuSvc.deleteDayMenuData(dayMenu)) {
				return true;
			} else {
				return false;
			}
		} else {
			LOGGER.error(String.format("User %s isn't an admin, and therefore does not have the appropriate privileges to perform delete task", user.getEmailAddress()));
			throw new UserPrivilegesException(String.format("User %s isn't an admin, and therefore does not have the \nappropriate privileges to perform delete task!", user.getEmailAddress()));
		}
	}
	
	/** 
	 * Use Case : DayMenu-320
	 * Retrieve All Day Menus
	 */
	public ArrayList<DayMenu> retrieveAllDayMenus() throws ServiceLoadException, 
		DayMenuServiceException, MenuItemServiceException, FoodItemServiceException {
		LOGGER.trace("retrieveAllDayMenus Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IDayMenuService dayMenuSvc = (IDayMenuService)serviceFactory.getService("IDayMenuService");		
		return dayMenuSvc.retrieveAllDayMenuData();
	}
	
	/** 
	 * Use Case : DayMenu-330
	 * Retrieve Day Menu
	 */
	public DayMenu retrieveDayMenu(Calendar date) throws ServiceLoadException, 
		DayMenuServiceException, MenuItemServiceException, FoodItemServiceException {
		LOGGER.trace("retrieveDayMenu Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IDayMenuService dayMenuSvc = (IDayMenuService)serviceFactory.getService("IDayMenuService");
		return dayMenuSvc.retrieveDayMenuData(date);
	}
}
