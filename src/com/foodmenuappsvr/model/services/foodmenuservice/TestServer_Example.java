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
public class TestServer_Example {

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
                System.out.println("New client connected"
                                   + client.getInetAddress()
                                         .getHostAddress() + ":" + client.getPort());
  
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
                
                User user = (User) objectInputStream.readObject();
                
                userManager = new UserManager(user);
            	foodItemManager = new FoodItemManager(user);
            	menuItemManager = new MenuItemManager(user);
            	dayMenuManager = new DayMenuManager(user);
            	
//            	try {
//					objectOutputStream.writeObject(userManager.retrieveAllUsers());
//					objectOutputStream.writeObject(foodItemManager.retrieveAllFoodItems());
//					objectOutputStream.writeObject(menuItemManager.retrieveAllMenuItems());
//					objectOutputStream.writeObject(dayMenuManager.retrieveAllDayMenus());
//				} catch (ServiceLoadException | UserServiceException | 
//						FoodItemServiceException | MenuItemServiceException | 
//						DayMenuServiceException e1) {
//					e1.printStackTrace();
//				}
            	
                Object obj;
             	
                for (;;) {
                	try {
                		if ((obj = objectInputStream.readObject()) != null) {
                			Response response = new Response();
                			
                			if (obj.getClass().equals(createWrap.getClass())) {
                				System.out.println("Create Request recieved from Client");
                			} else if (obj.getClass().equals(retrieveWrap.getClass())) {
                				System.out.println("Retrieve Request recieved from Client");
                				response = retrieve((RetrieveWrapper) obj);
                			} else if (obj.getClass().equals(updateWrap.getClass())) {
                				System.out.println("Update Request recieved from Client");
                			} else if (obj.getClass().equals(deleteWrap.getClass())) {
                				System.out.println("Delete Request recieved from Client");
                			} else {
                				System.out.println("Unknown Request recieved from Client");
                			}
                			
                			System.out.println("Returning Response to Client");
                			objectOutputStream.writeObject(response);
	                	} else {
	                		Thread.sleep(50000);
	                	}
                	} catch (EOFException | InterruptedException | ServiceLoadException | FoodItemServiceException | MenuItemServiceException | DayMenuServiceException | UserServiceException e) {
                		break;
                	}
                }
                
                System.out.println("Closing Socket");
                
                objectInputStream.close();
                objectInputStream.close();
	            clientSocket.close();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } 
        }
        
        public Response create(CreateWrapper create) throws ServiceLoadException, FoodItemServiceException, MenuItemServiceException, DayMenuServiceException, UserServiceException, IOException {
        	
        	boolean successFlag = false;
        	
        	switch (create.getTypeId()) {
        		// FoodItem
        		case 1:
        			if (foodItemManager.addNewFoodItem((FoodItem) create.getObj())) { successFlag = true; }
        		// MenuItem	
        		case 2:
        			if (menuItemManager.addNewMenuItem((MenuItem) create.getObj())) { successFlag = true; }
        		// DayMenu	
        		case 3:
        			if (dayMenuManager.addNewDayMenu((DayMenu) create.getObj())) { successFlag = true; }
        		// User	
        		case 4:
        			if (userManager.addNewUser((User) create.getObj())) { successFlag = true; }
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
	        		// MenuItem	
	        		case 2:
	        			response.setMenuItem(menuItemManager.retrieveMenuItem(retrieve.getSearchParam()));
	        			successFlag = true;
	        		// DayMenu	
	        		case 3:
	        			java.sql.Date date;
	        			SimpleDateFormat sdf1 = new SimpleDateFormat("MMM-d-yyyy");
	        			Calendar cal = Calendar.getInstance();
	        			
	        			try {
	        				date = (java.sql.Date) sdf1.parse(retrieve.getSearchParam());
	        				cal.setTime(date);
	        			} catch (ParseException e2) {
	        				e2.printStackTrace();
	        			}
	        			
	        			response.setDayMenu(dayMenuManager.retrieveDayMenu(cal));
	        			successFlag = false;
	        		// User	
	        		case 4:
	        			response.setUser(userManager.retrieveUser(retrieve.getSearchParam()));
	        			successFlag = true;
        		}	
        	} else {
        		switch (retrieve.getTypeId()) {
	        		// FoodItem
	        		case 1:
	        			response.setFoodItems(foodItemManager.retrieveAllFoodItems());
	        			successFlag = true;
	        		// MenuItem	
	        		case 2:
	        			response.setMenuItems(menuItemManager.retrieveAllMenuItems());
	        			successFlag = true;
	        		// DayMenu	
	        		case 3:
	        			response.setDayMenus(dayMenuManager.retrieveAllDayMenus());
	        			successFlag = true;
	        		// User	
	        		case 4:
	        			response.setUsers(userManager.retrieveAllUsers());
	        			successFlag = true;
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
	    		// MenuItem	
	    		case 2:
	    			if (menuItemManager.deleteMenuItem((MenuItem) delete.getObj())) { successFlag = true; }
	    		// DayMenu	
	    		case 3:
	    			if (dayMenuManager.deleteDayMenu((DayMenu) delete.getObj())) { successFlag = true; }
	    		// User	
	    		case 4:
	    			if (userManager.deleteUser((User) delete.getObj())) { successFlag = true; }
	    	}
	    	
	    	if (successFlag) {
	    		return new Response(true, String.format("Successfully Completed Create and Insert %s into the Database", delete.getObj().getClass().getName()));
	    	} else {
	    		return new Response(false, String.format("Failed to Successfully Create and Insert %s into the Database", delete.getObj().getClass().getName()));
	    	}
	    }
    }
}
