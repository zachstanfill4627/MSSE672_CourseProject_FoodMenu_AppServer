package com.foodmenuappsvr.model.services.userservice;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.foodmenu.model.domain.User;
import com.foodmenuappsvr.model.services.exceptions.UserServiceException;

public class UserSvcImpl implements IUserService {
	
	private static Logger  LOGGER = Logger.getLogger(UserSvcImpl.class);
	
	private static String propertiesFile = "config/application.properties";

	private static String connString, dbUsername, dbPassword;
	
	public UserSvcImpl() {
		LOGGER.trace("UserSvcImpl Default Constructor Called");
		
		try (InputStream input = new FileInputStream(propertiesFile)) {
            Properties prop = new Properties();
            prop.load(input);
            if(prop.getProperty("dbconnect.string") != null){
            	connString = prop.getProperty("dbconnect.string");
            	LOGGER.debug(String.format("Database Connection String = %s", connString));
            } else throw new Exception("dbconnect.string not present in properties file"); 
            if(prop.getProperty("dbconnect.string") != null){
            	dbUsername = prop.getProperty("dbconnect.user");
            	LOGGER.debug(String.format("Database Username = %s", dbUsername));
            } else throw new Exception("dbconnect.user not present in properties file");
            if(prop.getProperty("dbconnect.string") != null){
            	dbPassword = prop.getProperty("dbconnect.password");
            	LOGGER.debug(String.format("Database Password = %s", dbPassword));
            } else throw new Exception("dbconnect.password not present in properties file");
            
            LOGGER.trace("Successfully read database connection properties from properties files");
		} catch (Exception e) {
			System.err.println("Error in reading property file database connection values, Exiting!");
			System.err.println(e);
			LOGGER.fatal(e);
			System.exit(1);
		}
	}

	public boolean createUserData(User user, String salt) throws UserServiceException {
		LOGGER.trace("createUserData Called");
		
		/** Localize Variables */
		String fName = user.getFirstName();
		String lName = user.getLastName();
		String email = user.getEmailAddress();
		String recPhr = user.getRecoveryPhrase();
		int age = user.getAge();
		String role = user.getRole();
		String pass = user.getPassword();
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Insert Password Record into Info Table */
		strBfr.append(String.format("INSERT INTO info (infotext) VALUES (\"%s\");", 
				pass));
		String sql1 = strBfr.toString();
		strBfr.setLength(0);
		
		/** SQL Statement 2, Insert Password Record into Info Table */
		strBfr.append(String.format("INSERT INTO salt (salttext) VALUES (\"%s\");", 
				salt));
		String sql2 = strBfr.toString();
		strBfr.setLength(0);
		
		/** SQL Statement 2, Insert User data into Users Table */
		strBfr.append(String.format("INSERT INTO users (firstname, lastname, "
				+ "email, recoveryphrase, age, role, infoid, saltid) "
				+ "VALUES (\"%s\", \"%s\",\"%s\", \"%s\", %d, %s, %s, %s);",
				fName, lName, email, recPhr, age, 
				"(SELECT roleid FROM roles WHERE rolename = \"" + role + "\")", 
				"(SELECT MAX(infoid) FROM info LIMIT 1)",
				"(SELECT MAX(saltid) FROM salt LIMIT 1)"));
		String sql3 = strBfr.toString();
		strBfr.setLength(0);		
		
		
		/** SQL Statement 3, Query database - Check User Data */
		strBfr.append(String.format("SELECT userid, firstname, lastname, email, recoveryphrase, age, rolename, infotext, salttext "
				+ "FROM users "
				+ "INNER JOIN info ON users.infoid = info.infoid "
				+ "INNER JOIN salt ON users.saltid = salt.saltid "
				+ "INNER JOIN roles ON users.role = roles.roleid "
				+ "WHERE email = \"%s\";", email));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("createUserData -- SQL Statements:");
		LOGGER.debug(sql1);
		LOGGER.debug(sql2);
		LOGGER.debug(sql3);
		LOGGER.debug(query);
		
				
		/** Connect to Database & Execute SQL Statements & Check Accuracy */
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
			conn.setAutoCommit(false);
            
			/** Execute SQL Insert Statements - Batch Style */
			stmt.addBatch(sql1);
            stmt.addBatch(sql2);
            stmt.addBatch(sql3);
            LOGGER.info(String.format("createUserData -- Added user %s to database", user.getEmailAddress()));
            
            stmt.executeBatch();
            LOGGER.trace("SQL Statements Executed");
            
            
            /** Commit Changes */ 
            conn.commit();
            LOGGER.trace("SQL Statements Commited");
            
            /** Run SQL Query against newly added record */
            ResultSet rs = stmt.executeQuery(query);
            LOGGER.info("createUserData -- Begin Verifying User was added correctly");
            
            /** Verify Userdata in database matches User Object */
            rs.next();
            if(rs.getInt("userid") <= 0) { return false; };
    		if(!rs.getString("firstname").equals(fName)) { return false; };
    		if(!rs.getString("lastname").equals(lName)) { return false; };
    		if(!rs.getString("email").equals(email)) { return false; };
    		if(!rs.getString("recoveryphrase").equals(recPhr)) { return false; };
    		if(rs.getInt("age") <= 0 ) { return false; };
    		if(!rs.getString("rolename").equals(role)) { return false; };
    		if(!rs.getString("infotext").equals(pass)) { return false; };
    		if(!rs.getString("salttext").equals(salt)) { return false; };

    		LOGGER.info("createUserData -- Successfully Verified User was added correctlly");
    		
    		/** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
        } catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
        	LOGGER.error(e.getMessage());
            return false;
        }
		
		/** If Successful, Return True */
		LOGGER.info(String.format("User %s successfully added to the database", user.getEmailAddress()));
		return true;
	}
	
