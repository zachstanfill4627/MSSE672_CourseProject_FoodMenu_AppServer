package com.foodmenu.model.domain;

import java.io.Serializable;

public class DeleteWrapper implements Serializable {
	private int typeId = 0;
	private Object obj = null;
	
	// Default Constructor
	public DeleteWrapper() {
		
	}
	
	// Overloaded Constructor
	public DeleteWrapper(int typeId, Object obj) {
		super();
		this.typeId = typeId;
		this.obj = obj;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}
	
	public boolean validate() {
		if (typeId != 0 && obj != null) { 
			return true;
		} else { 
			return false; 
		}
	}
}