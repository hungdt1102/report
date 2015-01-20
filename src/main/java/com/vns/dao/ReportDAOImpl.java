/**
 * -----------------------------------------------------------------------------
 *
 * @ Copyright(c) 2012 VNS Telecom. JSC. All Rights Reserved.
 * -----------------------------------------------------------------------------
 * FILE NAME : ReportDAOImpl.java DESCRIPTION : PRINCIPAL AUTHOR : Do Tien Hung
 * SYSTEM NAME : report MODULE NAME : LANGUAGE : Java DATE OF FIRST RELEASE :
 * -----------------------------------------------------------------------------
 * @ Datetime Dec 23, 2014 5:24:12 PM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	Author	Version Description
 * -----------------------------------------------------------------------------------
 * Dec 23, 2014 hungdt 1.0 Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.dao;

import com.vns.hightchart.DataBean;
import com.vns.hightchart.PieChart;
import com.vns.hightchart.SeriesBean;
import com.vns.memcached.IMemcacheClient;
import com.vns.model.Revenue;
import com.vns.model.ShareRevenue;
import com.vns.model.Statistics;
import com.vns.sql.Database;
import com.vns.utils.CacheInit;
import com.vns.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.introspect.BasicClassIntrospector.GetterMethodFilter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * @author Hung
 *
 */

public class ReportDAOImpl extends CacheInit implements ReportDAO
{

	public static Logger		log			= Logger.getLogger(ReportDAOImpl.class);

	private static final String	NAME_SPACE	= "report";
	private static final int	TIME_CACHE	= 300;

	public DataBean getRevenue(String fromDate, String toDate, long merchantId)
	{

		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				DataBean bean = null;
				try
				{
					String key = NAME_SPACE + "getRevenue" + fromDate + toDate
							+ merchantId;
					bean = (DataBean) client.get(key);
					if (bean == null)
					{
						bean = getRevenueDB(fromDate, toDate, merchantId);
						client.set(key, TIME_CACHE, bean);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return bean;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getRevenueDB(fromDate, toDate, merchantId);
	}

	public DataBean getRevenueDB(String fromDate, String toDate, long merchantId)
	{
		log.info("Get Revenue from DB - MerchantId = " + merchantId);
		List<Date> dates = new ArrayList<Date>();
		DateFormat formatter;

		formatter = new SimpleDateFormat("dd/MM/yyyy");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		List<SeriesBean> list = null;

		String[] categories = null;

		try
		{
			sql = "select to_char(s.orderDate,'DD/MM') reqDate, serviceaddr, telcoId, count(*) MO from subscriberOrder@smsgw s "
					+ "where s.merchantID = ? "
					+ "and s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.cause = 'success' "
					+ "group by to_char(s.orderDate, 'DD/MM'), serviceaddr, telcoId "
					+ "order by max(orderDate) asc";

			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);

			stmt.setLong(1, merchantId);
			stmt.setString(2, fromDate + " 00:00:00");
			stmt.setString(3, toDate + " 23:59:59");

			rs = stmt.executeQuery();

			Date startDate = (Date) formatter.parse(fromDate);
			Date endDate = (Date) formatter.parse(toDate);
			long interval = 24 * 1000 * 60 * 60; // 1 day
			long endTime = endDate.getTime(); // create your endtime here,
												// possibly using Calendar or
												// Date
			long curTime = startDate.getTime();
			long sysdate = Calendar.getInstance().getTime().getTime();

			if (endTime >= sysdate)
			{
				endTime = sysdate;
			}
			while (curTime <= endTime)
			{
				dates.add(new Date(curTime));
				curTime += interval;
			}

			List<String> price = new ArrayList<String>();
			List<String> previousRevenue = getPreviousRevenue(fromDate, toDate, merchantId);
			List<String> mo = new ArrayList<String>();
			List<String> category = new ArrayList<String>();

			DateFormat formatter1 = new SimpleDateFormat("dd/MM");
			for (Date date : dates)
			{
				Date lDate = (Date) date;
				String ds = formatter1.format(lDate);
				category.add(ds);
				price.add("0");
				mo.add("0");
			}

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setReportDate(rs.getString("reqDate"));
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				re.add(r);
			}

			for (int i = 0; i < category.size(); i++)
			{
				long sum_price = 0;
				int sum_mo = 0;
				for (Revenue reve : re)
				{
					if (category.get(i).equals(reve.getReportDate()))
					{
						for (ShareRevenue s : share)
						{
							if (s.getMerchantId() == merchantId
									&& s.getShortCode().equalsIgnoreCase(
											reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId())
							{
								sum_price += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}
						}
						sum_mo += Integer.parseInt(reve.getMocouter());
					}
				}

				price.set(i, String.valueOf(sum_price));
				mo.set(i, String.valueOf(sum_mo));
			}

			list = new ArrayList<SeriesBean>();
			list.add(new SeriesBean("MO", "", mo.toArray(new String[mo.size()]), "spline"));
			list.add(new SeriesBean("DT", "", price.toArray(new String[price.size()]), "column"));
			list.add(new SeriesBean("DTCK", "", previousRevenue.toArray(new String[previousRevenue.size()]), "column"));

			categories = category.toArray(new String[category.size()]);

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return new DataBean("container", "THỐNG KÊ DOANH THU", "MO", "",
				Arrays.asList(categories), list);
	}

	public DataBean getRevenue(String fromDate, String toDate)
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				DataBean bean = null;
				try
				{
					String key = NAME_SPACE + "getRevenue" + fromDate + toDate;
					bean = (DataBean) client.get(key);
					if (bean == null)
					{
						bean = getRevenueDB(fromDate, toDate);
						client.set(key, TIME_CACHE, bean);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return bean;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getRevenueDB(fromDate, toDate);
	}

	public DataBean getRevenueDB(String fromDate, String toDate)
	{

		log.info("####### Get Revenue from DB for Manager #########");
		List<Date> dates = new ArrayList<Date>();
		DateFormat formatter;

		formatter = new SimpleDateFormat("dd/MM/yyyy");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		List<SeriesBean> list = null;

		String[] categories = null;

		try
		{
			sql = "select to_char(s.orderDate,'DD/MM') reqDate, serviceaddr, telcoId, count(*) MO, partnerId from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.cause = 'success' "
					+ "group by to_char(s.orderDate, 'DD/MM'), serviceaddr, telcoId, partnerId "
					+ "order by max(orderDate) asc";

			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);

			stmt.setString(1, fromDate + " 00:00:00");
			stmt.setString(2, toDate + " 23:59:59");

			rs = stmt.executeQuery();

			Date startDate = (Date) formatter.parse(fromDate);
			Date endDate = (Date) formatter.parse(toDate);
			long interval = 24 * 1000 * 60 * 60; // 1 day
			long endTime = endDate.getTime(); // create your endtime here,
												// possibly using Calendar or
												// Date
			long sysdate = Calendar.getInstance().getTime().getTime();

			if (endTime >= sysdate)
			{
				endTime = sysdate;
			}
			long curTime = startDate.getTime();
			while (curTime <= endTime)
			{
				dates.add(new Date(curTime));
				curTime += interval;
			}

			List<String> price = new ArrayList<String>();
			List<String> previousRevenue = getPreviousRevenue(fromDate, toDate);
			List<String> mo = new ArrayList<String>();
			List<String> category = new ArrayList<String>();

			DateFormat formatter1 = new SimpleDateFormat("dd/MM");
			for (Date date : dates)
			{
				Date lDate = (Date) date;
				String ds = formatter1.format(lDate);
				System.out.println(ds);
				category.add(ds);
				price.add("0");
				mo.add("0");
			}

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setReportDate(rs.getString("reqDate"));
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				r.setPartnerId(rs.getLong("partnerId"));
				re.add(r);
			}

			for (int i = 0; i < category.size(); i++)
			{
				long sum_price = 0;
				int sum_mo = 0;
				long sum_price_vdc = 0;
				long sum_price_neo = 0;
				long sum_price_vt6x64 = 0;
				for (Revenue reve : re)
				{
					if (category.get(i).equals(reve.getReportDate()))
					{
						for (ShareRevenue s : share)
						{
							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3001)
							{
								sum_price_neo += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}

							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3002)
							{
								sum_price_vdc += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}

							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3003)
							{
								sum_price_vt6x64 += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}
						}
						sum_mo += Integer.parseInt(reve.getMocouter());
					}
				}

				if (sum_price_neo <= 200000000)
				{
					sum_price_neo = sum_price_neo * 90 / 100;
				}
				else
				{
					sum_price_neo = sum_price_neo * 92 / 100;
				}

				if (sum_price_vdc <= 100000000)
				{
					sum_price_vdc = sum_price_vdc * 90 / 100;
				}
				else
				{
					sum_price_vdc = sum_price_vdc * 92 / 100;
				}

				sum_price = sum_price_neo + sum_price_vdc + sum_price_vt6x64;

				price.set(i, String.valueOf(sum_price));
				mo.set(i, String.valueOf(sum_mo));
			}

