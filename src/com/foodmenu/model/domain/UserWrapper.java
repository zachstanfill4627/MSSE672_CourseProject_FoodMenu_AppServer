package com.foodmenu.model.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class UserWrapper implements Serializable {
	/**
	 * Request Types:
	 *   1 = createUser (Untrusted)
	 *   2 = createUser (Trusted)
	 *   3 = authenticateUser
	 *   4 = resetPassword (Untrusted)
	 *   5 = resetPassword (Trusted)
	 *   6 = deleteUser
	 * 
	 *   7 = retrieveUser
	 *   8 = retrieveAuthenticatedUser
	 *   9 = retrieveAllUsers
	 *   
	 *   10 = responses
	 */
	private int requestType;
	private String email; 
	private String password;
	private String authToken;
	private boolean response;
	private String miscString;
	private int age;
	private User user;
	private User authenticatedUser;
	private ArrayList<User> users;
	
	/** Default Constructor */
	public UserWrapper() {
		
	}
	
	/** 
	 * Overloaded Constructor
	 * Used to create a user (Untrusted)
	 * Used to retrieve all users
	 */
	public UserWrapper(int requestType, User user, String miscString) {
		this.requestType = requestType;
		this.user = user;
		this.miscString = miscString;
	}
	
	/** 
	 * Overloaded Constructor
	 * Used to create a user (Trusted)
	 * Used to delete a user
	 */
	public UserWrapper(int requestType, User user, User authenticatedUser, String authToken) {
		this.requestType = requestType;
		this.user = user;
		this.authenticatedUser = authenticatedUser;
		this.authToken = authToken;
	}
	
	/** 
	 * Overloaded Constructor
	 * Used to authenticate a user
	 */
	public UserWrapper(int requestType, String email, String password) {
		this.requestType = requestType;
		this.email = email;
		this.password = password;
	}
	
	/** 
	 * Overloaded Constructor
	 * Used to reset a users password (Untrusted)
	 */
	public UserWrapper(int requestType, String email, int age, String miscString, String password) {
		this.requestType = requestType;
		this.email = email;
		this.age = age;
		this.miscString = miscString;
		this.password = password;
	}
	
	/**
	 * Overloaded Constructor
	 * Used to reset a users password (Trusted)
	 */
	public UserWrapper(int requestType, String email, String password, User user, String authToken) {
		this.requestType = requestType;
		this.email = email;
		this.password = password;
		this.user = user;
		this.authToken = authToken;
	}
	
	/**
	 * Overloaded Constructor
	 * Used to retrieve a user
	 * Used to retrieve an authenticated user
	 */
	public UserWrapper(int requestType, String miscString, User authenticatedUser, String authToken) {
		this.requestType = requestType;
		this.miscString = miscString;
		this.authenticatedUser = authenticatedUser;
		this.authToken = authToken;
	}
	
	/**
	 * Overloaded Constructor
	 * Used to retrieve a user
	 * Used to retrieve an authenticated user
	 */
	public UserWrapper(int requestType, String miscString, String email, String authToken) {
		this.requestType = requestType;
		this.miscString = miscString;
		this.email = email;
		this.authToken = authToken;
	}
	
	/**
	 * Overloaded Constructor
	 * Used to respond with a user
	 */
	public UserWrapper(int requestType, boolean response, User user) {
		this.requestType = requestType;
		this.response = response;
		this.user = user;
	}
	
	/**
	 * Overloaded Constructor
	 * Used to respond with all users
	 */
	public UserWrapper(int requestType, boolean response, ArrayList<User> users) {
		this.requestType = requestType;
		this.response = response;
		this.users = users;
	}

	/**
	 * Overloaded Constructor
	 * Used for simple responses
	 */	
	public UserWrapper(int requestType, boolean response) {
		this.requestType = requestType;
		this.response = response;
	}

	/**
	 * Overloaded Constructor
	 * Used for responses with messages
	 */	
	public UserWrapper(int requestType, boolean response, String miscString) {
		this.requestType = requestType;
		this.response = response;
		this.miscString = miscString;
	}

	/**
	 * @return the requestType
	 */
	public int getRequestType() {
		return requestType;
	}

	/**
	 * @param requestType the requestType to set
	 */
	public void setRequestType(int requestType) {
		this.requestType = requestType;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the authToken
	 */
	public String getAuthToken() {
		return authToken;
	}

	/**
	 * @param authToken the authToken to set
	 */
	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	/**
	 * @return the response
	 */
	public boolean getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(boolean response) {
		this.response = response;
	}

	/**
	 * @return the miscString
	 */
	public String getMiscString() {
		return miscString;
	}

	/**
	 * @param miscString the miscString to set
	 */
	public void setMiscString(String miscString) {
		this.miscString = miscString;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the authenticatedUser
	 */
	public User getAuthenticatedUser() {
		return authenticatedUser;
	}

	/**
	 * @param authenticatedUser the authenticatedUser to set
	 */
	public void setAuthenticatedUser(User authenticatedUser) {
		this.authenticatedUser = authenticatedUser;
	}

	/**
	 * @return the users
	 */
	public ArrayList<User> getUsers() {
		return users;
	}

	/**
	 * @param users the users to set
	 */
	public void setUsers(ArrayList<User> users) {
		this.users = users;
	}


}
