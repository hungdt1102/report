/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : UserDAO.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Dec 22, 2014 10:27:29 AM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Dec 22, 2014       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.dao;

import java.util.List;

import com.vns.model.UserMeta;
import com.vns.model.Users;

/**
 * @author Hung
 *
 */
public interface UserDAO
{
	public Users checkLogin(String username, String password);

	public UserMeta getUserMeta(String meta_key, long userId);

	public List<UserMeta> getAllUserMeta(long userId);

	public void inserUser(Users user);

	public void updateUser(Users user);

	public void deleteUser(long id);

	public List<Users> getAllUser();
}
