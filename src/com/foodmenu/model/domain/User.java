package com.foodmenu.model.domain;

import java.io.Serializable;

import org.apache.log4j.Logger;

/**
 * 
 * @author Zach Stanfill
 * Domain User Class
 */
public class User implements Serializable {
	
	private static Logger  LOGGER = Logger.getLogger(User.class);
	
	private static final long serialVersionUID = 1234567L;
	
	/** First Name of the User */
	private String firstName;
	
	/** Last Name of the User */
	private String lastName;
	
	/** Email Address of the User */
	private String emailAddress;
	
	/** Password of the User */
	private String password;
	
	/** Recovery Phrase of the User */
	private String recoveryPhrase;
	
	/** Age of the User */
	private int age;
	
	/** Role of the User */
	private String role;
	
	/** Default Constructor */
	public User () {
		LOGGER.trace("User Default Constructor Called");
	}
	
	/**
	 * Overloaded Constructor
	 * @param firstName
	 * @param lastName
	 * @param emailAddress
	 * @param password
	 * @param recoveryPhrase
	 * @param age
	 * @param role
	 */
	public User (String firstName, String lastName, String emailAddress, 
			String password, String recoveryPhrase, int age, String role) {
		super();
		LOGGER.trace("User Overloaded Constructor (with Password) Called");
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.password = password;
		this.recoveryPhrase = recoveryPhrase;
		this.age = age;
		this.role = role;
		LOGGER.debug(String.format("User Object Instantiated -- FirstName:%s   LastName:%s   "
				+ "Email:%s   RecoveryPhrase:%s   Age:%d   Role:%s", firstName, lastName,
				emailAddress, recoveryPhrase, age, role));
	}
	
	public User (String firstName, String lastName, String emailAddress, String recoveryPhrase,
			int age, String role) {
		super();
		LOGGER.trace("User Overloaded Constructor (without Password) Called");
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.recoveryPhrase = recoveryPhrase;
		this.age = age;
		this.role = role;
		LOGGER.debug(String.format("User Object Instantiated -- FirstName:%s   LastName:%s   "
				+ "Email:%s   RecoveryPhrase:%s   Age:%d   Role:%s", firstName, lastName,
				emailAddress, recoveryPhrase, age, role));
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		LOGGER.trace("getFirstName Method Called");
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		LOGGER.trace("setFirstName Method Called");
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		LOGGER.trace("getLastName Method Called");
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		LOGGER.trace("setLastName Method Called");
		this.lastName = lastName;
	}

	/**
	 * @return the emailAddress
	 */
	public String getEmailAddress() {
		LOGGER.trace("getEmailAddress Method Called");
		return emailAddress;
	}

	/**
	 * @param emailAddress the emailAddress to set
	 */
	public void setEmailAddress(String emailAddress) {
		LOGGER.trace("setEmailAddress Method Called");
		this.emailAddress = emailAddress;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		LOGGER.trace("getPassword Method Called");
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		LOGGER.trace("setPassword Method Called");
		this.password = password;
	}
	
	/**
	 * @return the recoveryPhrase
	 */
	public String getRecoveryPhrase() {
		LOGGER.trace("getRecoveryPhrase Method Called");
		return recoveryPhrase;
	}

	/**
	 * @param recoveryPhrase the recoveryPhrase to set
	 */
	public void setRecoveryPhrase(String recoveryPhrase) {
		LOGGER.trace("setRecoveryPhrase Method Called");
		this.recoveryPhrase = recoveryPhrase;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		LOGGER.trace("getAge Method Called");
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(int age) {
		LOGGER.trace("setAge Method Called");
		this.age = age;
	}

	/**
	 * @return the role
	 */
	public String getRole() {
		LOGGER.trace("getRole Method Called");
		return role;
	}

	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		LOGGER.trace("setRole Method Called");
		this.role = role;
	}
	
	/**
	 * validate() method
	 * @return boolean if all fields for FoodItem object is valid
	 */
	public boolean validate() {
		LOGGER.trace("validate Method Called");
		if(firstName == null) return false;
		if(lastName == null)return false;
		if(emailAddress == null)return false;
		if(password == null)return false;
		if(recoveryPhrase == null)return false;
		if(age <= 0)return false;
		if(role == null)return false;
		LOGGER.debug(String.format("User Validated", emailAddress));
		return true;
	}
	
	/**
	 * validateNoPassword() method
	 * @return boolean if all fields for FoodItem object is valid
	 */
	public boolean validateNoPassword() {
		LOGGER.trace("validate Method Called");
		if(firstName == null) return false;
		if(lastName == null)return false;
		if(emailAddress == null)return false;
		if(recoveryPhrase == null)return false;
		if(age <= 0)return false;
		if(role == null)return false;
		LOGGER.debug(String.format("User Validated", emailAddress));
		return true;
	}
	
}