	public User retrieveUserData(String email) throws UserServiceException {
		LOGGER.trace("retrieveUserData Called");
		
		/** Localize Variables */
		String fName = "";
		String lName = "";
		String recPhr = "";
		int age = 0;
		String role = ""; 
		String pass = "";
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Select Record from Users Table */
		strBfr.append(String.format("SELECT userid, firstname, lastname, email,"
				+ " recoveryphrase, age, rolename, infotext FROM ((users INNER "
				+ "JOIN info ON users.infoid = info.infoid) INNER JOIN roles "
				+ "ON users.role = roles.roleid) WHERE email = "
				+ "\"%s\";", email));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("retrieveUserData -- SQL Statements:");
		LOGGER.debug(query);
		
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
            
            /** Run SQL Query against Users Table */
            ResultSet rs = stmt.executeQuery(query);
            
            if(rs.next()) {            	
	            /** Assign Query Return to variables */
	            fName = rs.getString("firstname");
	            lName = rs.getString("lastname");
	            recPhr = rs.getString("recoveryphrase");
	            age = rs.getInt("age");
	            role = rs.getString("rolename"); 
	            pass = rs.getString("infotext");
            } else {
            	return null;
            }
                        
    		/** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
        } catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
        	LOGGER.error(e.getMessage());
        	return null;
        }
		
		/** Create User Object */
		User user = new User(fName, lName, email, pass, recPhr, age, role);
		LOGGER.info(String.format("User %s successfully retrieved from database", user.getEmailAddress()));
		
		/** Return User Object */ 
		return user;
	}
	
