package com.foodmenu.model.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class Response implements Serializable {
	
	private boolean flag;
	private String message;
	private FoodItem foodItem;
	private MenuItem menuItem;
	private DayMenu dayMenu;
	private User user;
	private ArrayList<FoodItem> foodItems;
	private ArrayList<MenuItem> menuItems;
	private ArrayList<DayMenu> dayMenus;
	private ArrayList<User> users;

	// Default Constructor
	public Response() {	
	}

	// Overloaded Constructor
	public Response(boolean flag, String message) {
		this.flag = flag;
		this.message = message;
	}

	public boolean getFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public FoodItem getFoodItem() {
		return foodItem;
	}

	public void setFoodItem(FoodItem foodItem) {
		this.foodItem = foodItem;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}

	public DayMenu getDayMenu() {
		return dayMenu;
	}

	public void setDayMenu(DayMenu dayMenu) {
		this.dayMenu = dayMenu;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ArrayList<FoodItem> getFoodItems() {
		return foodItems;
	}

	public void setFoodItems(ArrayList<FoodItem> foodItems) {
		this.foodItems = foodItems;
	}

	public ArrayList<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(ArrayList<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public ArrayList<DayMenu> getDayMenus() {
		return dayMenus;
	}

	public void setDayMenus(ArrayList<DayMenu> dayMenus) {
		this.dayMenus = dayMenus;
	}

	public ArrayList<User> getUsers() {
		return users;
	}

	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}
	
	public String toString() {
		return String.format("Flag: %b ; Message: %s", flag, message);
	}
}
