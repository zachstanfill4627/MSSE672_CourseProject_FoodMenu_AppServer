package com.foodmenu.model.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

/**
 * 
 * @author Zach Stanfill
 * Domain FoodItem Class
 */
public class FoodItem implements Serializable {
	
	private static Logger  LOGGER = Logger.getLogger(FoodItem.class);
	
	private static final long serialVersionUID = 1234567L;
	
	/** Common Known Food Name */
	private String foodName;
	
	/** Generalized Food Category 
	 * (Examples: American, Asian, Italian, Mexican, Other, 
	 * Side, Fruits, Vegetables)
	 */ 
	private String category;
	
	/** Generalized Health Value (1-10 Scale) */
	private int healthValue;
	
	/** Generalized Prep Time For Completion of the Food Item  
	 * (Calculated in Minutes)
	 */ 
	private int prepTime;
	
	/** User Defined Recipe for the specified Food item */
	private ArrayList<String> recipe = new ArrayList<String>();
	
	/** List of Ingredients for the specified food item*/
	private ArrayList<String> ingredients = new ArrayList<String>();
	
	/** Default Constructor */
	public FoodItem() {
		LOGGER.trace("FoodItem Default Constructor Called");
	}
	
	/** 
	 * Overloaded Constructor
	 * @param foodName
	 * @param category
	 * @param healthValue
	 * @param prepTime
	 * @param recipe
	 * @param ingredients
	 * 
	 */
	public FoodItem(String foodName, String category, int healthValue, 
					int prepTime, ArrayList<String> recipe, ArrayList<String> ingredients) 
					{
		super(); 
		LOGGER.trace("FoodItem Overloaded Constructor Called");
		this.foodName = foodName;
		this.category = category;
		this.healthValue = healthValue;
		this.prepTime = prepTime;
		this.recipe.addAll(recipe);
		Collections.sort(ingredients);
		this.ingredients.addAll(ingredients);
		LOGGER.debug(String.format("FoodItem Object Instantiated -- FoodName:%s", foodName));
	}
	
	
	// Getters & Setters

	/**
	 * @return the foodName
	 */
	public String getFoodName() {
		LOGGER.trace("getFoodName Method Called");
		return foodName;
	}

	/**
	 * @param foodName the foodName to set
	 */
	public void setFoodName(String foodName) {
		LOGGER.trace("setFoodName Method Called");
		this.foodName = foodName;
	}

	/**
	 * @return the category
	 */
	public String getCategory() {
		LOGGER.trace("getCategory Method Called");
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		LOGGER.trace("setCategory Method Called");
		this.category = category;
	}

	/**
	 * @return the healthValue
	 */
	public int getHealthValue() {
		LOGGER.trace("getHealthValue Method Called");
		return healthValue;
	}

	/**
	 * @param healthValue the healthValue to set
	 */
	public void setHealthValue(int healthValue) {
		LOGGER.trace("setHealthValue Method Called");
		this.healthValue = healthValue;
	}

	/**
	 * @return the prepTime
	 */
	public int getPrepTime() {
		LOGGER.trace("getPrepTime Method Called");
		return prepTime;
	}

	/**
	 * @param prepTime the prepTime to set
	 */
	public void setPrepTime(int prepTime) {
		LOGGER.trace("setPrepTime Method Called");
		this.prepTime = prepTime;
	}

	/**
	 * @return the recipe
	 */
	public ArrayList<String> getRecipe() {
		LOGGER.trace("getRecipe Method Called");
		return recipe;
	}

	/**
	 * @param recipe the recipe to set
	 */
	public void setRecipe(ArrayList<String> recipe) {
		LOGGER.trace("setRecipe Method Called");
		this.recipe = recipe;
	}

	/**
	 * @return the ingredients
	 */
	public ArrayList<String> getIngredients() {
		LOGGER.trace("getIngredients Method Called");
		return ingredients;
	}

	/**
	 * @param ingredients the ingredients to set
	 */
	public void setIngredients(ArrayList<String> ingredients) {
		LOGGER.trace("setIngredients Method Called");
		this.ingredients = ingredients;
	}
	
	/** 
	 * toString() method
	 * @return Formatted String listing all variables for the FoodItem Object
	 */
	public String toString() {
		LOGGER.trace("toString Method Called");
		StringBuffer strBfr = new StringBuffer();
		strBfr.append("FoodItem foodName: " + foodName);
		strBfr.append("\nFoodItem category: " + category);
		strBfr.append("\nFoodItem healthValue: " + healthValue);
		strBfr.append("\nFoodItem prepTime: " + prepTime);
		strBfr.append("\nFoodItem recipe: \n\t=============\n");
		recipe.forEach(step -> strBfr.append("\t" + step + "\n")); 
		strBfr.append("\t=============");
		strBfr.append("\nFoodItem ingredients: \n\t=============\n");
		ingredients.forEach(item -> strBfr.append("\t" + item + "\n"));
		strBfr.append("\t=============");
		
		return strBfr.toString(); 
	}
	
	/** 
	 * toSummaryString() method
	 * @return Formatted String listing some variables for the FoodItem Object
	 */
	public String toSummaryString() {
		LOGGER.trace("toSummaryString Method Called");
		StringBuffer strBfr = new StringBuffer();
		strBfr.append("FoodItem foodName: " + foodName);
		strBfr.append("\nFoodItem category: " + category);
		strBfr.append("\nFoodItem healthValue: " + healthValue);
		strBfr.append("\nFoodItem prepTime: " + prepTime);
				
		return strBfr.toString(); 
	}
	
	/**
	 * validate() method
	 * @return boolean if all fields for FoodItem object is valid
	 */
	 public boolean validate() {
		 LOGGER.trace("validate Method Called");
		 if(foodName == null) return false;
		 if(category == null) return false;
		 if(healthValue <= 0) return false;
		 if(prepTime < 0) return false;
		 if(recipe == null) return false;
		 if(ingredients == null) return false;
		 LOGGER.debug("FoodItem Validated");
		 return true;
	 }
		
}
