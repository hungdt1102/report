/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : UserMeta.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Dec 22, 2014 2:37:33 PM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Dec 22, 2014       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.model;

/**
 * @author Hung
 *
 */
public class UserMeta
{
	private long	metaId		= 0;
	private long	userId		= 0;
	private String	meta_key	= "";
	private String	meta_value	= "";

	public long getMetaId()
	{
		return metaId;
	}

	public void setMetaId(long metaId)
	{
		this.metaId = metaId;
	}

	public long getUserId()
	{
		return userId;
	}

	public void setUserId(long userId)
	{
		this.userId = userId;
	}

	public String getMeta_key()
	{
		return meta_key;
	}

	public void setMeta_key(String meta_key)
	{
		this.meta_key = meta_key;
	}

	public String getMeta_value()
	{
		return meta_value;
	}

	public void setMeta_value(String meta_value)
	{
		this.meta_value = meta_value;
	}

}
