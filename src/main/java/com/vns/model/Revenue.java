/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : Revenue.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Dec 23, 2014 4:45:23 PM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Dec 23, 2014       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.model;

import java.util.List;

/**
 * @author Hung
 *
 */
public class Revenue
{
	private String	revenue;

	// format DD/MM
	private String	reportDate;

	private String	mocouter;
	private String	shortCode;
	private long	telcoId;
	private long	partnerId;

	public long getPartnerId()
	{
		return partnerId;
	}

	public void setPartnerId(long partnerId)
	{
		this.partnerId = partnerId;
	}

	public String getShortCode()
	{
		return shortCode;
	}

	public void setShortCode(String shortCode)
	{
		this.shortCode = shortCode;
	}

	public long getTelcoId()
	{
		return telcoId;
	}

	public void setTelcoId(long telcoId)
	{
		this.telcoId = telcoId;
	}

	public String getRevenue()
	{
		return revenue;
	}

	public void setRevenue(String revenue)
	{
		this.revenue = revenue;
	}

	public String getReportDate()
	{
		return reportDate;
	}

	public void setReportDate(String reportDate)
	{
		this.reportDate = reportDate;
	}

	public String getMocouter()
	{
		return mocouter;
	}

	public void setMocouter(String mocouter)
	{
		this.mocouter = mocouter;
	}

}
