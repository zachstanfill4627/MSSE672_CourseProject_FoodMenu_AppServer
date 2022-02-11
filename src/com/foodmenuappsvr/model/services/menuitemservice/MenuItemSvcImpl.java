package com.foodmenuappsvr.model.services.menuitemservice;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.foodmenu.model.domain.FoodItem;
import com.foodmenu.model.domain.MenuItem;
import com.foodmenuappsvr.model.services.exceptions.FoodItemServiceException;
import com.foodmenuappsvr.model.services.exceptions.MenuItemServiceException;
import com.foodmenuappsvr.model.services.fooditemservice.FoodItemSvcImpl;

public class MenuItemSvcImpl implements IMenuItemService {
	
	private static Logger  LOGGER = Logger.getLogger(MenuItemSvcImpl.class);
	
	private static String propertiesFile = "config/application.properties";

	private static String connString, dbUsername, dbPassword;

	public MenuItemSvcImpl()  {
		LOGGER.trace("MenuItemSvcImpl Default Constructor Called");
		
		try (InputStream input = new FileInputStream(propertiesFile)) {
            Properties prop = new Properties();
            prop.load(input);
            if(prop.getProperty("dbconnect.string") != null){
            	connString = prop.getProperty("dbconnect.string");
            	LOGGER.debug(String.format("Database Connection String = %s", connString));
            } else throw new Exception("dbconnect.string not present in properties file"); 
            if(prop.getProperty("dbconnect.string") != null){
            	dbUsername = prop.getProperty("dbconnect.user");
            	LOGGER.debug(String.format("Database Username = %s", dbUsername));
            } else throw new Exception("dbconnect.user not present in properties file");
            if(prop.getProperty("dbconnect.string") != null){
            	dbPassword = prop.getProperty("dbconnect.password");
            	LOGGER.debug(String.format("Database Password = %s", dbPassword));
            } else throw new Exception("dbconnect.password not present in properties file");
            
            LOGGER.trace("Successfully read database connection properties from properties files");
		} catch (Exception e) {
			System.err.println("Error in reading property file database connection values, Exiting!");
			System.err.println(e);
			LOGGER.fatal(e);
			System.exit(1);
		}
	}

	public boolean createMenuItemData(MenuItem menuItem) throws MenuItemServiceException {
		LOGGER.trace("createMenuItemData Called");
		
		/** Localize Variables */
		String mealName = menuItem.getMealName();
		ArrayList<FoodItem> foodList = menuItem.getFoodList();
		int complexValue = menuItem.getComplexityValue();
		double healthValue = menuItem.getHealthValue();
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Insert MenuItem Record into MenuItems Table */
		strBfr.append(String.format("INSERT INTO menuitems (mealname, "
				+ "complexityvalue, healthvalue) VALUES (\"%s\", %d, %.2f);", 
				mealName, complexValue, healthValue));
		String sql1 = strBfr.toString();
		strBfr.setLength(0);
		
		/** SQL Statement 2, Insert FoodList Items into MealFoodList Table */
		ArrayList<String> foodListInsert = new ArrayList<String>();  
		foodList.forEach(item -> {
			strBfr.append(String.format("INSERT INTO mealfoodlist "
					+ "(menuitemid, fooditemid) VALUES (%s, %s",
					"(SELECT menuitemid FROM menuitems WHERE mealname = \"" + mealName + "\")",
					"(SELECT fooditemid FROM fooditems WHERE foodname = \"" + item.getFoodName() + "\"));"));
			foodListInsert.add(strBfr.toString());
			strBfr.setLength(0);
		});
		
		LOGGER.debug("createMenuItemData -- SQL Statements:");
		LOGGER.debug(sql1);
		foodList.forEach(item -> LOGGER.debug(item));
		
		/** Connect to Database & Execute SQL Statements & Check Accuracy */
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
			conn.setAutoCommit(false);
            
			/** Execute SQL Insert Statements - Batch Style */
			stmt.addBatch(sql1);
            foodListInsert.forEach(item -> {
					try {
						stmt.addBatch(item);
					} catch (SQLException e) {
						e.printStackTrace();
					}
			});
            stmt.executeBatch();
            LOGGER.trace("SQL Statements Executed");
            
            /** Commit Changes */ 
            conn.commit();
            LOGGER.trace("SQL Statements Committed");
            
            /** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
        } catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
        	LOGGER.error(e.getMessage());
            return false;
        }
		
		/** If Successful, Return True */
		return true; 
	}

