package com.foodmenuappsvr.model.services.jdbcservice;
/**
 * 
 * @author zach
 *
 * Adapted from Prof. Ishmael, MSSE672, Regis University -- Example EPedigreeThreadedServers Application
 */
public class JDBCServer {
	
	  private  String jDBCDriver;
	  private  String jDBCUrl;
	  private  String jDBCUser;
	  private  String jDBCPassword;
	    
	    public void setJDBCDriver(String jDBCDriver ){
		 
		 this.jDBCDriver = jDBCDriver;
		 	 
	 } // end of setJDBCDriver
	    
	    public void setJDBCUrl(String jDBCUrl){
		 
		 this.jDBCUrl = jDBCUrl;
		 	 
	 } // end of setJDBCUrl
	    
	    public void setJDBCUser(String jDBCUser){
		 
		 this.jDBCUser = jDBCUser;
		 	 
	 } // end of setJDBCUser
	    
	    public void setJDBCPassword(String jDBCPassword){
		 
		 this.jDBCPassword = jDBCPassword;
		 	 
	 } // end of setJDBCPassword
	    
	    public String getJDBCDriver(String driver){
		 
		 driver = jDBCDriver;
		 	 
		 return driver;
	 } // end of getJDBCDriver

	public String getJDBCUrl(String url){
		 
		 url = jDBCUrl;
		 	 
		 return url;
	 } // end of getJDBCUrl

	public String getJDBCUser(String user){
		 
		 user = jDBCUser;
		 	 
		 return user;
	 } // end of getJDBCUser

	public String getJDBCPassword(String password){
		 
		 password = jDBCPassword;
		 	 
		 return password;
	 } // end of getJDBCPassword

} // end of JDBCServer
