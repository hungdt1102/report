/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : Users.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Dec 22, 2014 10:27:41 AM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Dec 22, 2014       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.model;

import java.util.Date;

/**
 * @author Hung
 *
 */
public class Users
{
	private long	userId			= 0;
	private String	user_login		= "";
	private String	user_pass		= "";
	private String	user_email		= "";
	private String	user_url		= "";
	private Date	createDate		= null;
	private String	display_name	= "";
	private int		user_status		= 0;

	public long getUserId()
	{
		return userId;
	}

	public void setUserId(long userId)
	{
		this.userId = userId;
	}

	public String getUser_login()
	{
		return user_login;
	}

	public void setUser_login(String user_login)
	{
		this.user_login = user_login;
	}

	public String getUser_pass()
	{
		return user_pass;
	}

	public void setUser_pass(String user_pass)
	{
		this.user_pass = user_pass;
	}

	public String getUser_email()
	{
		return user_email;
	}

	public void setUser_email(String user_email)
	{
		this.user_email = user_email;
	}

	public String getUser_url()
	{
		return user_url;
	}

	public void setUser_url(String user_url)
	{
		this.user_url = user_url;
	}

	public Date getCreateDate()
	{
		return createDate;
	}

	public void setCreateDate(Date createDate)
	{
		this.createDate = createDate;
	}

	public String getDisplay_name()
	{
		return display_name;
	}

	public void setDisplay_name(String display_name)
	{
		this.display_name = display_name;
	}

	public int getUser_status()
	{
		return user_status;
	}

	public void setUser_status(int user_status)
	{
		this.user_status = user_status;
	}

}