	public MenuItem retrieveMenuItemData(String mealName) throws MenuItemServiceException, FoodItemServiceException {
		LOGGER.trace("retrieveMenuItemData(String) Called");
		/** Localize Variables */
		int menuItemID = 0;
		ArrayList<FoodItem> foodList = new ArrayList<FoodItem>();
		int complexValue = 0;
		double healthValue = 0.0;
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Select Record from MenuItems Table */
		strBfr.append(String.format("SELECT * FROM menuitems WHERE mealname = "
				+ "\"%s\"", mealName));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("retrieveMenuItemData(String) -- SQL Statements:");
		LOGGER.debug(query);
		
		/** Connect to Database & Execute SQL Statements & Check Accuracy */
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
			conn.setAutoCommit(false);
			
			/** Run SQL Query against FoodItems Table */
            ResultSet rs = stmt.executeQuery(query);
            
            if(rs.next()) {
            	/** Assign Query Return to Variables */
            	menuItemID = rs.getInt("menuitemid");
            	mealName = rs.getString("mealname");
            	complexValue = rs.getInt("complexityvalue");
            	healthValue = rs.getDouble("healthvalue");
            } else {
            	return null;
            }
            
            /** SQL Statement 2, Select Record from MealFoodList Table */
    		strBfr.append(String.format("SELECT * FROM mealfoodlist WHERE menuitemid "
    				+ "= %d;", menuItemID));
    		query = strBfr.toString();
    		strBfr.setLength(0);
    		
    		rs = stmt.executeQuery(query);
    		FoodItemSvcImpl foodImpl = new FoodItemSvcImpl();
    		while(rs.next()) {
    			foodList.add(foodImpl.retrieveFoodItemData(rs.getInt("fooditemid")));
    		}
            
            /** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
		} catch (SQLException e) {
    			/** Error Output */
    	        System.err.println(e.getMessage());
    	        LOGGER.error(e.getMessage());
    	        return null;
    	}
		
		MenuItem menuItem = new MenuItem (mealName, foodList, complexValue);
		
		return menuItem;
	}
	
	public MenuItem retrieveMenuItemData(int menuItemID) throws MenuItemServiceException, FoodItemServiceException {
		LOGGER.trace("retrieveMenuItemData(int) Called");
		/** Localize Variables */
		String mealName = "";
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Select Record from FoodItems Table */
		strBfr.append(String.format("SELECT * FROM menuitems WHERE menuitemid "
				+ "= %d", menuItemID));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("retrieveMenuItemData(int) -- SQL Statements:");
		LOGGER.debug(query);
		
		/** Connect to Database & Execute SQL Statements & Check Accuracy */
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
			conn.setAutoCommit(false);
			
			/** Run SQL Query against FoodItems Table */
            ResultSet rs = stmt.executeQuery(query);
            
            if(rs.next()) {
            	/** Assign Query Return to Variables */
            	mealName = rs.getString("mealname");
            } else {
            	return null;
            }
            
            /** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
		} catch (SQLException e) {
			/** Error Output */
	        System.err.println(e.getMessage());
	        LOGGER.error(e.getMessage());
	        return null;
	    }
		
		MenuItem menuItem = retrieveMenuItemData(mealName);
				
		/** If Successful, Return True */
		return menuItem;
	}
	
	public ArrayList<MenuItem> retrieveAllMenuItemData () throws MenuItemServiceException, FoodItemServiceException {
		LOGGER.trace("retrieveAllMenuItemData Called");
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Select Record from FoodItems Table */
		strBfr.append(String.format("SELECT mealName FROM menuitems;"));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("retrieveAllMenuItemData -- SQL Statements:");
		LOGGER.debug(query);
		
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) { 
			LOGGER.trace("Database Connection Opened");
            
            /** Run SQL Query against Users Table */
            ResultSet rs = stmt.executeQuery(query);
            
            while(rs.next()) {
            	menuItems.add(retrieveMenuItemData(rs.getString("mealname")));
            }   
            
            /** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
            
            return menuItems;
		} catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
        	LOGGER.error(e.getMessage());
        	return null;
        }
	}

	public boolean updateMenuItemData(MenuItem menuItem) throws MenuItemServiceException {
		LOGGER.trace("updateMenuItemData Called");
		deleteMenuItemData(menuItem);
		if(!createMenuItemData(menuItem)) {
			return false;
		}
		
		return true;
	}

	public boolean deleteMenuItemData(MenuItem menuItem) throws MenuItemServiceException {
		LOGGER.trace("deleteMenuItemData Called");
		String mealName = menuItem.getMealName();
	
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Delete Record from MenuItems Table */
		strBfr.append(String.format("DELETE FROM menuitems WHERE mealname = "
				+ "\"%s\";", mealName));
		String sql1 = strBfr.toString();
		strBfr.setLength(0);
		
		/** Database Cleanup 
		 * BUG -- SQLite Database Table Configured to ON DELETE CASCADE, however 
		 * cascade is not properly working, therefore manual DELETE Statements
		 * complete database cleanup tasks 
		 */
		String sql2 = "DELETE FROM mealfoodlist WHERE menuitemid NOT IN (SELECT "
				+ "DISTINCT menuitemid FROM menuitems);";

		/** SQL Statement 3, Select Record from MealItems Table */
		strBfr.append(String.format("SELECT * FROM menuitems WHERE mealname like "
				+ "\"%s\";", mealName));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("deleteMenuItemData -- SQL Statements:");
		LOGGER.debug(sql1);
		LOGGER.debug(sql2);
		LOGGER.debug(query);
		
		/** Connect to Database & Execute SQL Statements & Check Accuracy */
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
			conn.setAutoCommit(false);
			
			/** Execute SQL Statements - Batch Style */
			stmt.addBatch(sql1);
			stmt.addBatch(sql2);
            stmt.executeBatch();
            
            /** Commit Changes */ 
            conn.commit();          
            
            /** Run SQL Query against record */
            ResultSet rs = stmt.executeQuery(query);
            
            if(rs.next()) { return false; };

            /** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
		} catch (SQLException e) {
			/** Error Output */
	        System.err.println(e.getMessage());
	        LOGGER.error(e.getMessage());
	        return false;
	    }
			
		/** If Successful, Return True */
		return true;
	}
}
