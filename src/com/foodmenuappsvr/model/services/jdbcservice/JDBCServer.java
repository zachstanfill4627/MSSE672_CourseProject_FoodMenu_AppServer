package com.foodmenuappsvr.model.services.jdbcservice;
/**
 * 
 * @author zach
 *
 * Adapted from Prof. Ishmael, MSSE672, Regis University -- Example EPedigreeThreadedServers Application
 */
public class JDBCServer {
	private String jdbcURL;
	private String jdbcUser;
	private String jdbcPassword;
	
	/**
	 * @return the jdbcURL
	 */
	public String getJdbcURL() {
		return jdbcURL;
	}
	/**
	 * @param jdbcURL the jdbcURL to set
	 */
	public void setJdbcURL(String jdbcURL) {
		this.jdbcURL = jdbcURL;
	}
	/**
	 * @return the jdbcUser
	 */
	public String getJdbcUser() {
		return jdbcUser;
	}
	/**
	 * @param jdbcUser the jdbcUser to set
	 */
	public void setJdbcUser(String jdbcUser) {
		this.jdbcUser = jdbcUser;
	}
	/**
	 * @return the jdbcPassword
	 */
	public String getJdbcPassword() {
		return jdbcPassword;
	}
	/**
	 * @param jdbcPassword the jdbcPassword to set
	 */
	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}

} // end of JDBCServer
