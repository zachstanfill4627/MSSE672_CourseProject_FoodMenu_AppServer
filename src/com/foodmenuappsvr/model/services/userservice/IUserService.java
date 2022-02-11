package com.foodmenuappsvr.model.services.userservice;

import java.util.ArrayList;

import com.foodmenu.model.domain.User;
import com.foodmenuappsvr.model.services.IService;
import com.foodmenuappsvr.model.services.exceptions.UserServiceException;

public interface IUserService extends IService {

	public final String NAME = "IUserService";
	
	public boolean createUserData(User user, String salt) throws UserServiceException;
	public User retrieveUserData(String email) throws UserServiceException;
	public String retrieveUserSaltData(String email) throws UserServiceException;
	public ArrayList<User> retrieveAllUserData() throws UserServiceException;
	public boolean updateUserData(User user) throws UserServiceException;
	public boolean updateUserPasswordData(User user) throws UserServiceException;
	public boolean resetUserPasswordData(String email, int age, String recPhrase) throws UserServiceException;
	public boolean deleteUserData(User user) throws UserServiceException;
	public boolean authenticateUserData(String email, String password) throws UserServiceException;
}
