package com.foodmenuappsvr.model.business.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.foodmenuappsvr.model.business.exceptions.ServiceLoadException;
import com.foodmenuappsvr.model.services.IService;
import com.foodmenuappsvr.model.services.implreferenceservice.ImplReferenceService;

/**
 * @author Zach Stanfill
 * Adapted from Prof. Ishmael, MSSE670, Regis University
 */
public class ServiceFactory {
	
	private ImplReferenceService implReference;

	public ServiceFactory() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		implReference = (ImplReferenceService)context.getBean("implReferences");
	}
	
	/**
	 * @param serviceName
	 * @return 
	 * @throws ServiceLoadException
	 */
	public IService getService(String serviceName) throws ServiceLoadException {
		try {
			Class<?> c = Class.forName(implReference.implReferenceLookup(serviceName));
			return (IService)c.newInstance();
		} catch (Exception e) {
			serviceName = serviceName + " not loaded";
			throw new ServiceLoadException(serviceName, e);
		}
	}
	
	/**
	 * UNUSED WITH THE IMPLEMENTATION OF SPRING APPLICATIONCONEXT
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