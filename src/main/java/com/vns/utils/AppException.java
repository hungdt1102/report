/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : AppException.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : MOReceiver
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Dec 3, 2014 3:48:57 PM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Dec 3, 2014       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.utils;

/**
 * @author Hung
 *
 */
public class AppException extends Exception
{
	private String	mstrReason	= null;
	private String	mstrContext	= null;
	private String	mstrInfo	= null;

	public AppException(String strReason)
	{
		setReason(strReason);
	}

	public AppException(String strReason, String strContext)
	{
		setContext(strContext);
		setReason(strReason);
	}

	public AppException(String strReason, String strContext, String strInfo)
	{
		setContext(strContext);
		setInfo(strInfo);
		setReason(strReason);
	}

	public AppException(Exception e, String strContext)
	{
		super(e);
		setContext(strContext);
		if ((e instanceof AppException))
		{
			setReason(((AppException) e).getReason());
			setInfo(((AppException) e).getInfo());
		}
		else
		{
			setReason(e.getMessage());
		}
	}

	public AppException(Exception e, String strContext, String strInfo)
	{
		super(e);
		setContext(strContext);
		setInfo(strInfo);
		if ((e instanceof AppException))
			setReason(((AppException) e).getReason());
		else
			setReason(e.getMessage());
	}

	public String getLocalizedMessage()
	{
		return this.mstrReason;
	}

	public String getMessage()
	{
		String strMessage = this.mstrReason;
		if ((this.mstrContext != null) && (this.mstrContext.length() > 0))
			strMessage = strMessage + "\r\nContext: " + this.mstrContext;
		if ((this.mstrInfo != null) && (this.mstrInfo.length() > 0))
			strMessage = strMessage + "\r\nAdditional info: " + this.mstrInfo;
		return strMessage;
	}

	public String toString()
	{
		return getMessage();
	}

	public String getContext()
	{
		return this.mstrContext;
	}

	public void setContext(String mstrContext)
	{
		this.mstrContext = mstrContext;
	}

	public String getInfo()
	{
		return this.mstrInfo;
	}

	public void setInfo(String mstrInfo)
	{
		this.mstrInfo = mstrInfo;
	}

	public String getReason()
	{
		return this.mstrReason;
	}

	public void setReason(String strReason)
	{
		if (strReason == null)
			this.mstrReason = "Null pointer exception";
		else
			this.mstrReason = strReason.trim();
	}
}
