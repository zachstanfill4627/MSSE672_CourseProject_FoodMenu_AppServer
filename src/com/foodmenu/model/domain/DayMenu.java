package com.foodmenu.model.domain;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.log4j.Logger;

/**
 * 
 * @author Zach Stanfill
 * Domain DayMenu Class
 */
public class DayMenu implements Serializable {
	
	private static Logger  LOGGER = Logger.getLogger(DayMenu.class);
	
	private static final long serialVersionUID = 1234567L;
	
	private final DecimalFormat df = new DecimalFormat("#.##");
	
	/** Specified date for the given DayMenu */
	private Calendar date;
	
	/** List of Menu Items for the given DayMenu Object */ 
	private ArrayList<MenuItem> menuList = new ArrayList<MenuItem>();
	
	/** User Specified Complexity Value for DayMenu Object */
	private int complexityValue = 0;
	
	/** Calculated Health Value of the DayMenu Object based on Menu Item
	 * Health Values 
	 */
	private double healthValue = 0.0;
	
	/** Default DayMenu Object constructor */
	public DayMenu() {
		LOGGER.trace("DayMenu Default Constructor Called");
	}	
	
	/**
	 * Overloaded DayMenu Object Constructor
	 * @param menuList
	 */
	public DayMenu(Calendar date, ArrayList<MenuItem> menuList) {
		super();
		LOGGER.trace("DayMenu Overloaded Constructor Called");
		this.date = date;
		this.menuList.addAll(menuList);
		menuList.forEach(item -> {
			this.complexityValue += item.getComplexityValue();
			this.healthValue = Double.sum(this.healthValue, item.getHealthValue());
		});
		this.complexityValue = this.complexityValue / menuList.size();
		this.healthValue = this.healthValue	/ menuList.size();
		LOGGER.debug(String.format("DateMenu %d-%d-%d Object Instantiated", 
				date.get(Calendar.YEAR), date.get(Calendar.MONTH)+1, date.get(Calendar.DATE)));
	}

	/**
	 * @return the date
	 */
	public Calendar getDate() {
		LOGGER.trace("getDate Method Called");
		return date;
	}
	
	public String getDateString() {
		LOGGER.trace("getDateString Method Called");
		String dateString = String.format("%d-%d-%d", 
				date.get(Calendar.YEAR), date.get(Calendar.MONTH)+1, 
				date.get(Calendar.DATE));
		
		return dateString;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Calendar date) {
		LOGGER.trace("setDate Method Called");
		this.date = date;
	}
	
	/**
	 * @return the menuList
	 */
	public ArrayList<MenuItem> getMenuList() {
		LOGGER.trace("getMenuList Method Called");
		return menuList;
	}

	/**
	 * @param menuList the menuList to set
	 */
	public void setMenuList(ArrayList<MenuItem> menuList) {
		LOGGER.trace("setMenuList Method Called");
		this.menuList = menuList;
	}
	
	/**
	 * @return the complexityValue
	 */
	public int getComplexityValue() {
		LOGGER.trace("getComplexityValue Method Called");
		return complexityValue;
	}

	/**
	 * @param complexityValue the complexityValue to set
	 */
	public void setComplexityValue(int complexityValue) {
		LOGGER.trace("setComplexityValue Method Called");
		this.complexityValue = complexityValue;
	}

	/**
	 * @return the healthValue
	 */
	public double getHealthValue() {
		LOGGER.trace("getHealthValue Method Called");
		return healthValue;
	}

	/**
	 * @param healthValue the healthValue to set
	 */
	public void setHealthValue() {
		LOGGER.trace("setHealthValue Method Called");
		this.healthValue = 0.0;
		menuList.forEach(item -> this.healthValue+=item.getHealthValue());
		this.healthValue = this.healthValue / menuList.size();
	}

	/** 
	 * toString() method
	 * @return Formatted String listing all variables for the DayMenu Object
	 */
	public String toString() {
		LOGGER.trace("toString Method Called");
		StringBuffer strBfr = new StringBuffer();
		strBfr.append(String.format("Scheduled Day: %d-%d-%d\n", 
				date.get(Calendar.YEAR), date.get(Calendar.MONTH), 
				date.get(Calendar.DATE)));
		strBfr.append("Day Complexity Value: " + complexityValue);
		strBfr.append("\nDay Health Value: " + df.format(healthValue));
		menuList.forEach(item -> { 
			strBfr.append("\n****************************************\n"); 
			strBfr.append(item.toSummaryString());
		});
				
		return strBfr.toString(); 
	}
	
	/** 
	 * toSummaryString() method
	 * @return Formatted String listing some variables for the DayMenu Object
	 */
	public String toSummaryString() {
		LOGGER.trace("toSummaryString Method Called");
		StringBuffer strBfr = new StringBuffer();
		strBfr.append(String.format("Scheduled Day: %d-%d-%d\n", 
				date.get(Calendar.YEAR), date.get(Calendar.MONTH), 
				date.get(Calendar.DATE)));
		strBfr.append("Day Complexity Value: " + complexityValue);
		strBfr.append("\nDay Health Value: " + df.format(healthValue));
		menuList.forEach(item -> { 
			strBfr.append("\n****************************************\n");
			strBfr.append("Menu Item: " + item.getMealName());
			item.getFoodList().forEach(food -> {
				strBfr.append("\n\t" + food.getFoodName());
			});
		});
				
		return strBfr.toString(); 
	}
	
	/**
	 * validate() method
	 * @return boolean if all fields for DayMenu object is valid
	 */
	 public boolean validate() {
		 LOGGER.trace("validate Method Called");
		 if(date == null) return false;
		 if(menuList == null) return false;
		 if(complexityValue < 0) return false;
		 if(healthValue < 0) return false;
		 LOGGER.debug("DayMenu Validated");		
		 return true;
	 }
	
}