	public String retrieveUserSaltData(String email) {
		LOGGER.trace("retrieveUserSaltData Called");
		
		String salt = "";
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Select Record from Users Table */
		strBfr.append(String.format("SELECT salttext FROM users "
				+ "INNER JOIN salt ON users.saltid = salt.saltid "
				+ "WHERE email = \"%s\";", email));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("retrieveUserSaltData -- SQL Statements:");
		LOGGER.debug(query);
		
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {          
			LOGGER.trace("Database Connection Opened");
            
            /** Run SQL Query against Users Table */
            ResultSet rs = stmt.executeQuery(query);
            
            if(rs.next()) {            	
	            /** Assign Query Return to variables */
	            salt = rs.getString("salttext");
            } else {
            	return null;
            }
                        
    		/** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
        } catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
        	LOGGER.trace(e.getMessage());
        	return null;
        }
		
		LOGGER.info(String.format("User SaltData %s successfully retrieved from database", email));
		return salt;
	}
	
	public ArrayList<User> retrieveAllUserData() {
		LOGGER.trace("retrieveAllUserData Called");
		
		ArrayList<User> users = new ArrayList<User>();
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Select Record from Users Table */
		strBfr.append(String.format("SELECT firstname, lastname, email, recoveryphrase, "
				+ "age, rolename FROM users INNER JOIN roles ON "
				+ "users.role = roles.roleid;"));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("retrieveAllUserData -- SQL Statements:");
		LOGGER.debug(query);
		
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
			
            
            /** Run SQL Query against Users Table */
            ResultSet rs = stmt.executeQuery(query);
            
            while(rs.next()) {
            	users.add(new User(rs.getString("firstname"), rs.getString("lastname"), rs.getString("email"), rs.getString("recoveryphrase"), rs.getInt("age"), rs.getString("rolename")));
            }
            
    		/** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
            
            return users;
		} catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
        	LOGGER.error(e.getMessage());
        	return users;
        }
	}
	
	public boolean updateUserData (User user) throws UserServiceException {
		LOGGER.trace("updateUserData Called");
		
		/** Localize Variables */
		String fName = user.getFirstName();
		String lName = user.getLastName();
		String email = user.getEmailAddress();
		String recPhr = user.getRecoveryPhrase();
		int age = user.getAge();
		String role = user.getRole();
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
					
		/** SQL Statement 1, Update User data in Users Table */
		strBfr.append(String.format("UPDATE users SET firstname = '%s', "
				+ "lastname = '%s', recoveryphrase = '%s', age = %d, role = %s"
				+ "WHERE email = \"%s\";",
				fName, lName, recPhr, age,
				"(SELECT roleid FROM roles WHERE rolename = \"" + role + "\")",
				email));
		String sql1 = strBfr.toString();
		strBfr.setLength(0);		
		
		/** SQL Statement 2, Query database - Check User Data */
		strBfr.append(String.format("SELECT userid, firstname, lastname, email,"
				+ " recoveryphrase, age, rolename FROM (users INNER "
				+ "JOIN roles ON users.role = roles.roleid) WHERE email = "
				+ "\"%s\";", email));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("updateUserData -- SQL Statements:");
		LOGGER.debug(sql1);
		LOGGER.debug(query);
		
		/** Connect to Database & Execute SQL Statements & Check Accuracy */
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
			conn.setAutoCommit(false);
            
			/** Execute SQL Update Statements - Batch Style */
			stmt.addBatch(sql1);
            stmt.executeBatch();
            
            /** Commit Changes */ 
            conn.commit();          
            
            /** Run SQL Query against modified record */
            ResultSet rs = stmt.executeQuery(query);

            if(rs.next()) {
	            /** Verify Userdata in database matches User Object */
	            if(rs.getInt("userid") <= 0) { return false; };
	    		if(!rs.getString("firstname").equals(fName)) { return false; };
	    		if(!rs.getString("lastname").equals(lName)) { return false; };
	    		if(!rs.getString("email").equals(email)) { return false; };
	    		if(!rs.getString("recoveryphrase").equals(recPhr)) { return false; };
	    		if(rs.getInt("age") <= 0 ) { return false; };
	    		if(!rs.getString("rolename").equals(role)) { return false; };
	    		LOGGER.trace("Update User Data Verified");
            } else {
            	return false;
            }
    		/** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
        } catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
        	LOGGER.error(e.getMessage());
            return false;
        }
		
		/** If Successful, Return True */
		return true;
	}
	
	public boolean updateUserPasswordData (User user) throws UserServiceException {
		LOGGER.trace("updateUserPasswordData Called");
		
		/** Localize Variables */
		String email = user.getEmailAddress();
		String pass = user.getPassword();
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Insert Password Record into Info Table */
		strBfr.append(String.format("INSERT INTO info (infotext) VALUES (\"%s\");", 
				pass));
		String sql1 = strBfr.toString();
		strBfr.setLength(0);
		
		/** SQL Statement 2, Update User data in Users Table */
		strBfr.append(String.format("UPDATE users SET infoid = "
				+ "(SELECT MAX(infoid) FROM info LIMIT 1)"
				+ "WHERE email = \"%s\";", email));
		String sql2 = strBfr.toString();
		strBfr.setLength(0);
		
		/** Database Cleanup 
		 * BUG -- SQLite Database Table Configured to ON DELETE CASCADE, however 
		 * cascade is not properly working, therefore manual DELETE Statements
		 * complete database cleanup tasks
		 */
		String sql3 = "DELETE FROM info WHERE infoid NOT IN (SELECT "
				+ "DISTINCT infoid FROM users);";
		
		/** SQL Statement 3, Query database - Check User Data */
		strBfr.append(String.format("SELECT email, infotext FROM users INNER "
				+ "JOIN info ON users.infoid = info.infoid WHERE email = "
				+ "\"%s\";", email));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("udpateUserPasswordData -- SQL Statements:");
		LOGGER.debug(sql1);
		LOGGER.debug(sql2);
		LOGGER.debug(sql3);
		LOGGER.debug(query);
		
		/** Connect to Database & Execute SQL Statements & Check Accuracy */
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
			conn.setAutoCommit(false);
            
			/** Execute SQL Statements - Batch Style */
			stmt.addBatch(sql1);
            stmt.addBatch(sql2);
            stmt.addBatch(sql3);
            stmt.executeBatch();
            
            /** Commit Changes */ 
            conn.commit();
            LOGGER.trace("SQL Statements Commited");
            
            /** Run SQL Query against newly added record */
            ResultSet rs = stmt.executeQuery(query);

            /** Verify Userdata in database matches User Object */
            rs.next();
    		if(!rs.getString("email").equals(email)) { return false; };
    		if(!rs.getString("infotext").equals(pass)) { return false; };
    		LOGGER.info("User Password Update Verified");
            
    		/** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Opened");
        } catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
            return false;
        }
		
		/** If Successful, Return True */
		return true;
	}
	
	public boolean resetUserPasswordData(String email, int age, String recPhrase) throws UserServiceException {
		LOGGER.trace("restUserPasswordData Called");
		
		User user = retrieveUserData(email);
		
		if (user == null) {
			return false;
		}
		
		if (recPhrase.equals(user.getRecoveryPhrase()) && age == user.getAge()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean deleteUserData (User user) throws UserServiceException {
		LOGGER.trace("deleteUserData Called");
		
		/** Localize Variables */
		String email = user.getEmailAddress();
		
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
					
		/** SQL Statement 1, Delete User data in Users Table */
		strBfr.append(String.format("DELETE FROM users WHERE email = \"%s\";", 
				email));
		String sql1 = strBfr.toString();
		strBfr.setLength(0);
		
		/** SQL Statement 2, Update User data in Users Table */
		strBfr.append(String.format("SELECT * FROM users WHERE email = \"%s\";", 
				email));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		/** Database Cleanup 
		 * BUG -- SQLite Database Table Configured to ON DELETE CASCADE, however 
		 * cascade is not properly working, therefore manual DELETE Statements
		 * complete database cleanup tasks
		 */ 
		String sql2 = "DELETE FROM info WHERE infoid NOT IN (SELECT "
				+ "DISTINCT infoid FROM users);";
		String sql3 = "DELETE FROM salt WHERE saltid NOT IN (SELECT "
				+ "DISTINCT saltid FROM users);";
		
		LOGGER.debug("udpateUserPasswordData -- SQL Statements:");
		LOGGER.debug(sql1);
		LOGGER.debug(sql2);
		LOGGER.debug(sql3);
		LOGGER.debug(query);
		
		/** Connect to Database & Execute SQL Statements & Check Accuracy */
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
			conn.setAutoCommit(false);
            
			/** Execute SQL Statements - Batch Style */
			stmt.addBatch(sql1);
			stmt.addBatch(sql2);
			stmt.addBatch(sql3);
            stmt.executeBatch();
            
            /** Commit Changes */ 
            conn.commit();
            LOGGER.trace("SQL Statements Commited");
            
            /** Run SQL Query against record */
            ResultSet rs = stmt.executeQuery(query);
            
            if(rs.next()) { return false; };
            LOGGER.trace("User Delete Verified");
            
            /** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
        } catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
        	LOGGER.trace(e.getMessage());
            return false;
        }
		
		/** If Successful, Return True */
		LOGGER.info(String.format("User %s successfully deleted from  database", user.getEmailAddress()));
		return true;
	}
	
	public boolean authenticateUserData (String email, String password) throws UserServiceException {
		LOGGER.trace("authenticateUserData Called");
		/** Re-usable String Buffer for SQL Statement instantiation */ 
		StringBuffer strBfr = new StringBuffer();
		
		/** SQL Statement 1, Insert Password Record into Info Table */
		strBfr.append(String.format("SELECT infotext FROM users INNER JOIN info"
				+ " ON users.infoid = info.infoid WHERE email = \"%s\";", 
				email));
		String query = strBfr.toString();
		strBfr.setLength(0);
		
		LOGGER.debug("authenticateUserData -- SQL Statements:");
		LOGGER.debug(query);
		
		/** Connect to Database & Execute SQL Statements & Check Accuracy */
		try (Connection conn = DriverManager.getConnection(connString, dbUsername, dbPassword);
                Statement stmt = conn.createStatement()) {
			LOGGER.trace("Database Connection Opened");
            
            /** Run SQL Query against modified record */
            ResultSet rs = stmt.executeQuery(query);

            if (rs.next()) {
            	/** Verify Userdata in database matches User Object */
            	if(!rs.getString("infotext").equals(password)) { 
            		return false; 
            	};
            } else { 
            	return false;
            }
            
    		/** Close Database Connection */
            conn.close();
            LOGGER.trace("Database Connection Closed");
        } catch (SQLException e) {
        	/** Error Output */
        	System.err.println(e.getMessage());
        	LOGGER.trace(e.getMessage());
            return false;
        }
		
		/** If Successful, Return True */
		LOGGER.trace(String.format("User %s Successfully Authenticated", email));
		return true;
	}


}
