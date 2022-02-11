package com.foodmenuappsvr.model.services.daymenuservice;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.foodmenu.model.domain.DayMenu;
import com.foodmenu.model.domain.MenuItem;
import com.foodmenuappsvr.model.services.exceptions.DayMenuServiceException;
import com.foodmenuappsvr.model.services.exceptions.FoodItemServiceException;
import com.foodmenuappsvr.model.services.exceptions.MenuItemServiceException;
import com.foodmenuappsvr.model.services.menuitemservice.MenuItemSvcImpl;

public class DayMenuSvcImpl implements IDayMenuService {
	
	private static Logger  LOGGER = Logger.getLogger(DayMenuSvcImpl.class);
	
	private static String propertiesFile = "config/application.properties";

	private static String connString, dbUsername, dbPassword;

	public DayMenuSvcImpl() {
		LOGGER.trace("DayMenuSvcImpl Default Constructor Called");
		
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
			System.exit(4);
		}
	}

	public boolean createDayMenuData(DayMenu dayMenu) throws DayMenuServiceException {
		LOGGER.trace("createDayMenuData Called");		
		
		/** Localize Variables */
		Calendar date = dayMenu.getDate();
		ArrayList<MenuItem> menuList = dayMenu.getMenuList();
		int complexValue = dayMenu.getComplexityValue();
		double healthValue = dayMenu.getHealthValue();
		
		/** Date Formatter */
		String dateString = String.format("%d-%d-%d", 
				date.get(Calendar.YEAR), date.get(Calendar.MONTH), 
				date.get(Calendar.DATE));
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Insert DayMenu Record into DayMenu Table */
		strBfr.append(String.format("INSERT INTO daymenu (date, "
				+ "complexityvalue, healthvalue) VALUES (\"%s\", %d, %.2f);", 
				dateString, complexValue, healthValue));
		String sql1 = strBfr.toString();
		strBfr.setLength(0);
		
		/** SQL Statement 2, Insert FoodList Items into MealFoodList Table */
		ArrayList<String> menuListInsert = new ArrayList<String>();  
		menuList.forEach(item -> {
			strBfr.append(String.format("INSERT INTO daymeallist "
					+ "(daymenuid, menuitemid) VALUES (%s, %s",
					"(SELECT daymenuid FROM daymenu WHERE date = \"" + dateString + "\")",
					"(SELECT menuitemid FROM menuitems WHERE mealname = \"" + item.getMealName() + "\"));"));
			menuListInsert.add(strBfr.toString());
			strBfr.setLength(0);
		});
		
		LOGGER.debug("createDayMenuData -- SQL Statements:");
		LOGGER.debug(sql1);
		menuListInsert.forEach(item -> LOGGER.debug(item));
		
		/** Connect to Database & Execute SQL Statements & Check Accuracy */
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
			conn.setAutoCommit(false);
            
			/** Execute SQL Insert Statements - Batch Style */
			stmt.addBatch(sql1);
			LOGGER.info(String.format("DayMenu %s added to daymenu database table", dateString));
            menuListInsert.forEach(item -> {
					try {
						stmt.addBatch(item);
						LOGGER.info(String.format("Meal linked to %s daymenu", dateString));
					} catch (SQLException e) {
						LOGGER.warn(String.format("Failed to link meal to %s daymenu", dateString));
						e.printStackTrace();
					}
			});
            stmt.executeBatch();
            LOGGER.trace("SQL Statements Executed");            
            /** Commit Changes */ 
            conn.commit();
            LOGGER.trace("SQL Statements commited");
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
		LOGGER.info(String.format("DayMenu %s successfully added to database", dateString));
		return true;
	}

	public DayMenu retrieveDayMenuData(Calendar date) throws DayMenuServiceException, MenuItemServiceException, FoodItemServiceException {
		LOGGER.trace("retrieveDayMenuData Called");
		
		/** Localize Variables */
		int dayMenuID = 0;
		ArrayList<MenuItem> menuList = new ArrayList<MenuItem>();
		int complexValue = 0;
		double healthValue = 0.0;
		
		/** Date Formatter */
		String dateString = String.format("%d-%d-%d", 
				date.get(Calendar.YEAR), date.get(Calendar.MONTH), 
				date.get(Calendar.DATE));
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Select Record from MenuItems Table */
		strBfr.append(String.format("SELECT * FROM daymenu WHERE date = "
				+ "\"%s\"", dateString));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("retrieveDayMenuData -- SQL Statements:");
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
            	dayMenuID = rs.getInt("daymenuid");
            	complexValue = rs.getInt("complexityvalue");
            	healthValue = rs.getDouble("healthvalue");
            } else {
            	return null;
            }
            
            /** SQL Statement 2, Select Record from MealFoodList Table */
    		strBfr.append(String.format("SELECT * FROM daymeallist WHERE daymenuid "
    				+ "= %d;", dayMenuID));
    		query = strBfr.toString();
    		strBfr.setLength(0);
    		
    		rs = stmt.executeQuery(query);
    		MenuItemSvcImpl menuImpl = new MenuItemSvcImpl();
    		while(rs.next()) {
    			menuList.add(menuImpl.retrieveMenuItemData(rs.getInt("menuitemid")));
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
		
		DayMenu dayMenu = new DayMenu (date, menuList);
		
		LOGGER.info(String.format("DayMenu %s successfully retrieved from database", dateString));
		return dayMenu;
	}
	
	public ArrayList<DayMenu> retrieveAllDayMenuData () throws DayMenuServiceException, MenuItemServiceException, FoodItemServiceException {
		LOGGER.trace("retrieveAllDayMenuData Called");
		
		DayMenu dayMenu = new DayMenu();
		ArrayList<DayMenu> dayMenus = new ArrayList<DayMenu>();
		ArrayList<String> dateValues = new ArrayList<String>();
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Select Record from FoodItems Table */
		strBfr.append(String.format("SELECT date FROM daymenu;"));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("retrieveAllDayMenuData -- SQL Statements:");
		LOGGER.debug(query);
		
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) { 
			LOGGER.trace("Database Connection Opened");
            
            /** Run SQL Query against Users Table */
            ResultSet rs = stmt.executeQuery(query);
            
            while(rs.next()) {
            	dateValues.add(rs.getString("date"));
            }            
            
            dateValues.forEach(item -> {{
            	Calendar cal = Calendar.getInstance();
            	int year = Integer.parseInt(item.split("-")[0]);
            	int month = Integer.parseInt(item.split("-")[1]);
            	int day = Integer.parseInt(item.split("-")[2]);
            	cal.set(year, month, day);
            	
            	try {
					dayMenus.add(retrieveDayMenuData(cal));
				} catch (DayMenuServiceException | MenuItemServiceException | FoodItemServiceException e) {
					e.printStackTrace();
				}
            }});
            
    		/** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
         
            return dayMenus;
		} catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
        	LOGGER.error(e.getMessage());
        	return null;
        }
	}

	public boolean updateDayMenuData(DayMenu dayMenu) throws DayMenuServiceException {
		LOGGER.trace("updateDayMenuData Called");
		
		deleteDayMenuData(dayMenu);
		if(!createDayMenuData(dayMenu)) {
			LOGGER.error("updateDayMenuData -- Failed to createDayMenuData");
			return false;
		}
		
		LOGGER.error("updateDayMenuData Completed Successfully");
		return true;
	}

	public boolean deleteDayMenuData(DayMenu dayMenu) throws DayMenuServiceException {
		LOGGER.trace("deleteDayMenuData Called");
		
		Calendar date = dayMenu.getDate();
		
		/** Date Formatter */
		String dateString = String.format("%d-%d-%d", 
				date.get(Calendar.YEAR), date.get(Calendar.MONTH), 
				date.get(Calendar.DATE));
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Delete Record from MenuItems Table */
		strBfr.append(String.format("DELETE FROM dayMenu WHERE date = "
				+ "\"%s\";", dateString));
		String sql1 = strBfr.toString();
		strBfr.setLength(0);
		
		/** Database Cleanup 
		 * BUG -- SQLite Database Table Configured to ON DELETE CASCADE, however 
		 * cascade is not properly working, therefore manual DELETE Statements
		 * complete database cleanup tasks 
		 */
		String sql2 = "DELETE FROM daymeallist WHERE daymenuid NOT IN (SELECT "
				+ "DISTINCT daymenuid FROM daymenu);";

		/** SQL Statement 3, Select Record from MealItems Table */
		strBfr.append(String.format("SELECT * FROM daymenu WHERE date = "
				+ "\"%s\";", dateString));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("deleteDayMenuData -- SQL Statements:");
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
			LOGGER.info(String.format("deleteDayMenuData -- Deleted dayMenu %s from database", dateString));
			stmt.addBatch(sql2);
			LOGGER.info(String.format("deleteDayMenuData -- Deleted meals associated with daymenu %s from database", dateString));
            stmt.executeBatch();
            LOGGER.trace("SQL Statements Executed");
            
            /** Commit Changes */ 
            conn.commit();
            LOGGER.trace("SQL Statements Commited");
            
            /** Run SQL Query against record */
            ResultSet rs = stmt.executeQuery(query);
            
            if(rs.next()) { return false; };
            LOGGER.info(String.format("deleteDayMenuData -- Verified dayMenu %s was removed from the database", dateString));

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
		LOGGER.info(String.format("DayMenu %s successfully deleted from  database", dateString));
		return true;
	}
}
