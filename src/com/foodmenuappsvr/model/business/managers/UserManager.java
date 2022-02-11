package com.foodmenuappsvr.model.business.managers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.foodmenuappsvr.model.business.exceptions.*;
import com.foodmenuappsvr.model.business.factory.ServiceFactory;
import com.foodmenu.model.domain.User;
import com.foodmenuappsvr.model.services.exceptions.UserServiceException;
import com.foodmenuappsvr.model.services.userservice.IUserService;

/**
 * @author Zach Stanfill
 * Adapted from Prof. Ishmael, MSSE670, Regis University
 */
public class UserManager {
	
	private static Logger  LOGGER = Logger.getLogger(UserManager.class);
	
	private static String propertiesFile = "config/application.properties";
	
	private User user;
	
	/** Default Password Policy Values */
	private static int minLength = 8;
	private static int maxLength = 24;
	private static int charClasses = 4;
	private static int minCharClass = 0;
	private static int iterations = 10000;
	private static int keyLength = 512;
	
	public UserManager() {
		LOGGER.trace("UserManager Default Constructor Called");
	}
	
	public UserManager(User user) {
		LOGGER.trace("UserManager Overloaded Constructor Called");
		this.user = user;
	}
	
	/** 
	 * Use Case : Users-400
	 * Add New User
	 * @throws IOException 
	 */
	public boolean addNewUser(User user) throws ServiceLoadException, 
		UserServiceException, IOException {
		LOGGER.trace("addNewUser Called");
		
		String salt = "";
		String password = user.getPassword();
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IUserService userSvc = (IUserService)serviceFactory.getService("IUserService");
		
		LOGGER.trace("AddNewUser -- Checking Validity of Password");
		LOGGER.info(String.format("AddNewUser -- Validating User %s password", user.getEmailAddress()));
		if(isValidPassword(password)) {
			LOGGER.trace("AddNewUser -- Password Validated");
			
			LOGGER.trace("AddNewUser -- Continuing New User Password Set");
			salt = generateSaltKey();
			
            char[] passwordChars = password.toCharArray();
            byte[] saltBytes = salt.getBytes();
            
            byte[] hashedBytes = hashPassword(passwordChars, saltBytes, iterations, keyLength);
            String hashedString = Hex.encodeHexString(hashedBytes);
            
            user.setPassword(hashedString);
            LOGGER.trace("AddNewUser -- Password Successfully Set for User Object");
		} else {
			LOGGER.trace("AddNewUser -- Password Validation Failed");
			return false;
		}
		
		LOGGER.trace("AddNewUser -- Creating New User in Authentication Database");
		if(userSvc.createUserData(user, salt)) {
			LOGGER.info(String.format("AddNewUser -- New User %s added to Authentication Database", user.getEmailAddress()));
			return true;
		} else {
			LOGGER.error(String.format("AddNewUser -- Failed to add New User %s to Authentication Database", user.getEmailAddress()));
			return false;
		}
	}

	/** 
	 * Use Case : Users-410
	 * Delete Existing User 
	 */
	public boolean deleteUser(User user) throws ServiceLoadException, 
		UserServiceException, UserPrivilegesException {
		LOGGER.trace("deleteUser Called");
		
		if(this.user.getRole().equals("admin")) {
			ServiceFactory serviceFactory = new ServiceFactory();
			IUserService userSvc = (IUserService)serviceFactory.getService("IUserService");
			if(userSvc.deleteUserData(user)) {
				return true;
			} else {
				return false;
			}
		} else {
			LOGGER.error(String.format("User %s isn't an admin, and therefore does not have the appropriate privileges to perform delete task!", user.getEmailAddress()));
			throw new UserPrivilegesException(String.format("User %s isn't an admin, and therefore does not have the \nappropriate privileges to perform delete task!", user.getEmailAddress()));
		}
	}
	
