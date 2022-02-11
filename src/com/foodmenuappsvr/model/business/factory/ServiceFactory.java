package com.foodmenuappsvr.model.business.factory;

import com.foodmenuappsvr.model.business.exceptions.ServiceLoadException;
import com.foodmenuappsvr.model.services.IService;

/**
 * @author Zach Stanfill
 * Adapted from Prof. Ishmael, MSSE670, Regis University
 */
public class ServiceFactory {

	public ServiceFactory() {
	}
	
	/**
	 * @param serviceName
	 * @return 
	 * @throws ServiceLoadException
	 */
	public IService getService(String serviceName) throws ServiceLoadException {
		try {
			Class<?> c = Class.forName(getImplName(serviceName));
			return (IService)c.newInstance();
		} catch (Exception e) {
			serviceName = serviceName + " not loaded";
			throw new ServiceLoadException(serviceName, e);
		}
	}
	
	/**
	 * @param serviceName
	 * @return IService Assoicated Impl Path
	 * @throws Exception
	 */
	private String getImplName (String serviceName) throws Exception
	{
		
	    java.util.Properties props = new java.util.Properties();

	    java.io.FileInputStream source = new 
		    java.io.FileInputStream("config/application.properties");
	    props.load(source);
	    source.close();
	    return props.getProperty(serviceName);
	}

}