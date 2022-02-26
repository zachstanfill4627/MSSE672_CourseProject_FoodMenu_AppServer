package com.foodmenuappsvr.model.services.implreferenceservice;

public class ImplReferenceService {
	private String IDayMenuService;
	private String IFoodItemService;
	private String IMenuItemService;
	private String IUserService;
	/**
	 * @return the iDayMenuService
	 */
	public String getIDayMenuService() {
		return IDayMenuService;
	}
	/**
	 * @param iDayMenuService the iDayMenuService to set
	 */
	public void setIDayMenuService(String iDayMenuService) {
		IDayMenuService = iDayMenuService;
	}
	/**
	 * @return the iFoodItemService
	 */
	public String getIFoodItemService() {
		return IFoodItemService;
	}
	/**
	 * @param iFoodItemService the iFoodItemService to set
	 */
	public void setIFoodItemService(String iFoodItemService) {
		IFoodItemService = iFoodItemService;
	}
	/**
	 * @return the iMenuItemService
	 */
	public String getIMenuItemService() {
		return IMenuItemService;
	}
	/**
	 * @param iMenuItemService the iMenuItemService to set
	 */
	public void setIMenuItemService(String iMenuItemService) {
		IMenuItemService = iMenuItemService;
	}
	/**
	 * @return the iUserService
	 */
	public String getIUserService() {
		return IUserService;
	}
	/**
	 * @param iUserService the iUserService to set
	 */
	public void setIUserService(String iUserService) {
		IUserService = iUserService;
	}
	
	public String implReferenceLookup(String  refString) {
		switch (refString) {
			case "IDayMenuService":
				return IDayMenuService;
			case "IFoodItemService":
				return IFoodItemService;
			case "IMenuItemService":
				return IMenuItemService;
			case "IUserService":
				return IUserService;
			default :
				return "Unknown Impl Value";	
				
		}
	}
	
}
