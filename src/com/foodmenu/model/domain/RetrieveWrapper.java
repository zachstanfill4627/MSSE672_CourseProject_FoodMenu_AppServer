package com.foodmenu.model.domain;

import java.io.Serializable;

public class RetrieveWrapper implements Serializable {
	private int typeId = 0;
	private boolean flag = false;
	private String searchParam = "";
	
	// Default Constructor
	public RetrieveWrapper() {
		
	}
	
	// Overloaded Constructor
	public RetrieveWrapper(int typeId) {
		super();
		this.typeId = typeId;
	}
	
	// Overloaded Constructor
	public RetrieveWrapper(int typeId, boolean flag, String searchParam) {
		super();
		this.typeId = typeId;
		this.flag = flag;
		this.searchParam = searchParam;
	}


	public int getTypeId() {
		return typeId;
	}


	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}


	public boolean getFlag() {
		return flag;
	}


	public void setList(boolean flag) {
		this.flag = flag;
	}


	public String getSearchParam() {
		return searchParam;
	}


	public void setSearchParam(String searchParam) {
		this.searchParam = searchParam;
	}
	
	public boolean validate() {
		if (typeId != 0) {
			if (!flag) {
				return true;
			} else if (flag && !searchParam.equals("")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
}
