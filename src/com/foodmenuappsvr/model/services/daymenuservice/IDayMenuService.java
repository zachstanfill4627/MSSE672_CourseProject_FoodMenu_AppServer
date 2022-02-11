package com.foodmenuappsvr.model.services.daymenuservice;

import java.util.ArrayList;
import java.util.Calendar;

import com.foodmenu.model.domain.DayMenu;
import com.foodmenuappsvr.model.services.IService;
import com.foodmenuappsvr.model.services.exceptions.DayMenuServiceException;
import com.foodmenuappsvr.model.services.exceptions.FoodItemServiceException;
import com.foodmenuappsvr.model.services.exceptions.MenuItemServiceException;

public interface IDayMenuService extends IService {
	
	public boolean createDayMenuData(DayMenu dayMenu) throws DayMenuServiceException;
	public DayMenu retrieveDayMenuData(Calendar date) throws DayMenuServiceException, MenuItemServiceException, FoodItemServiceException;
	public ArrayList<DayMenu> retrieveAllDayMenuData() throws DayMenuServiceException, MenuItemServiceException, FoodItemServiceException;
	public boolean updateDayMenuData(DayMenu dayMenu) throws DayMenuServiceException;
	public boolean deleteDayMenuData(DayMenu dayMenu) throws DayMenuServiceException;
}
