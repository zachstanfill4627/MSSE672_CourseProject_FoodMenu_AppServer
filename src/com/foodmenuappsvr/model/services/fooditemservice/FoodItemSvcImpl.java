package com.foodmenuappsvr.model.services.fooditemservice;

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
import com.foodmenuappsvr.model.services.exceptions.FoodItemServiceException;

public class FoodItemSvcImpl implements IFoodItemService {
	
	private static Logger  LOGGER = Logger.getLogger(FoodItemSvcImpl.class);
	
	private static String propertiesFile = "config/application.properties";

	private static String connString, dbUsername, dbPassword;

	public FoodItemSvcImpl() {
		LOGGER.trace("FoodItemSvcImpl Default Constructor Called");
		
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
			System.exit(2);
		}
	}

	public boolean createFoodItemData(FoodItem foodItem) throws FoodItemServiceException {
		LOGGER.trace("createFoodItemData Called");
		
		/** Localize Variables */
		String foodName = foodItem.getFoodName();
		String category = foodItem.getCategory();
		int healthValue = foodItem.getHealthValue();
		int prepTime = foodItem.getPrepTime();
		ArrayList<String> recipe = foodItem.getRecipe();
		ArrayList<String> ingredients = foodItem.getIngredients();
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Insert FoodItem Record into FoodItems Table */
		strBfr.append(String.format("INSERT INTO fooditems (foodname, category, "
				+ "healthvalue, preptime) VALUES (\"%s\", \"%s\", %d, %d);", 
				foodName, category, healthValue, prepTime));
		String sql1 = strBfr.toString();
		strBfr.setLength(0);
		
		/** SQL Statement 2, Insert Recipe Step into Recipe Table */
		ArrayList<String> recipeInsert = new ArrayList<String>();  
		recipe.forEach(step -> {
			strBfr.append(String.format("INSERT INTO recipe (fooditemid, "
				+ "steptext) VALUES (%s, \"%s\");\n", 
				"(SELECT fooditemid FROM fooditems WHERE foodname = \"" + foodName 
				+ "\" ORDER BY fooditemid DESC)",
				step)) ; 
				recipeInsert.add(strBfr.toString());
				strBfr.setLength(0);
		});
		
		/** SQL Statement 3, Insert Ingredients into Ingredients Table */
		ArrayList<String> ingredientsInsert = new ArrayList<String>();
		ingredients.forEach(ingredient -> {
			strBfr.append(String.format("INSERT "
				+ "INTO ingredients (fooditemid, ingredient) VALUES "
				+ "(%s, \"%s\");\n",
				"(SELECT fooditemid FROM fooditems WHERE foodname = \"" + foodName 
				+ "\" ORDER BY fooditemid DESC)",
				ingredient));
			ingredientsInsert.add(strBfr.toString());
			strBfr.setLength(0);	
		});
		
		LOGGER.debug("createFoodItemData -- SQL Statements:");
		LOGGER.debug(sql1);
		recipe.forEach(step -> LOGGER.debug(step));
		ingredients.forEach(item -> LOGGER.debug(item));
		
		/** Connect to Database & Execute SQL Statements & Check Accuracy */
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
			conn.setAutoCommit(false);
            
			/** Execute SQL Insert Statements - Batch Style */
			stmt.addBatch(sql1);
            recipeInsert.forEach(step -> {
					try {
						stmt.addBatch(step);
					} catch (SQLException e) {
						e.printStackTrace();
					}
			});
            ingredientsInsert.forEach(ingredient -> {
				try {
					stmt.addBatch(ingredient);
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
            LOGGER.trace("Database Connection Opened");
        } catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
        	LOGGER.error(e.getMessage());
            return false;
        }
		
		/** If Successful, Return True */
		return true;     
				
	}

	public FoodItem retrieveFoodItemData(String foodName) throws FoodItemServiceException {
		LOGGER.trace("retrieveFoodItemData(String) Called");
		/** Localize Variables */
		int foodItemID = 0;
		String category = "";
		int healthValue = 0;
		int prepTime = 0;
		ArrayList<String> recipe = new ArrayList<String>();
		ArrayList<String> ingredients = new ArrayList<String>();
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Select Record from FoodItems Table */
		strBfr.append(String.format("SELECT * FROM fooditems WHERE foodname = "
				+ "\"%s\"", foodName));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("retrieveFoodItemData -- SQL Statements:");
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
            	foodItemID = rs.getInt("fooditemid");
            	foodName = rs.getString("foodname");
            	category = rs.getString("category");
            	healthValue = rs.getInt("healthvalue");
            	prepTime = rs.getInt("preptime");
            } else {
            	return null;
            }
            
            /** SQL Statement 2, Select Record from Recipe Table */
    		strBfr.append(String.format("SELECT * FROM recipe WHERE fooditemid "
    				+ "= %d ORDER BY recipeid;", foodItemID));
    		query = strBfr.toString();
    		strBfr.setLength(0);
    		
    		rs = stmt.executeQuery(query);
    		while(rs.next()) {
    			recipe.add(rs.getString("steptext"));
    		}
    		
    		/** SQL Statement 3, Select Record from Ingredients Table */
    		strBfr.append(String.format("SELECT * FROM ingredients WHERE fooditemid "
    				+ "= %d;", foodItemID));
    		query = strBfr.toString();
    		strBfr.setLength(0);
    		
    		rs = stmt.executeQuery(query);
    		while(rs.next()) {
    			ingredients.add(rs.getString("ingredient"));
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
		
		/** Create FoodItem Object */
		FoodItem foodItem = new FoodItem(foodName, category, healthValue, prepTime,
				recipe, ingredients);
		
		/** If Successful, Return True */
		return foodItem;
	}
	
	public FoodItem retrieveFoodItemData(int foodItemID) throws FoodItemServiceException {
		LOGGER.trace("retrieveFoodItemData(int) Called");
		/** Localize Variables */
		String foodName = "";

		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Select Record from FoodItems Table */
		strBfr.append(String.format("SELECT * FROM fooditems WHERE fooditemid "
				+ "= %d", foodItemID));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("retrieveFoodItemData -- SQL Statements:");
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
            	foodName = rs.getString("foodname");
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
		
		FoodItem foodItem = retrieveFoodItemData(foodName);
				
		/** If Successful, Return True */
		return foodItem;
	}
	
	public ArrayList<FoodItem> retrieveAllFoodItemData() throws FoodItemServiceException {
		LOGGER.trace("retrieveAllFoodItemData Called");
		ArrayList<FoodItem> foodItems = new ArrayList<FoodItem>();
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Select Record from FoodItems Table */
		strBfr.append(String.format("SELECT foodName FROM fooditems;"));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("retrieveAllFoodItemData -- SQL Statements:");
		LOGGER.debug(query);
		
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {    
			LOGGER.trace("Database Connection Opened");
            
            /** Run SQL Query against Users Table */
            ResultSet rs = stmt.executeQuery(query);
            
            while(rs.next()) {
            	foodItems.add(retrieveFoodItemData(rs.getString("FoodName")));
            }            
            
    		/** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
            
            return foodItems;
		} catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
        	LOGGER.error(e.getMessage());
        	return null;
        }
	}

	public boolean updateFoodItemData(FoodItem foodItem) throws FoodItemServiceException {
		LOGGER.trace("updateFoodItemData Called");
		deleteFoodItemData(foodItem);
		if(!createFoodItemData(foodItem)) {
			return false;
		}
		
		return true;
	}

	public boolean deleteFoodItemData(FoodItem foodItem) throws FoodItemServiceException {
		LOGGER.trace("deleteFoodItemData Called");
		String foodName = foodItem.getFoodName();
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Delete Record from FoodItems Table */
		strBfr.append(String.format("DELETE FROM fooditems WHERE foodname like "
				+ "\"%s\";", foodName));
		String sql1 = strBfr.toString();
		strBfr.setLength(0);
		
		/** Database Cleanup 
		 * BUG -- SQLite Database Table Configured to ON DELETE CASCADE, however 
		 * cascade is not properly working, therefore manual DELETE Statements
		 * complete database cleanup tasks 
		 */
		String sql2 = "DELETE FROM recipe WHERE fooditemid NOT IN (SELECT "
				+ "DISTINCT fooditemid FROM fooditems);";
		String sql3 = "DELETE FROM ingredients WHERE fooditemid NOT IN (SELECT "
				+ "DISTINCT fooditemid FROM fooditems);";
		String sql4 = "DELETE FROM mealfoodlist WHERE fooditemid NOT IN (SELECT "
				+ "DISTINCT fooditemid FROM fooditems);";
		
		/** SQL Statement 4, Select Record from FoodItems Table */
		strBfr.append(String.format("SELECT * FROM fooditems WHERE foodname = "
				+ "\"%s\";", foodName));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("deleteFoodItemData -- SQL Statements:");
		LOGGER.debug(sql1);
		LOGGER.debug(sql2);
		LOGGER.debug(sql3);
		LOGGER.debug(sql4);
		LOGGER.debug(query);
		
		/** Connect to Database & Execute SQL Statements & Check Accuracy */
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
			conn.setAutoCommit(false);
			
			/** Execute SQL Statements - Batch Style */
			stmt.addBatch(sql1);
			stmt.addBatch(sql2);
			stmt.addBatch(sql3);
			stmt.addBatch(sql4);
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
