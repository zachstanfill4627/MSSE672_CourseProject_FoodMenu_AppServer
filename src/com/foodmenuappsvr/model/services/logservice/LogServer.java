package com.foodmenuappsvr.model.services.logservice;

/**
 * 
 * @author zach
 *
 * Adapted from Prof. Ishmael, MSSE672, Regis University -- Example EPedigreeThreadedServers Application
 */

public class LogServer {

	private String propFileLocation;
	
	
	public void setPropFileLocation(String propFileLocation ){
		 
		 this.propFileLocation = propFileLocation;
		 	 
	 	} // end of propFileLocation
	
	public String getPropFile (String propFile){
		
		propFile = propFileLocation;
		
		return propFile;
	}
	
	
} // end of LogServer
