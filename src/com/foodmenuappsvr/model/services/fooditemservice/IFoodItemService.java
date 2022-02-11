package com.foodmenuappsvr.model.services.fooditemservice;

import java.util.ArrayList;

import com.foodmenu.model.domain.FoodItem;
import com.foodmenuappsvr.model.services.IService;
import com.foodmenuappsvr.model.services.exceptions.FoodItemServiceException;

public interface IFoodItemService extends IService {
	
	public final String NAME = "IFoodItemService";
	
	public boolean createFoodItemData(FoodItem foodItem) throws FoodItemServiceException;
	public FoodItem retrieveFoodItemData(String foodName) throws FoodItemServiceException;
	public FoodItem retrieveFoodItemData(int foodItemID) throws FoodItemServiceException;
	public ArrayList<FoodItem> retrieveAllFoodItemData() throws FoodItemServiceException;
	public boolean updateFoodItemData(FoodItem foodItem) throws FoodItemServiceException;
	public boolean deleteFoodItemData(FoodItem foodItem) throws FoodItemServiceException;

}
