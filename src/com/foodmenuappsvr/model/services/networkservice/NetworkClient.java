package com.foodmenuappsvr.model.services.networkservice;

/**
 * @author zach
 * Adapted from Prof. Ishmael, MSSE672, Regis University -- Example EPedigreeThreadedServers Application
 */

public class NetworkClient {
	
	private String networkPort;
	private String networkAddress;
	 
public void setNetworkPort(String networkPort){
	 
	 this.networkPort = networkPort;
	 	 
    } // end of setNetworkPort

public void setNetworkAddress(String networkAddress){
	 
	 this.networkAddress = networkAddress;
	 
    } // end of setNetworkIPAddress

public String getPort(String port) {
	 
	 port = networkPort;
	 	 
	 return port;
    } // end of getPort

public String getIP(String IP){
	
	 IP = networkAddress;
	 
	 return IP;
	 
    } // end of getIP
	
    
} // end of NetworkClient