			list = new ArrayList<SeriesBean>();
			list.add(new SeriesBean("MO", "", mo.toArray(new String[mo.size()]), "spline"));
			list.add(new SeriesBean("DT", "", price.toArray(new String[price.size()]), "column"));
			list.add(new SeriesBean("DTCK", "", previousRevenue.toArray(new String[previousRevenue.size()]), "column"));

			categories = category.toArray(new String[category.size()]);

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return new DataBean("container", "THỐNG KÊ DOANH THU", "MO", "",
				Arrays.asList(categories), list);
	}

	@SuppressWarnings("unchecked")
	public List<String> getPreviousRevenue(String fromDate, String toDate)
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				List<String> bean = null;
				try
				{
					String key = NAME_SPACE + "getPreviousRevenue" + fromDate + toDate;
					bean = (List<String>) client.get(key);
					if (bean == null)
					{
						bean = getPreviousRevenueDB(fromDate, toDate);
						client.set(key, TIME_CACHE, bean);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return bean;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getPreviousRevenueDB(fromDate, toDate);
	}

	public List<String> getPreviousRevenueDB(String fromDate, String toDate)
	{
		log.info("####### Get getPreviousRevenueDB from DB for Manager #########");
		List<String> price = new ArrayList<String>();
		List<String> mo = new ArrayList<String>();
		List<String> category = new ArrayList<String>();

		List<Date> dates = new ArrayList<Date>();
		DateFormat formatter;

		formatter = new SimpleDateFormat("dd/MM/yyyy");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		try
		{
			sql = "select to_char(s.orderDate,'DD/MM') reqDate, serviceaddr, telcoId, count(*) MO, partnerId from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.cause = 'success' "
					+ "group by to_char(s.orderDate, 'DD/MM'), serviceaddr, telcoId, partnerId "
					+ "order by max(orderDate) asc";

			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);

			stmt.setString(1, fromDate + " 00:00:00");
			stmt.setString(2, toDate + " 23:59:59");

			rs = stmt.executeQuery();

			Date startDate = (Date) formatter.parse(fromDate);
			Date endDate = (Date) formatter.parse(toDate);

			// subtract 1 month
			Calendar tempDate = Calendar.getInstance();
			tempDate.setTime(startDate);
			tempDate.add(Calendar.MONTH, -1);
			startDate = tempDate.getTime();

			tempDate.setTime(endDate);
			tempDate.add(Calendar.MONTH, -1);
			endDate = tempDate.getTime();

			long interval = 24 * 1000 * 60 * 60; // 1 day
			long endTime = endDate.getTime(); // create your endtime here,
												// possibly using Calendar or
												// Date
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MONTH, -1);
			long sysdate = now.getTime().getTime();

			if (endTime >= sysdate)
			{
				endTime = sysdate;
			}
			long curTime = startDate.getTime();
			while (curTime <= endTime)
			{
				dates.add(new Date(curTime));
				curTime += interval;
			}

			DateFormat formatter1 = new SimpleDateFormat("dd/MM");
			for (Date date : dates)
			{
				Date lDate = (Date) date;
				String ds = formatter1.format(lDate);
				System.out.println(ds);
				category.add(ds);
				price.add("0");
				mo.add("0");
			}

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setReportDate(rs.getString("reqDate"));
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				r.setPartnerId(rs.getLong("partnerId"));
				re.add(r);
			}

			for (int i = 0; i < category.size(); i++)
			{
				long sum_price = 0;
				int sum_mo = 0;
				long sum_price_vdc = 0;
				long sum_price_neo = 0;
				long sum_price_vt6x64 = 0;
				for (Revenue reve : re)
				{
					if (category.get(i).equals(reve.getReportDate()))
					{
						for (ShareRevenue s : share)
						{
							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3001)
							{
								sum_price_neo += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}

							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3002)
							{
								sum_price_vdc += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}

							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3003)
							{
								sum_price_vt6x64 += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}
						}
						sum_mo += Integer.parseInt(reve.getMocouter());
					}
				}

				if (sum_price_neo <= 200000000)
				{
					sum_price_neo = sum_price_neo * 90 / 100;
				}
				else
				{
					sum_price_neo = sum_price_neo * 92 / 100;
				}

				if (sum_price_vdc <= 100000000)
				{
					sum_price_vdc = sum_price_vdc * 90 / 100;
				}
				else
				{
					sum_price_vdc = sum_price_vdc * 92 / 100;
				}

				sum_price = sum_price_neo + sum_price_vdc + sum_price_vt6x64;

				price.set(i, String.valueOf(sum_price));
				mo.set(i, String.valueOf(sum_mo));
			}

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return price;
	}

	@SuppressWarnings("unchecked")
	public List<String> getPreviousRevenue(String fromDate, String toDate, long merchantId)
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				List<String> bean = null;
				try
				{
					String key = NAME_SPACE + "getPreviousRevenue" + fromDate + toDate + merchantId;
					bean = (List<String>) client.get(key);
					if (bean == null)
					{
						bean = getPreviousRevenueDB(fromDate, toDate, merchantId);
						client.set(key, TIME_CACHE, bean);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return bean;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getPreviousRevenueDB(fromDate, toDate, merchantId);
	}

	public List<String> getPreviousRevenueDB(String fromDate, String toDate, long merchantId)
	{
		log.info("####### Get getPreviousRevenueDB from DB for Manager #########");
		List<String> price = new ArrayList<String>();
		List<String> mo = new ArrayList<String>();
		List<String> category = new ArrayList<String>();

		List<Date> dates = new ArrayList<Date>();
		DateFormat formatter;

		formatter = new SimpleDateFormat("dd/MM/yyyy");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		try
		{
			sql = "select to_char(s.orderDate,'DD/MM') reqDate, serviceaddr, telcoId, count(*) MO, partnerId from subscriberOrder@smsgw s "
					+ "where s.merchantId = ?"
					+ "and s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.cause = 'success' "
					+ "group by to_char(s.orderDate, 'DD/MM'), serviceaddr, telcoId, partnerId "
					+ "order by max(orderDate) asc";

			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);

			stmt.setLong(1, merchantId);
			stmt.setString(2, fromDate + " 00:00:00");
			stmt.setString(3, toDate + " 23:59:59");

			rs = stmt.executeQuery();

			Date startDate = (Date) formatter.parse(fromDate);
			Date endDate = (Date) formatter.parse(toDate);

			// subtract 1 month
			Calendar tempDate = Calendar.getInstance();
			tempDate.setTime(startDate);
			tempDate.add(Calendar.MONTH, -1);
			startDate = tempDate.getTime();

			tempDate.setTime(endDate);
			tempDate.add(Calendar.MONTH, -1);
			endDate = tempDate.getTime();

			long interval = 24 * 1000 * 60 * 60; // 1 day
			long endTime = endDate.getTime(); // create your endtime here,
												// possibly using Calendar or
												// Date
			Calendar now = Calendar.getInstance();
			now.add(Calendar.MONTH, -1);
			long sysdate = now.getTime().getTime();

			if (endTime >= sysdate)
			{
				endTime = sysdate;
			}
			long curTime = startDate.getTime();
			while (curTime <= endTime)
			{
				dates.add(new Date(curTime));
				curTime += interval;
			}

			DateFormat formatter1 = new SimpleDateFormat("dd/MM");
			for (Date date : dates)
			{
				Date lDate = (Date) date;
				String ds = formatter1.format(lDate);
				System.out.println(ds);
				category.add(ds);
				price.add("0");
				mo.add("0");
			}

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setReportDate(rs.getString("reqDate"));
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				r.setPartnerId(rs.getLong("partnerId"));
				re.add(r);
			}

			for (int i = 0; i < category.size(); i++)
			{
				long sum_price = 0;
				int sum_mo = 0;
				for (Revenue reve : re)
				{
					if (category.get(i).equals(reve.getReportDate()))
					{
						for (ShareRevenue s : share)
						{
							if (s.getMerchantId() == merchantId
									&& s.getShortCode().equalsIgnoreCase(
											reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId())
							{
								sum_price += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}
						}
						sum_mo += Integer.parseInt(reve.getMocouter());
					}
				}

				price.set(i, String.valueOf(sum_price));
				mo.set(i, String.valueOf(sum_mo));
			}

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return price;
	}

	public Statistics getStatisticDay(long merchantId)
	{

		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				Statistics statistic = null;
				try
				{
					String key = NAME_SPACE + "getStatisticDay" + merchantId;
					statistic = (Statistics) client.get(key);
					if (statistic == null)
					{
						statistic = getStatisticDayDB(merchantId);
						client.set(key, TIME_CACHE, statistic);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return statistic;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getStatisticDayDB(merchantId);

	}

	public Statistics getStatisticDayDB(long merchantId)
	{
		log.info("####### Get getStatisticDay from DB for cp: " + merchantId
				+ " #########");
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		Statistics statistic = new Statistics();
		try
		{
			sql = "select serviceaddr, telcoId, count(*) MO from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS')  "
					+ "and s.merchantId = ?  "
					+ "and s.cause = 'success' "
					+ "group by  serviceaddr, telcoId ";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, sdf.format(Calendar.getInstance().getTime())
					+ " 00:00:00");
			stmt.setString(2, sdf.format(Calendar.getInstance().getTime())
					+ " 23:59:59");
			stmt.setLong(3, merchantId);
			rs = stmt.executeQuery();

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				re.add(r);
			}

			int sum_price = 0;
			int sum_mo = 0;
			for (Revenue reve : re)
			{
				for (ShareRevenue s : share)
				{
					if (s.getMerchantId() == merchantId
							&& s.getShortCode().equalsIgnoreCase(
									reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId())
					{
						sum_price += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));
					}
				}
				sum_mo += Integer.parseInt(reve.getMocouter());

			}

			statistic.setRevenue(String.valueOf(sum_price));
			statistic.setMo(String.valueOf(sum_mo));

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return statistic;
	}

	public Statistics getStatisticMonth(long merchantId)
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				Statistics statistic = null;
				try
				{
					String key = NAME_SPACE + "getStatisticMonth" + merchantId;
					statistic = (Statistics) client.get(key);
					if (statistic == null)
					{
						statistic = getStatisticMonthDB(merchantId);
						client.set(key, TIME_CACHE, statistic);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return statistic;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getStatisticMonthDB(merchantId);
	}

	public Statistics getStatisticMonthDB(long merchantId)
	{
		log.info("####### Get getStatisticMonth from DB for cp: " + merchantId
				+ " #########");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		Statistics statistic = new Statistics();
		try
		{
			sql = "select serviceaddr, telcoId, count(*) MO from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS')  "
					+ "and s.merchantId = ?  "
					+ "and s.cause = 'success' "
					+ "group by  serviceaddr, telcoId ";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, DateUtils.getFirstDayOfTheMonth() + " 00:00:00");
			stmt.setString(2, DateUtils.getLastDayOfTheMonth() + " 23:59:59");
			stmt.setLong(3, merchantId);
			rs = stmt.executeQuery();

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				re.add(r);
			}

			int sum_price = 0;
			int sum_mo = 0;
			for (Revenue reve : re)
			{
				for (ShareRevenue s : share)
				{
					if (s.getMerchantId() == merchantId
							&& s.getShortCode().equalsIgnoreCase(
									reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId())
					{
						sum_price += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));
					}
				}
				sum_mo += Integer.parseInt(reve.getMocouter());

			}

			statistic.setRevenue(String.valueOf(sum_price));
			statistic.setMo(String.valueOf(sum_mo));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return statistic;
	}

	public Statistics getStatisticDay()
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				Statistics statistic = null;
				try
				{
					String key = NAME_SPACE + "getStatisticDay";
					statistic = (Statistics) client.get(key);
					if (statistic == null)
					{
						statistic = getStatisticDayDB();
						client.set(key, TIME_CACHE, statistic);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return statistic;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getStatisticDayDB();
	}

	public Statistics getStatisticDayDB()
	{
		log.info("####### Get getStatisticDay from DB for Manager #########");
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		Statistics statistic = new Statistics();
		try
		{
			sql = "select serviceaddr, telcoId, count(*) MO, partnerId from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS')  "
					+ "and s.cause = 'success' "
					+ "group by  serviceaddr, telcoId, partnerId ";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, sdf.format(Calendar.getInstance().getTime())
					+ " 00:00:00");
			stmt.setString(2, sdf.format(Calendar.getInstance().getTime())
					+ " 23:59:59");
			rs = stmt.executeQuery();

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				r.setPartnerId(rs.getLong("partnerId"));
				re.add(r);
			}

			long sum_price = 0;
			int sum_mo = 0;
			long sum_price_vdc = 0;
			long sum_price_neo = 0;
			long sum_price_vt6x64 = 0;

			for (Revenue reve : re)
			{
				for (ShareRevenue s : share)
				{
					if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3001)
					{
						sum_price_neo += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));

					}

					if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3002)
					{
						sum_price_vdc += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));

					}

					if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3003)
					{
						sum_price_vt6x64 += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));

					}
				}

			}

			if (sum_price_neo <= 200000000)
			{
				sum_price_neo = sum_price_neo * 90 / 100;
			}
			else
			{
				sum_price_neo = sum_price_neo * 92 / 100;
			}

			if (sum_price_vdc <= 100000000)
			{
				sum_price_vdc = sum_price_vdc * 90 / 100;
			}
			else
			{
				sum_price_vdc = sum_price_vdc * 92 / 100;
			}

			sum_price = sum_price_neo + sum_price_vdc + sum_price_vt6x64;

			statistic.setRevenue(String.valueOf(sum_price));
			statistic.setMo(String.valueOf(sum_mo));

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return statistic;
	}

	public Statistics getStatisticMonth()
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				Statistics statistic = null;
				try
				{
					String key = NAME_SPACE + "getStatisticMonth";
					statistic = (Statistics) client.get(key);
					if (statistic == null)
					{
						statistic = getStatisticMonthDB();
						client.set(key, TIME_CACHE, statistic);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return statistic;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getStatisticMonthDB();
	}

	public Statistics getStatisticMonthDB()
	{
		log.info("####### Get getStatisticMonth from DB for Manager #########");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		Statistics statistic = new Statistics();
		try
		{
			sql = "select serviceaddr, telcoId, count(*) MO, partnerId from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS')  "
					+ "and s.cause = 'success' "
					+ "group by  serviceaddr, telcoId, partnerId ";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, DateUtils.getFirstDayOfTheMonth() + " 00:00:00");
			stmt.setString(2, DateUtils.getLastDayOfTheMonth() + " 23:59:59");
			rs = stmt.executeQuery();

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				r.setPartnerId(rs.getLong("partnerId"));
				re.add(r);
			}

			long sum_price = 0;
			int sum_mo = 0;
			long sum_price_vdc = 0;
			long sum_price_neo = 0;
			long sum_price_vt6x64 = 0;
			for (Revenue reve : re)
			{
				for (ShareRevenue s : share)
				{
					if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3001)
					{
						sum_price_neo += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));

					}

					if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3002)
					{
						sum_price_vdc += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));

					}

					if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3003)
					{
						sum_price_vt6x64 += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));

					}
				}

			}
			if (sum_price_neo <= 200000000)
			{
				sum_price_neo = sum_price_neo * 90 / 100;
			}
			else
			{
				sum_price_neo = sum_price_neo * 92 / 100;
			}

			if (sum_price_vdc <= 100000000)
			{
				sum_price_vdc = sum_price_vdc * 90 / 100;
			}
			else
			{
				sum_price_vdc = sum_price_vdc * 92 / 100;
			}

			sum_price = sum_price_neo + sum_price_vdc + sum_price_vt6x64;

			statistic.setRevenue(String.valueOf(sum_price));
			statistic.setMo(String.valueOf(sum_mo));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return statistic;
	}

	public Statistics getStatisticLastDay(long merchantId)
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				Statistics statistic = null;
				try
				{
					String key = NAME_SPACE + "getStatisticLastDay"
							+ merchantId;
					statistic = (Statistics) client.get(key);
					if (statistic == null)
					{
						statistic = getStatisticLastDayDB(merchantId);
						client.set(key, TIME_CACHE, statistic);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return statistic;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getStatisticLastDayDB(merchantId);
	}

	public Statistics getStatisticLastDayDB(long merchantId)
	{
		log.info("####### Get getStatisticLastDayDB from DB for cp: "
				+ merchantId + " #########");
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		Statistics statistic = new Statistics();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		try
		{
			sql = "select serviceaddr, telcoId, count(*) MO from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS')  "
					+ "and s.merchantId = ?  "
					+ "and s.cause = 'success' "
					+ "group by  serviceaddr, telcoId ";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, sdf.format(calendar.getTime()) + " 00:00:00");
			stmt.setString(2, sdf.format(calendar.getTime()) + " 23:59:59");
			stmt.setLong(3, merchantId);
			rs = stmt.executeQuery();

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				re.add(r);
			}

			long sum_price = 0;
			int sum_mo = 0;
			for (Revenue reve : re)
			{
				for (ShareRevenue s : share)
				{
					if (s.getMerchantId() == merchantId
							&& s.getShortCode().equalsIgnoreCase(
									reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId())
					{
						sum_price += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));
					}
				}
				sum_mo += Integer.parseInt(reve.getMocouter());

			}

			statistic.setRevenue(String.valueOf(sum_price));
			statistic.setMo(String.valueOf(sum_mo));

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return statistic;
	}

	public Statistics getStatisticLastMonth(long merchantId)
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				Statistics statistic = null;
				try
				{
					String key = NAME_SPACE + "getStatisticLastMonth"
							+ merchantId;
					statistic = (Statistics) client.get(key);
					if (statistic == null)
					{
						statistic = getStatisticLastMonthDB(merchantId);
						client.set(key, TIME_CACHE, statistic);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return statistic;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getStatisticLastMonthDB(merchantId);
	}

	public Statistics getStatisticLastMonthDB(long merchantId)
	{
		log.info("####### Get getStatisticLastMonth from DB for cp: "
				+ merchantId + " #########");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		Statistics statistic = new Statistics();
		try
		{
			sql = "select s.serviceaddr, s.telcoId, count(*) MO from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS')  "
					+ "and s.merchantId = ?  "
					+ "and s.cause = 'success' "
					+ "group by  s.serviceaddr, s.telcoId ";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, DateUtils.getFirstDayOfTheLastMonth()
					+ "00:00:00");
			stmt.setString(2, DateUtils.getLastDayOfTheLastMonth() + "23:59:59");
			stmt.setLong(3, merchantId);
			rs = stmt.executeQuery();

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				re.add(r);
			}

			long sum_price = 0;
			int sum_mo = 0;
			for (Revenue reve : re)
			{
				System.out.println(reve.getShortCode() + "--"
						+ reve.getMocouter());
				for (ShareRevenue s : share)
				{
					if (s.getMerchantId() == merchantId
							&& s.getShortCode().equalsIgnoreCase(
									reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId())
					{
						sum_price += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));
						System.out.println(s.getPrice() + " -- "
								+ reve.getMocouter());
					}
				}
				sum_mo += Integer.parseInt(reve.getMocouter());

			}

			statistic.setRevenue(String.valueOf(sum_price));
			statistic.setMo(String.valueOf(sum_mo));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return statistic;
	}

	public Statistics getStatisticLastDay()
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				Statistics statistic = null;
				try
				{
					String key = NAME_SPACE + "getStatisticLastDay";
					statistic = (Statistics) client.get(key);
					if (statistic == null)
					{
						statistic = getStatisticLastDayDB();
						client.set(key, TIME_CACHE, statistic);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return statistic;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getStatisticLastDayDB();
	}

	public Statistics getStatisticLastDayDB()
	{
		log.info("####### Get getStatisticLastDayDB from DB for Manager #########");
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		Statistics statistic = new Statistics();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		try
		{
			sql = "select serviceaddr, telcoId, count(*) MO,partnerId from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS')  "
					+ "and s.cause = 'success' "
					+ "group by  serviceaddr, telcoId, partnerId ";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, sdf.format(calendar.getTime()) + " 00:00:00");
			stmt.setString(2, sdf.format(calendar.getTime()) + " 23:59:59");
			rs = stmt.executeQuery();

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				r.setPartnerId(rs.getLong("partnerId"));
				re.add(r);
			}

			long sum_price = 0;
			int sum_mo = 0;
			long sum_price_vdc = 0;
			long sum_price_neo = 0;
			long sum_price_vt6x64 = 0;
			for (Revenue reve : re)
			{
				for (ShareRevenue s : share)
				{
					if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3001)
					{
						sum_price_neo += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));

					}

					if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3002)
					{
						sum_price_vdc += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));

					}

					if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3003)
					{
						sum_price_vt6x64 += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));

					}
				}

			}

			if (sum_price_neo <= 200000000)
			{
				sum_price_neo = sum_price_neo * 90 / 100;
			}
			else
			{
				sum_price_neo = sum_price_neo * 92 / 100;
			}

			if (sum_price_vdc <= 100000000)
			{
				sum_price_vdc = sum_price_vdc * 90 / 100;
			}
			else
			{
				sum_price_vdc = sum_price_vdc * 92 / 100;
			}

			sum_price = sum_price_neo + sum_price_vdc + sum_price_vt6x64;

			statistic.setRevenue(String.valueOf(sum_price));
			statistic.setMo(String.valueOf(sum_mo));

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return statistic;
	}

	public Statistics getStatisticLastMonth()
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				Statistics statistic = null;
				try
				{
					String key = NAME_SPACE + "getStatisticLastMonth";
					statistic = (Statistics) client.get(key);
					if (statistic == null)
					{
						statistic = getStatisticLastMonthDB();
						client.set(key, TIME_CACHE, statistic);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return statistic;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getStatisticLastMonthDB();
	}

	public Statistics getStatisticLastMonthDB()
	{
		log.info("####### Get getStatisticLastMonthDB from DB for Manager #########");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		Statistics statistic = new Statistics();
		try
		{
			sql = "select serviceaddr, telcoId, count(*) MO, partnerId from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS')  "
					+ "and s.cause = 'success' "
					+ "group by  serviceaddr, telcoId, partnerId ";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, DateUtils.getFirstDayOfTheLastMonth()
					+ "00:00:00");
			stmt.setString(2, DateUtils.getLastDayOfTheLastMonth() + "23:59:59");
			rs = stmt.executeQuery();

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				r.setPartnerId(rs.getLong("partnerId"));
				re.add(r);
			}

			long sum_price = 0;
			int sum_mo = 0;
			long sum_price_vdc = 0;
			long sum_price_neo = 0;
			long sum_price_vt6x64 = 0;
			for (Revenue reve : re)
			{
				for (ShareRevenue s : share)
				{
					if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3001)
					{
						sum_price_neo += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));

					}

					if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3002)
					{
						sum_price_vdc += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));

					}

					if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
							&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3003)
					{
						sum_price_vt6x64 += (Integer.parseInt(s.getPrice()) * Integer
								.parseInt(reve.getMocouter()));

					}
				}

			}

			if (sum_price_neo <= 200000000)
			{
				sum_price_neo = sum_price_neo * 90 / 100;
			}
			else
			{
				sum_price_neo = sum_price_neo * 92 / 100;
			}

			if (sum_price_vdc <= 100000000)
			{
				sum_price_vdc = sum_price_vdc * 90 / 100;
			}
			else
			{
				sum_price_vdc = sum_price_vdc * 92 / 100;
			}

			sum_price = sum_price_neo + sum_price_vdc + sum_price_vt6x64;

			statistic.setRevenue(String.valueOf(sum_price));
			statistic.setMo(String.valueOf(sum_mo));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return statistic;
	}

	public DataBean getStatisticShortCodeMonth()
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				DataBean bean = null;
				try
				{
					String key = NAME_SPACE + "getStatisticShortCodeMonth";
					bean = (DataBean) client.get(key);
					if (bean == null)
					{
						bean = getStatisticShortCodeMonthDB();
						client.set(key, TIME_CACHE, bean);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return bean;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getStatisticShortCodeMonthDB();
	}

	public DataBean getStatisticShortCodeMonthDB()
	{
		log.info("####### Get getStatisticShortCodeMonthDB from DB for Manager #########");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		List<PieChart> lst = new ArrayList<PieChart>();

		PieChart chart = null;

		try
		{
			sql = "select s.serviceaddr, count(*) counter from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS')  "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS')  "
					+ "and s.cause = 'success' " + "group by s.serviceaddr";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, DateUtils.getFirstDayOfTheMonth() + "00:00:00");
			stmt.setString(2, DateUtils.getLastDayOfTheMonth() + "23:59:59");
			rs = stmt.executeQuery();

			while (rs.next())
			{
				chart = new PieChart();
				chart.setName(rs.getString("serviceAddr"));
				chart.setValue(rs.getFloat("counter"));
				lst.add(chart);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return new DataBean(lst);
	}

	public DataBean getStatisticShortCodeMonth(long merchantId)
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				DataBean bean = null;
				try
				{
					String key = NAME_SPACE + "getStatisticShortCodeMonth"
							+ merchantId;
					bean = (DataBean) client.get(key);
					if (bean == null)
					{
						bean = getStatisticShortCodeMonthDB(merchantId);
						client.set(key, TIME_CACHE, bean);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return bean;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getStatisticShortCodeMonthDB(merchantId);
	}

	public DataBean getStatisticShortCodeMonthDB(long merchantId)
	{
		log.info("####### Get getStatisticShortCodeMonthDB from DB for merchant: "
				+ merchantId + " #########");

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		List<PieChart> lst = new ArrayList<PieChart>();

		PieChart chart = null;

		try
		{
			sql = "select s.serviceaddr, count(*) counter from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS')  "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS')  "
					+ "and s.merchantId = ?"
					+ "and s.cause = 'success' "
					+ "group by s.serviceaddr";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, DateUtils.getFirstDayOfTheMonth() + "00:00:00");
			stmt.setString(2, DateUtils.getLastDayOfTheMonth() + "23:59:59");
			stmt.setLong(3, merchantId);
			rs = stmt.executeQuery();

			while (rs.next())
			{
				chart = new PieChart();
				chart.setName(rs.getString("serviceAddr"));
				chart.setValue(rs.getFloat("counter"));
				lst.add(chart);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return new DataBean(lst);
	}

	public DataBean getRevenueDay()
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				DataBean bean = null;
				try
				{
					String key = NAME_SPACE + "getRevenueDay";
					bean = (DataBean) client.get(key);
					if (bean == null)
					{
						bean = getRevenueDayDB();
						client.set(key, TIME_CACHE, bean);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return bean;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getRevenueDayDB();
	}

	public DataBean getRevenueDayDB()
	{
		log.info("####### Get Revenue from DB for Manager #########");
		List<Date> dates = new ArrayList<Date>();
		DateFormat formatter;

		formatter = new SimpleDateFormat("dd/MM/yyyy");

		DateFormat nFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		String fromDate = formatter.format(Calendar.getInstance().getTime());
		String toDate = formatter.format(Calendar.getInstance().getTime());

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		List<SeriesBean> list = null;

		String[] categories = null;

		try
		{
			sql = "select to_char(s.orderDate,'HH24') reqDate, serviceaddr, telcoId, count(*) MO, partnerId from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.cause = 'success' "
					+ "group by to_char(s.orderDate, 'HH24'), serviceaddr, telcoId, partnerId "
					+ "order by max(orderDate) asc";

			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);

			stmt.setString(1, fromDate + " 00:00:00");
			stmt.setString(2, toDate + " 23:59:59");

			rs = stmt.executeQuery();

			Date startDate = (Date) nFormatter.parse(fromDate + " 00:00:00");
			Date endDate = (Date) nFormatter.parse(toDate + " 23:59:59");
			long interval = 1000 * 60 * 60; // 1 hour
			long endTime = endDate.getTime(); // create your endtime here,
												// possibly using Calendar or
												// Date
			long sysdate = Calendar.getInstance().getTime().getTime();

			if (endTime >= sysdate)
			{
				endTime = sysdate;
			}
			long curTime = startDate.getTime();
			while (curTime <= endTime)
			{
				dates.add(new Date(curTime));
				curTime += interval;
			}

			List<String> price = new ArrayList<String>();
			List<String> priceLastDay = getRevenueLastDay();
			List<String> priceLastWeek = getRevenueLastWeek();
			List<String> priceLastMonth = getRevenueLastMonth();
			List<String> mo = new ArrayList<String>();
			List<String> category = new ArrayList<String>();

			DateFormat formatter1 = new SimpleDateFormat("HH");
			for (Date date : dates)
			{
				Date lDate = (Date) date;
				String ds = formatter1.format(lDate);
				System.out.println(ds);
				category.add(ds);
				price.add("0");
				mo.add("0");
			}

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setReportDate(rs.getString("reqDate"));
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				r.setPartnerId(rs.getLong("partnerId"));
				re.add(r);
			}

			for (int i = 0; i < category.size(); i++)
			{
				long sum_price = 0;
				int sum_mo = 0;
				long sum_price_vdc = 0;
				long sum_price_neo = 0;
				long sum_price_vt6x64 = 0;
				for (Revenue reve : re)
				{
					if (category.get(i).equals(reve.getReportDate()))
					{
						for (ShareRevenue s : share)
						{
							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3001)
							{
								sum_price_neo += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}

							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3002)
							{
								sum_price_vdc += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}

							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3003)
							{
								sum_price_vt6x64 += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}
						}
						sum_mo += Integer.parseInt(reve.getMocouter());
					}
				}

				if (sum_price_neo <= 200000000)
				{
					sum_price_neo = sum_price_neo * 90 / 100;
				}
				else
				{
					sum_price_neo = sum_price_neo * 92 / 100;
				}

				if (sum_price_vdc <= 100000000)
				{
					sum_price_vdc = sum_price_vdc * 90 / 100;
				}
				else
				{
					sum_price_vdc = sum_price_vdc * 92 / 100;
				}

				sum_price = sum_price_neo + sum_price_vdc + sum_price_vt6x64;

				price.set(i, String.valueOf(sum_price));
				mo.set(i, String.valueOf(sum_mo));
			}

			list = new ArrayList<SeriesBean>();
			list.add(new SeriesBean("DT", "", price.toArray(new String[price.size()]), "spline"));
			list.add(new SeriesBean("DTLD", "", priceLastDay.toArray(new String[priceLastDay.size()]), "spline"));
			list.add(new SeriesBean("DTLW", "", priceLastWeek.toArray(new String[priceLastWeek.size()]), "spline"));
			list.add(new SeriesBean("DTLM", "", priceLastMonth.toArray(new String[priceLastMonth.size()]), "spline"));

			categories = category.toArray(new String[category.size()]);

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return new DataBean("container", "THỐNG KÊ DOANH THU", "MO", "", Arrays.asList(categories), list);
	}

	public List<String> getRevenueLastDay()
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				List<String> bean = null;
				try
				{
					String key = NAME_SPACE + "getRevenueLastDay";
					bean = (List<String>) client.get(key);
					if (bean == null)
					{
						bean = getRevenueLastDayDB();
						client.set(key, TIME_CACHE, bean);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return bean;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getRevenueLastDayDB();
	}

	public List<String> getRevenueLastDayDB()
	{
		log.info("####### Get getRevenueLastDayDB for Manager #########");
		List<Date> dates = new ArrayList<Date>();
		DateFormat formatter;

		formatter = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat nFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		Calendar lastDay = Calendar.getInstance();

		lastDay.add(Calendar.DATE, -1);

		String fromDate = formatter.format(lastDay.getTime());
		String toDate = formatter.format(lastDay.getTime());

		List<String> price = new ArrayList<String>();
		List<String> mo = new ArrayList<String>();
		List<String> category = new ArrayList<String>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		try
		{
			sql = "select to_char(s.orderDate,'HH24') reqDate, serviceaddr, telcoId, count(*) MO, partnerId from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.cause = 'success' "
					+ "group by to_char(s.orderDate, 'HH24'), serviceaddr, telcoId, partnerId "
					+ "order by max(orderDate) asc";

			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);

			stmt.setString(1, fromDate + " 00:00:00");
			stmt.setString(2, toDate + " 23:59:59");

			rs = stmt.executeQuery();

			Date startDate = (Date) nFormatter.parse(fromDate + " 00:00:00");
			Date endDate = (Date) nFormatter.parse(toDate + " 23:59:59");
			long interval = 1000 * 60 * 60; // 1 hour
			long endTime = endDate.getTime(); // create your endtime here,
												// possibly using Calendar or
												// Date
			long sysdate = Calendar.getInstance().getTime().getTime();

			if (endTime >= sysdate)
			{
				endTime = sysdate;
			}
			long curTime = startDate.getTime();
			while (curTime <= endTime)
			{
				dates.add(new Date(curTime));
				curTime += interval;
			}

			DateFormat formatter1 = new SimpleDateFormat("HH");
			for (Date date : dates)
			{
				Date lDate = (Date) date;
				String ds = formatter1.format(lDate);
				System.out.println(ds);
				category.add(ds);
				price.add("0");
				mo.add("0");
			}

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setReportDate(rs.getString("reqDate"));
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				r.setPartnerId(rs.getLong("partnerId"));
				re.add(r);
			}

			for (int i = 0; i < category.size(); i++)
			{
				long sum_price = 0;
				int sum_mo = 0;
				long sum_price_vdc = 0;
				long sum_price_neo = 0;
				long sum_price_vt6x64 = 0;
				for (Revenue reve : re)
				{
					if (category.get(i).equals(reve.getReportDate()))
					{
						for (ShareRevenue s : share)
						{
							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3001)
							{
								sum_price_neo += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}

							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3002)
							{
								sum_price_vdc += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}

							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3003)
							{
								sum_price_vt6x64 += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}
						}
						sum_mo += Integer.parseInt(reve.getMocouter());
					}
				}

				if (sum_price_neo <= 200000000)
				{
					sum_price_neo = sum_price_neo * 90 / 100;
				}
				else
				{
					sum_price_neo = sum_price_neo * 92 / 100;
				}

				if (sum_price_vdc <= 100000000)
				{
					sum_price_vdc = sum_price_vdc * 90 / 100;
				}
				else
				{
					sum_price_vdc = sum_price_vdc * 92 / 100;
				}

				sum_price = sum_price_neo + sum_price_vdc + sum_price_vt6x64;

				price.set(i, String.valueOf(sum_price));
				mo.set(i, String.valueOf(sum_mo));
			}

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return price;
	}

	public List<String> getRevenueLastWeek()
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				List<String> bean = null;
				try
				{
					String key = NAME_SPACE + "getRevenueWeekDay";
					bean = (List<String>) client.get(key);
					if (bean == null)
					{
						bean = getRevenueLastWeekDB();
						client.set(key, TIME_CACHE, bean);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return bean;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getRevenueLastWeekDB();
	}

	public List<String> getRevenueLastWeekDB()
	{
		log.info("####### Get getRevenueLastDayDB for Manager #########");
		List<Date> dates = new ArrayList<Date>();
		DateFormat formatter;

		formatter = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat nFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		Calendar lastDay = Calendar.getInstance();

		lastDay.add(Calendar.DATE, -7);

		String fromDate = formatter.format(lastDay.getTime());
		String toDate = formatter.format(lastDay.getTime());

		List<String> price = new ArrayList<String>();
		List<String> mo = new ArrayList<String>();
		List<String> category = new ArrayList<String>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		try
		{
			sql = "select to_char(s.orderDate,'HH24') reqDate, serviceaddr, telcoId, count(*) MO, partnerId from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.cause = 'success' "
					+ "group by to_char(s.orderDate, 'HH24'), serviceaddr, telcoId, partnerId "
					+ "order by max(orderDate) asc";

			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);

			stmt.setString(1, fromDate + " 00:00:00");
			stmt.setString(2, toDate + " 23:59:59");

			rs = stmt.executeQuery();

			Date startDate = (Date) nFormatter.parse(fromDate + " 00:00:00");
			Date endDate = (Date) nFormatter.parse(toDate + " 23:59:59");
			long interval = 1000 * 60 * 60; // 1 hour
			long endTime = endDate.getTime(); // create your endtime here,
												// possibly using Calendar or
												// Date
			long sysdate = Calendar.getInstance().getTime().getTime();

			if (endTime >= sysdate)
			{
				endTime = sysdate;
			}
			long curTime = startDate.getTime();
			while (curTime <= endTime)
			{
				dates.add(new Date(curTime));
				curTime += interval;
			}

			DateFormat formatter1 = new SimpleDateFormat("HH");
			for (Date date : dates)
			{
				Date lDate = (Date) date;
				String ds = formatter1.format(lDate);
				System.out.println(ds);
				category.add(ds);
				price.add("0");
				mo.add("0");
			}

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setReportDate(rs.getString("reqDate"));
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				r.setPartnerId(rs.getLong("partnerId"));
				re.add(r);
			}

			for (int i = 0; i < category.size(); i++)
			{
				long sum_price = 0;
				int sum_mo = 0;
				long sum_price_vdc = 0;
				long sum_price_neo = 0;
				long sum_price_vt6x64 = 0;
				for (Revenue reve : re)
				{
					if (category.get(i).equals(reve.getReportDate()))
					{
						for (ShareRevenue s : share)
						{
							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3001)
							{
								sum_price_neo += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}

							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3002)
							{
								sum_price_vdc += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}

							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3003)
							{
								sum_price_vt6x64 += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}
						}
						sum_mo += Integer.parseInt(reve.getMocouter());
					}
				}

				if (sum_price_neo <= 200000000)
				{
					sum_price_neo = sum_price_neo * 90 / 100;
				}
				else
				{
					sum_price_neo = sum_price_neo * 92 / 100;
				}

				if (sum_price_vdc <= 100000000)
				{
					sum_price_vdc = sum_price_vdc * 90 / 100;
				}
				else
				{
					sum_price_vdc = sum_price_vdc * 92 / 100;
				}

				sum_price = sum_price_neo + sum_price_vdc + sum_price_vt6x64;

				price.set(i, String.valueOf(sum_price));
				mo.set(i, String.valueOf(sum_mo));
			}

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return price;
	}

	public List<String> getRevenueLastMonth()
	{
		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				List<String> bean = null;
				try
				{
					String key = NAME_SPACE + "getRevenueLastMonthDay";
					bean = (List<String>) client.get(key);
					if (bean == null)
					{
						bean = getRevenueLastMonthDB();
						client.set(key, TIME_CACHE, bean);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}
				cachedPool.returnClient(client);
				return bean;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getRevenueLastMonthDB();
	}

	public List<String> getRevenueLastMonthDB()
	{
		log.info("####### Get getRevenueLastMonthDB for Manager #########");
		List<Date> dates = new ArrayList<Date>();
		DateFormat formatter;

		formatter = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat nFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		Calendar lastDay = Calendar.getInstance();

		lastDay.add(Calendar.MONTH, -1);

		String fromDate = formatter.format(lastDay.getTime());
		String toDate = formatter.format(lastDay.getTime());

		List<String> price = new ArrayList<String>();
		List<String> mo = new ArrayList<String>();
		List<String> category = new ArrayList<String>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		OptionDAO optionDAO = new OptionDAOImpl();

		List<ShareRevenue> share = optionDAO.getAllShareReveue();

		try
		{
			sql = "select to_char(s.orderDate,'HH24') reqDate, serviceaddr, telcoId, count(*) MO, partnerId from subscriberOrder@smsgw s "
					+ "where "
					+ "s.orderDate >= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.orderDate <= to_date(?,'DD/MM/YYYY HH24:MI:SS') "
					+ "and s.cause = 'success' "
					+ "group by to_char(s.orderDate, 'HH24'), serviceaddr, telcoId, partnerId "
					+ "order by max(orderDate) asc";

			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);

			stmt.setString(1, fromDate + " 00:00:00");
			stmt.setString(2, toDate + " 23:59:59");

			rs = stmt.executeQuery();

			Date startDate = (Date) nFormatter.parse(fromDate + " 00:00:00");
			Date endDate = (Date) nFormatter.parse(toDate + " 23:59:59");
			long interval = 1000 * 60 * 60; // 1 hour
			long endTime = endDate.getTime(); // create your endtime here,
												// possibly using Calendar or
												// Date
			long sysdate = Calendar.getInstance().getTime().getTime();

			if (endTime >= sysdate)
			{
				endTime = sysdate;
			}
			long curTime = startDate.getTime();
			while (curTime <= endTime)
			{
				dates.add(new Date(curTime));
				curTime += interval;
			}

			DateFormat formatter1 = new SimpleDateFormat("HH");
			for (Date date : dates)
			{
				Date lDate = (Date) date;
				String ds = formatter1.format(lDate);
				System.out.println(ds);
				category.add(ds);
				price.add("0");
				mo.add("0");
			}

			List<Revenue> re = new ArrayList<Revenue>();
			Revenue r = null;

			while (rs.next())
			{
				r = new Revenue();
				r.setReportDate(rs.getString("reqDate"));
				r.setMocouter(rs.getString("mo"));
				r.setShortCode(rs.getString("serviceAddr"));
				r.setTelcoId(rs.getLong("telcoId"));
				r.setPartnerId(rs.getLong("partnerId"));
				re.add(r);
			}

			for (int i = 0; i < category.size(); i++)
			{
				long sum_price = 0;
				int sum_mo = 0;
				long sum_price_vdc = 0;
				long sum_price_neo = 0;
				long sum_price_vt6x64 = 0;
				for (Revenue reve : re)
				{
					if (category.get(i).equals(reve.getReportDate()))
					{
						for (ShareRevenue s : share)
						{
							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3001)
							{
								sum_price_neo += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}

							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3002)
							{
								sum_price_vdc += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}

							if (s.getMerchantId() == 0 && s.getShortCode().equalsIgnoreCase(reve.getShortCode())
									&& s.getTelcoId() == reve.getTelcoId() && s.getPartnerid() == 3003)
							{
								sum_price_vt6x64 += (Integer.parseInt(s.getPrice()) * Integer
										.parseInt(reve.getMocouter()));

							}
						}
						sum_mo += Integer.parseInt(reve.getMocouter());
					}
				}

				if (sum_price_neo <= 200000000)
				{
					sum_price_neo = sum_price_neo * 90 / 100;
				}
				else
				{
					sum_price_neo = sum_price_neo * 92 / 100;
				}

				if (sum_price_vdc <= 100000000)
				{
					sum_price_vdc = sum_price_vdc * 90 / 100;
				}
				else
				{
					sum_price_vdc = sum_price_vdc * 92 / 100;
				}

				sum_price = sum_price_neo + sum_price_vdc + sum_price_vt6x64;

				price.set(i, String.valueOf(sum_price));
				mo.set(i, String.valueOf(sum_mo));
			}

		}
		catch (Exception e)
		{

			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (conn != null)
				{
					conn.close();
				}
				if (stmt != null)
				{
					stmt.close();
				}
				if (rs != null)
				{
					rs.close();
				}
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
			}

		}

		return price;
	}

	public DataBean getRevenueDay(long merchantId)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
