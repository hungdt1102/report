/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : RevenueReq.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Dec 24, 2014 1:51:38 PM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Dec 24, 2014       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Hung
 *
 */
public class RevenueReq
{
	private String	fromDate;
	private String	toDate;
	private int	merchantId;

	@JsonCreator
	public RevenueReq(@JsonProperty("fromDate") String fromDate, @JsonProperty("toDate") String toDate, @JsonProperty("merchantId") int merchantId)
	{
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.merchantId = merchantId;
	}

	public String getFromDate()
	{
		return fromDate;
	}

	public void setFromDate(String fromDate)
	{
		this.fromDate = fromDate;
	}

	public String getToDate()
	{
		return toDate;
	}

	public void setToDate(String toDate)
	{
		this.toDate = toDate;
	}

	public int getMerchantId()
	{
		return merchantId;
	}

	public void setMerchantId(int merchantId)
	{
		this.merchantId = merchantId;
	}

}
