package com.foodmenuappsvr.model.services.foodmenuservice;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.foodmenu.model.domain.*;
import com.foodmenuappsvr.model.business.exceptions.ServiceLoadException;
import com.foodmenuappsvr.model.business.exceptions.UserPrivilegesException;
import com.foodmenuappsvr.model.business.managers.*;
import com.foodmenuappsvr.model.services.exceptions.DayMenuServiceException;
import com.foodmenuappsvr.model.services.exceptions.FoodItemServiceException;
import com.foodmenuappsvr.model.services.exceptions.MenuItemServiceException;
import com.foodmenuappsvr.model.services.exceptions.UserServiceException;

/** 
 * @author Zach Stanfill
 * Modeled from GeeksForGeeks.org/multithreaded-servers-in-java 
 */
public class FoodMenuServer {

    public static void main(String[] args) {
        ServerSocket server = null;
  
        try {
  
            // server is listening on port 1234
            server = new ServerSocket(40010);
            server.setReuseAddress(true);
  
            // running infinite loop for getting client request
            while (true) {
  
                // socket object to receive incoming client requests
                Socket client = server.accept();
  
                // Displaying that new client is connected to server
                System.out.println("Spinning up new Thread for Client:  "
                                   + client.getInetAddress().getHostAddress() 
                                   + ":" + client.getPort());
  
                // create a new thread object
                ClientHandler clientSock
                    = new ClientHandler(client);
  
                // This thread will handle the client separately
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
  
    // ClientHandler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        
        private User user;
        private String sessionKey;
        
        private CreateWrapper createWrap = new CreateWrapper();
        private RetrieveWrapper retrieveWrap = new RetrieveWrapper();
        private UpdateWrapper updateWrap = new UpdateWrapper();
        private DeleteWrapper deleteWrap = new DeleteWrapper();
        
    	private UserManager userManager;
    	private FoodItemManager foodItemManager;
    	private MenuItemManager menuItemManager;
    	private DayMenuManager	dayMenuManager;
    	
    	
  
        // Constructor
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
  
        public void run() {
        	
            try {
                OutputStream outputStream = clientSocket.getOutputStream();
                InputStream inputStream = clientSocket.getInputStream();
                
                ObjectOutputStream objectOutputStream =  new ObjectOutputStream(outputStream);
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                
                UserWrapper newUser = (UserWrapper) objectInputStream.readObject();
                
                if(!validateAuthUser(newUser.getEmail(), newUser.getAuthToken())){
                    objectInputStream.close();
                    objectInputStream.close();
    	            clientSocket.close();
                }
                
                System.out.printf("User %s Logged In from %s:%s.\n", user.getEmailAddress(), clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                
                userManager = new UserManager(user);
            	foodItemManager = new FoodItemManager(user);
            	menuItemManager = new MenuItemManager(user);
            	dayMenuManager = new DayMenuManager(user);
            	
                Object obj;
             	
                for (;;) {
                	try {
                		if ((obj = objectInputStream.readObject()) != null) {
                			Response response = new Response();
                			
                			if (obj.getClass().equals(createWrap.getClass())) {
                				System.out.printf("%s from %s:%s | Create Request recieved from Client\n", user.getEmailAddress(), clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                				response = create((CreateWrapper) obj);
                			} else if (obj.getClass().equals(retrieveWrap.getClass())) {
                				System.out.printf("%s from %s:%s | Retrieve Request recieved from Client\n", user.getEmailAddress(), clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                				response = retrieve((RetrieveWrapper) obj);
                			} else if (obj.getClass().equals(updateWrap.getClass())) {
                				System.out.printf("%s from %s:%s | Update Request recieved from Client\n", user.getEmailAddress(), clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                				System.out.println("Server doesn't currently support update requests");
                				response = new Response(false, String.format("Failed to Update the Database -- Not Supported at this time"));
                			} else if (obj.getClass().equals(deleteWrap.getClass())) {
                				System.out.printf("%s from %s:%s | Delete Request recieved from Client\n", user.getEmailAddress(), clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                				response = delete((DeleteWrapper) obj);
                			} else {
                				System.out.println("Unknown Request recieved from Client");
                				response = new Response(false, String.format("Unknown Request recieved from Client"));
                			}
                			
                			objectOutputStream.writeObject(response);
	                	} else {
	                		Thread.sleep(50000);
	                	}
                	} catch (EOFException | InterruptedException | ServiceLoadException | FoodItemServiceException | MenuItemServiceException | DayMenuServiceException | UserServiceException | UserPrivilegesException e) {
                		break;
                	}
                }
                
                System.out.printf("%s from %s:%s | Client Initiated Connection Closed.\n", user.getEmailAddress(), clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort());
                if(requestRevokeAuthUser(user.getEmailAddress())) {
                	System.out.printf("User %s Logged Out\n", user.getEmailAddress());
                } else {
                	System.out.printf("User Session %s not properly closed\n", user.getEmailAddress());
                }
                	
                System.out.println("Ending Thread:  "
                        + clientSocket.getInetAddress().getHostAddress() 
                        + ":" + clientSocket.getPort());
                
                objectInputStream.close();
                objectInputStream.close();
	            clientSocket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } 
        }
        
        /**
         * validateAuthUser is designed to connect and retrieve data the Authentication Server then disconnect
         * This class is not designed to have an enduring connection
         * 
         */
        public boolean validateAuthUser(String email, String authKey) throws UnknownHostException, IOException, ClassNotFoundException {
    		// establish a connection by providing host and port number
            Socket authServerSocket = new Socket("localhost", 40008);
            // get the input stream from the connected socket
            OutputStream authOutputStream = authServerSocket.getOutputStream();
            InputStream authInputStream = authServerSocket.getInputStream();
            // create a DataInputStream so we can read data from it.
            ObjectOutputStream authObjectOutputStream =  new ObjectOutputStream(authOutputStream);
            ObjectInputStream authObjectInputStream = new ObjectInputStream(authInputStream);
            
            UserWrapper userWrapper = new UserWrapper();
            userWrapper.setRequestType(8);
    		userWrapper.setEmail(email);
    		userWrapper.setAuthToken(authKey);
    		
    		authObjectOutputStream.writeObject(userWrapper);
    		
    		UserWrapper authServerResponse = (UserWrapper) authObjectInputStream.readObject();
    		
    		this.user = authServerResponse.getUser();
        	
        	return true;
        }
        
        public boolean requestRevokeAuthUser(String email) throws UnknownHostException, IOException, ClassNotFoundException {
    		// establish a connection by providing host and port number
            Socket authServerSocket = new Socket("localhost", 40008);
            // get the input stream from the connected socket
            OutputStream authOutputStream = authServerSocket.getOutputStream();
            InputStream authInputStream = authServerSocket.getInputStream();
            // create a DataInputStream so we can read data from it.
            ObjectOutputStream authObjectOutputStream =  new ObjectOutputStream(authOutputStream);
            ObjectInputStream authObjectInputStream = new ObjectInputStream(authInputStream);
            
            UserWrapper userWrapper = new UserWrapper();
            userWrapper.setRequestType(11);
            userWrapper.setEmail(email);
            
            authObjectOutputStream.writeObject(userWrapper);
    		
    		UserWrapper authServerResponse = (UserWrapper) authObjectInputStream.readObject();
    		
    		return authServerResponse.getResponse();
        }
        
        public Response create(CreateWrapper create) throws ServiceLoadException, FoodItemServiceException, MenuItemServiceException, DayMenuServiceException, UserServiceException, IOException {
        	
        	boolean successFlag = false;
        	
        	switch (create.getTypeId()) {
        		// FoodItem
        		case 1:
        			if (foodItemManager.addNewFoodItem((FoodItem) create.getObj())) { successFlag = true; }
        			break;
        		// MenuItem	
        		case 2:
        			if (menuItemManager.addNewMenuItem((MenuItem) create.getObj())) { successFlag = true; }
        			break;
        		// DayMenu	
        		case 3:
        			if (dayMenuManager.addNewDayMenu((DayMenu) create.getObj())) { successFlag = true; }
        			break;
        		// User	
        		case 4:
        			if (userManager.addNewUser((User) create.getObj())) { successFlag = true; }
        			break;
        	}
        	
        	if (successFlag) {
        		return new Response(true, String.format("Successfully Completed Create and Insert %s into the Database", create.getObj().getClass().getName()));
        	} else {
        		return new Response(false, String.format("Failed to Successfully Create and Insert %s into the Database", create.getObj().getClass().getName()));
        	}
        }
        
        public Response retrieve(RetrieveWrapper retrieve) throws ServiceLoadException, FoodItemServiceException, MenuItemServiceException, DayMenuServiceException, UserServiceException, IOException {
        	
        	boolean successFlag = false;
        	Response response = new Response();
        	
        	if (retrieve.getFlag()) {
        		switch (retrieve.getTypeId()) {
	        		// FoodItem
	        		case 1:
	        			response.setFoodItem(foodItemManager.retrieveFoodItem(retrieve.getSearchParam()));
	        			successFlag = true;
	        			break;
	        		// MenuItem	
	        		case 2:
	        			response.setMenuItem(menuItemManager.retrieveMenuItem(retrieve.getSearchParam()));
	        			successFlag = true;
	        			break;
	        		// DayMenu	
	        		case 3:
	        			Date date;
	        			SimpleDateFormat sdf1 = new SimpleDateFormat("MMM-d-yyyy");
	        			Calendar cal = Calendar.getInstance();
	        			
	        			try {
	        				date = sdf1.parse(retrieve.getSearchParam());
	        				cal.setTime(date);
	        			} catch (ParseException e2) {
	        				e2.printStackTrace();
	        			}
	        			
	        			response.setDayMenu(dayMenuManager.retrieveDayMenu(cal));
	        			successFlag = false;
	        			break;
	        		// User	
	        		case 4:
	        			response.setUser(userManager.retrieveUser(retrieve.getSearchParam()));
	        			successFlag = true;
	        			break;
        		}	
        	} else {
        		switch (retrieve.getTypeId()) {
	        		// FoodItem
	        		case 1:
	        			response.setFoodItems(foodItemManager.retrieveAllFoodItems());
	        			successFlag = true;
	        			break;
	        		// MenuItem	
	        		case 2:
	        			response.setMenuItems(menuItemManager.retrieveAllMenuItems());
	        			successFlag = true;
	        			break;
	        		// DayMenu	
	        		case 3:
	        			response.setDayMenus(dayMenuManager.retrieveAllDayMenus());
	        			successFlag = true;
	        			break;
	        		// User	
	        		case 4:
	        			response.setUsers(userManager.retrieveAllUsers());
	        			successFlag = true;
	        			break;
        		}
        	}
        	
        	if (successFlag) {
        		response.setFlag(true);
        		response.setMessage("Request - Successfully Retrieved");
        	} else {
        		response.setFlag(false);
        		response.setMessage("Request - Unsuccessful Retrieval");        		
        	}
        	
        	return response;
        }
   
	    public Response delete(DeleteWrapper delete) throws ServiceLoadException, FoodItemServiceException, MenuItemServiceException, DayMenuServiceException, UserServiceException, IOException, UserPrivilegesException {
	    	
	    	boolean successFlag = false;
	    	
	    	switch (delete.getTypeId()) {
	    		// FoodItem
	    		case 1:
	    			if (foodItemManager.deleteFoodItem((FoodItem) delete.getObj())) { successFlag = true; }
	    			break;
	    		// MenuItem	
	    		case 2:
	    			if (menuItemManager.deleteMenuItem((MenuItem) delete.getObj())) { successFlag = true; }
	    			break;
	    		// DayMenu	
	    		case 3:
	    			if (dayMenuManager.deleteDayMenu((DayMenu) delete.getObj())) { successFlag = true; }
	    			break;
	    		// User	
	    		case 4:
	    			if (userManager.deleteUser((User) delete.getObj())) { successFlag = true; }
	    			break;
	    	}
	    	
	    	if (successFlag) {
	    		return new Response(true, String.format("Successfully Completed Create and Insert %s into the Database", delete.getObj().getClass().getName()));
	    	} else {
	    		return new Response(false, String.format("Failed to Successfully Create and Insert %s into the Database", delete.getObj().getClass().getName()));
	    	}
	    }
    }
}
