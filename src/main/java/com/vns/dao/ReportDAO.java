/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : ReportDAO.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Dec 23, 2014 3:33:46 PM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Dec 23, 2014       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.dao;

import java.util.List;

import com.vns.hightchart.DataBean;
import com.vns.hightchart.PieChart;
import com.vns.model.Option;
import com.vns.model.Revenue;
import com.vns.model.Statistics;

/**
 * @author Hung
 *
 */
public interface ReportDAO
{
	public DataBean getRevenue(String fromDate, String toDate, long merchantId);

	public DataBean getRevenue(String fromDate, String toDate);

	public Statistics getStatisticDay(long merchantId);

	public Statistics getStatisticMonth(long merchantId);

	public Statistics getStatisticLastDay(long merchantId);

	public Statistics getStatisticLastMonth(long merchantId);

	public Statistics getStatisticDay();

	public Statistics getStatisticMonth();

	public Statistics getStatisticLastDay();

	public Statistics getStatisticLastMonth();

	public DataBean getStatisticShortCodeMonth();

	public DataBean getStatisticShortCodeMonth(long merchantId);

	public DataBean getRevenueDay();

	public DataBean getRevenueDay(long merchantId);

}