	/**
	 * Use Case : Users-420
	 * Authenticate User
	 */
	public boolean authenticateUser(String email, String password) throws 
		ServiceLoadException, UserServiceException {
		LOGGER.trace("authenticateUser Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IUserService userSvc = (IUserService)serviceFactory.getService("IUserService");
		
		String salt = "";
		
		LOGGER.trace("authenticateUser -- Retrieveing Salt Key");
		salt = userSvc.retrieveUserSaltData(email);
		
		if(salt == null) {
			LOGGER.trace("authenticateUser -- saltkey is null");
			return false;
		}
		
		LOGGER.trace("authenticateUser -- Hashing Password with SaltKey");
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = salt.getBytes();
        
        byte[] hashedBytes = hashPassword(passwordChars, saltBytes, iterations, keyLength);
        String hashedString = Hex.encodeHexString(hashedBytes);
        LOGGER.debug("authenticateUser -- hashed password successfully");
		
		if(userSvc.authenticateUserData(email, hashedString)) {
			LOGGER.info(String.format("authenticateUser -- Authentication Succeded", email));
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Use Case : Users-430
	 * Retrieve User
	 */
	public User retrieveUser(String email) throws 
		ServiceLoadException, UserServiceException {
		LOGGER.trace("retrieveUser Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IUserService userSvc = (IUserService)serviceFactory.getService("IUserService");
		return userSvc.retrieveUserData(email);	
	}
	
	/**
	 * Use Case : Users-440
	 * Retrieve All Users
	 */
	public ArrayList<User> retrieveAllUsers() throws 
		ServiceLoadException, UserServiceException {
		LOGGER.trace("retrieveAllUsers Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IUserService userSvc = (IUserService)serviceFactory.getService("IUserService");
		return userSvc.retrieveAllUserData();	
	}
	
	/**
	 * Use Case : Users-450
	 * Reset User Password -- Trusted
	 * @throws IOException 
	 */
	public boolean resetUserPassword(User user) throws 
		ServiceLoadException, UserServiceException, IOException {
		LOGGER.trace("resetUserPassword (Trusted) Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IUserService userSvc = (IUserService)serviceFactory.getService("IUserService");
		
		String salt = "";
		
		salt = userSvc.retrieveUserSaltData(user.getEmailAddress());
		
        char[] passwordChars = user.getPassword().toCharArray();
        byte[] saltBytes = salt.getBytes();
        
        byte[] hashedBytes = hashPassword(passwordChars, saltBytes, iterations, keyLength);
        String hashedString = Hex.encodeHexString(hashedBytes);
        
        user.setPassword(hashedString);
        
		LOGGER.trace("resetUerPassword (Trusted) -- UpdatingUserPasswordData in the Database");
		return userSvc.updateUserPasswordData(user);	
	}
	
	/**
	 * Use Case : Users-451
	 * Reset User Password -- Untrusted - Verifying Identity
	 * @throws UserServiceException 
	 */
	public boolean resetUserPassword(String email, int age, String recPhrase) 
			throws ServiceLoadException, UserServiceException {
		LOGGER.trace("resetUserPassword (Untrusted - Needs Verification) Called");

		ServiceFactory serviceFactory = new ServiceFactory();
		IUserService userSvc = (IUserService)serviceFactory.getService("IUserService");
		
		LOGGER.trace("resetUerPassword (Untrusted) -- UpdatingUserPasswordData in the Database");
		return userSvc.resetUserPasswordData(email, age, recPhrase);	
	}
	
	/**
	 * Use Case : Users-451
	 * Reset User Password -- Untrusted - Verified
	 * @throws UserServiceException ServiceLoadException 
	 * @throws IOException 
	 */
	public boolean resetUserPassword(String email, String password) 
			throws UserServiceException, ServiceLoadException, IOException {
		LOGGER.trace("resetUserPassword (Untrusted - Verified) Called");
		
		ServiceFactory serviceFactory = new ServiceFactory();
		IUserService userSvc = (IUserService)serviceFactory.getService("IUserService");
		
		User user = userSvc.retrieveUserData(email);
		
		String salt = "";

		LOGGER.info(String.format("resetUserPassword (Untrusted - Verified) -- Validating User %s password", user.getEmailAddress()));
		if(isValidPassword(password)) {
			salt = userSvc.retrieveUserSaltData(user.getEmailAddress());
			
	        char[] passwordChars = user.getPassword().toCharArray();
	        byte[] saltBytes = salt.getBytes();
	        
	        byte[] hashedBytes = hashPassword(passwordChars, saltBytes, iterations, keyLength);
	        String hashedString = Hex.encodeHexString(hashedBytes);
	        
	        user.setPassword(hashedString);

	        LOGGER.trace("resetUerPassword (Untrusted - Verified) -- UpdatingUserPasswordData in the Database");
			return userSvc.updateUserPasswordData(user);
		} else {
			LOGGER.trace(String.format("User %s, Password was not updated because it doesn't meet the password complexity requirements", user.getEmailAddress()));
			return false;
		}
	}
	
    /**
     * Read Properties Files
     */
	private static void readProperties() throws IOException {
		LOGGER.trace("readProperties Called");
		
		/** Read Configured Properties */
		try (InputStream input = new FileInputStream(propertiesFile)) {
            Properties prop = new Properties();
            prop.load(input);
            LOGGER.trace("readProperties -- Properties read successfully");
                        
            if(prop.getProperty("password.minLength") != null) {
            	minLength = Integer.parseInt(prop.getProperty("password.minLength"));
            }
            if(prop.getProperty("password.maxLength") != null) {
            	maxLength = Integer.parseInt(prop.getProperty("password.maxLength"));
            }
            if(prop.getProperty("password.charClasses") != null) {
            	charClasses = Integer.parseInt(prop.getProperty("password.charClasses"));
            }
            if(prop.getProperty("password.minCharClass") != null) {
            	minCharClass = Integer.parseInt(prop.getProperty("password.minCharClass"));
            }

            /** Validate Password Parameters */
            if(minLength < 0 || minLength > maxLength) {
            	System.err.println("Invalid password.minLength ; Setting Value to 8.");
            	minLength = 8;
            }
            if((maxLength > 50)) {
            	System.err.println("Invalid password.maxLength ; Setting Value to 24.");
            	maxLength = 24;
            }
            if(!(charClasses >= 1 && charClasses <= 4)) {
            	System.err.println("Invalid password.charClasses ; Setting Value to 4.");
            	charClasses = 4;	
            }
            if((charClasses*minCharClass) > maxLength) {
        		System.err.println("Invalid password.minCharClass ; Setting Value to 0");
        		minCharClass = 0;	
        	}
            
            LOGGER.info(String.format("Password Policy set from properties file: "
            		+ "MinLength:%d   MaxLength:%s   charClasses:%d   minCharClass:%d",
            		minLength, maxLength, charClasses, minCharClass));
			
		} catch (Exception e) {
			minLength = 8;
			maxLength = 24;
			charClasses = 4;
			minCharClass = 0;
			iterations = 10000;
			keyLength = 512;
			LOGGER.warn(String.format("Error setting password policy from properties file."
					+ "Default password policy implemented: "
            		+ "MinLength:%d   MaxLength:%s   charClasses:%d   minCharClass:%d",
            		minLength, maxLength, charClasses, minCharClass));
			System.err.println("Error in reading property file password values, setting to default values!");
		}
		
		
		
	}
	
	/**
     * Validate Password against Policy
     * @param password
     * @return boolean
     * @throws NumberFormatException 
	 * @throws IOException 
     */
    public static boolean isValidPassword(String password) throws NumberFormatException, IOException {
    	LOGGER.trace("isValidPassword Called");

    	readProperties();
    	
    	/** Verify the password length is within the specified parameters */
    	if (password.length() < minLength || password.length() > maxLength) {
    		System.err.println("Invalid Password Length");
    		LOGGER.error("PasswordValidation -- Password Invalid : Password Length Requriements ");
    		return false;
    	}
    	
    	/** Initialize Counter Variables */
		int upper = 0, lower = 0, number = 0, special = 0;
		
		/** Initialize Available Special Characters for use in Passwords */
		String specialCharacters = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
		ArrayList<Character>specChar = new ArrayList<Character>();
		for(int c = 0; c < specialCharacters.length(); c++) { 
			specChar.add(specialCharacters.charAt(c));
		}

		/** Count Character Classes */
		for(int c = 0; c < password.length(); c++) { 
			if (password.charAt(c) >= 'A' && password.charAt(c) <= 'Z') { upper++; }
			else if (password.charAt(c) >= 'a' && password.charAt(c) <= 'z') { lower++; }
			else if (password.charAt(c) >= '0' && password.charAt(c) <= '9') { number++; }
			else if (specChar.indexOf(password.charAt(c)) > -1) { special++; }
		}
		
		/** Validate Password Meets Parameters */
		if(charClasses == 1) {
			if(upper > 0 || lower > 0 || special > 0) {
				System.err.println("Invalid Password Characters. Password can only contain Numbers [0-9].");
				LOGGER.error("PasswordValidation -- Password Invalid : Password Characters. Password can only contain Numbers [0-9].");
				return false;
			} else if (!(number >= minCharClass)) {
				System.err.println("Password does not contain the Minimum Number of Characters from the Number [0-9] Character Class.");
				LOGGER.error("PasswordValidation -- Password Invalid : Password Characters. Password does not contain the Minimum Number of Characters from the Number [0-9] Character Class.");
				return false;
			}
		} else if (charClasses == 2 ) {
			if(number > 0 || special > 0) {
				System.err.println("Invalid Password Characters. Password can only contain Alphabetic Characters [a-zA-Z].");
				LOGGER.error("PasswordValidation -- Password Invalid : Password Characters. Password can only contain Alphabetic Characters [a-zA-Z].");
				return false;
			} else if (!(upper >= minCharClass && lower >= minCharClass)) {
				System.err.println("Password does not contain the Minimum Number of Characters from each Character Class.");
				LOGGER.error("PasswordValidation -- Password Invalid : Password Characters. Password does not contain the Minimum Number of Characters from each Character Class.");
				return false;
			}
		} else if (charClasses == 3 ) {
			if(special > 0) {
				System.err.println("Invalid Password Characters. Password can only contain Alphanumeric Characters [a-zA-Z0-9].");
				LOGGER.error("PasswordValidation -- Password Invalid : Password Characters. Password can only contain Alphanumeric Characters [a-zA-Z0-9].");
				return false;
			} else if (!(upper >= minCharClass && lower >= minCharClass && number >= minCharClass)) {
				System.err.println("Password does not contain the Minimum Number of Characters from each Character Class.");
				LOGGER.error("PasswordValidation -- Password Invalid : Password Characters. Password does not contain the Minimum Number of Characters from each Character Class.");
				return false;
			}
		} else if (charClasses == 4 ) {
			if(!(upper >= minCharClass && lower >= minCharClass && number >= minCharClass && special >= minCharClass)) {
				System.err.println("Password does not contain the Minimum Number of Characters from each Character Class.");
				LOGGER.error("PasswordValidation -- Password Invalid : Password Characters. Password does not contain the Minimum Number of Characters from each Character Class.");
				System.err.printf("\tUpper: %d\n\tLower: %d\n\tNumbers: %d\n\tSpecial: %d\n", upper, lower, number, special);
				return false;
			}
		}
			
        return true;
    }
    
	/**
	 * Generate Salt Key
	 * @return salt key
	 */
	private static String generateSaltKey() {
		LOGGER.trace("generateSaltKey Called");
	    int leftLimit = 48; // numeral '0'
	    int rightLimit = 122; // letter 'z'
	    int targetStringLength = 32;
	    Random random = new Random();

	    String salt = random.ints(leftLimit, rightLimit + 1)
	      .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
	      .limit(targetStringLength)
	      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
	      .toString();
	    
	    return salt;
	}


    /**
     * Hash Password
     * @param password
     * @param salt
     * @param iterations
     * @param keyLength
     * @return
     */
    private static byte[] hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) {
    	LOGGER.trace("hashPassword Called");

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return res;
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }
	
	
}
