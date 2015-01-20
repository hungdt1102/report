/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : Option.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Dec 23, 2014 5:31:21 PM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Dec 23, 2014       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.model;

/**
 * @author Hung
 *
 */
public class Option
{
	private long	id				= 0;
	private String	option_name		= "";
	private String	option_value	= "";
	private int		autoload		= 0;

	public long getId()
	{
		return id;
	}

	public void setId(long id)
	{
		this.id = id;
	}

	public String getOption_name()
	{
		return option_name;
	}

	public void setOption_name(String option_name)
	{
		this.option_name = option_name;
	}

	public String getOption_value()
	{
		return option_value;
	}

	public void setOption_value(String option_value)
	{
		this.option_value = option_value;
	}

	public int getAutoload()
	{
		return autoload;
	}

	public void setAutoload(int autoload)
	{
		this.autoload = autoload;
	}

}
