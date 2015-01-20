/**
 * -----------------------------------------------------------------------------
 *
 * @ Copyright(c) 2012 VNS Telecom. JSC. All Rights Reserved.
 * -----------------------------------------------------------------------------
 * FILE NAME : IndexController.java DESCRIPTION : PRINCIPAL AUTHOR : Do Tien
 * Hung SYSTEM NAME : report MODULE NAME : LANGUAGE : Java DATE OF FIRST RELEASE
 * :
 * -----------------------------------------------------------------------------
 * @ Datetime Dec 22, 2014 10:27:18 AM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	Author	Version Description
 * -----------------------------------------------------------------------------------
 * Dec 22, 2014 hungdt 1.0 Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.vns.dao.ReportDAO;
import com.vns.dao.ReportDAOImpl;
import com.vns.hightchart.DataBean;
import com.vns.model.Revenue;
import com.vns.model.RevenueReq;
import com.vns.model.Statistics;
import com.vns.model.UserMeta;
import com.vns.model.Users;
import com.vns.utils.Constants;

/**
 * @author Hung
 *
 */
@Controller
@RequestMapping("/home")
public class IndexController
{

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView createHomePage(HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		ModelAndView mv = new ModelAndView("index");

		String rolename = "";
		String merchantId = "0";
		Users user = (Users) request.getSession().getAttribute("user");

		if (user == null)
		{
			System.out.println("Chua dang nhap");

			response.sendRedirect("login.htm");
			return null;
		}

		List<UserMeta> lst = (List<UserMeta>) request.getSession()
				.getAttribute("lstUserMeta");

		for (UserMeta meta : lst)
		{
			if (meta.getMeta_key().equals(Constants.rp_role))
			{
				rolename = meta.getMeta_value();
			}
			if (meta.getMeta_key().equals(Constants.rp_merchantId))
			{
				merchantId = meta.getMeta_value();
			}
		}
		mv.addObject("rolename", rolename);
		mv.addObject("merchantId", merchantId);
		mv.addObject("displayname", user.getDisplay_name().toString());
		return mv;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/loadchart", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody DataBean createReportHomePage(
			@RequestBody RevenueReq req, HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		String rolename = "";
		Users user = (Users) request.getSession().getAttribute("user");

		if (user == null)
		{
			System.out.println("Chua dang nhap");
			response.sendRedirect("login.htm");
			return null;
		}

		List<UserMeta> lst = (List<UserMeta>) request.getSession()
				.getAttribute("lstUserMeta");

		for (UserMeta meta : lst)
		{
			if (meta.getMeta_key().equals(Constants.rp_role))
			{
				rolename = meta.getMeta_value();
			}
		}

		DataBean n = null;

		ReportDAO reportDAO = new ReportDAOImpl();

		String fromDate = req.getFromDate();
		String toDate = req.getToDate();

		if (rolename.equalsIgnoreCase("admin"))
		{
			n = reportDAO.getRevenue(fromDate, toDate);
		}
		else if (rolename.equalsIgnoreCase("merchant"))
		{
			n = reportDAO.getRevenue(req.getFromDate(), req.getToDate(),
					req.getMerchantId());
		}

		return n;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/loadcharthour", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody DataBean loadChartHour(
			@RequestBody RevenueReq req, HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		String rolename = "";
		Users user = (Users) request.getSession().getAttribute("user");

		if (user == null)
		{
			System.out.println("Chua dang nhap");
			response.sendRedirect("login.htm");
			return null;
		}

		List<UserMeta> lst = (List<UserMeta>) request.getSession()
				.getAttribute("lstUserMeta");

		for (UserMeta meta : lst)
		{
			if (meta.getMeta_key().equals(Constants.rp_role))
			{
				rolename = meta.getMeta_value();
			}
		}

		DataBean n = null;

		ReportDAO reportDAO = new ReportDAOImpl();
		

		if (rolename.equalsIgnoreCase("admin"))
		{
			n = reportDAO.getRevenueDay();
		}
		else if (rolename.equalsIgnoreCase("merchant"))
		{
			
		}

		return n;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/loadpiechart", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody DataBean loadPieChart(HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		String rolename = "";
		long merchantId = 0;
		Users user = (Users) request.getSession().getAttribute("user");

		if (user == null)
		{
			System.out.println("Chua dang nhap");
			response.sendRedirect("login.htm");
			return null;
		}

		List<UserMeta> lst = (List<UserMeta>) request.getSession()
				.getAttribute("lstUserMeta");

		for (UserMeta meta : lst)
		{
			if (meta.getMeta_key().equals(Constants.rp_role))
			{
				rolename = meta.getMeta_value();
			}
			if (meta.getMeta_key().equals(Constants.rp_merchantId))
			{
				merchantId = Long.parseLong(meta.getMeta_value());
			}
		}

		DataBean n = null;

		ReportDAO reportDAO = new ReportDAOImpl();

		if (rolename.equalsIgnoreCase("admin"))
		{
			n = reportDAO.getStatisticShortCodeMonth();
		}
		else if (rolename.equalsIgnoreCase("merchant"))
		{
			n = reportDAO.getStatisticShortCodeMonth(merchantId);
		}

		return n;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getday", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Statistics getStatisticDay(HttpServletRequest request,
			HttpServletResponse response) throws IOException
	{
		String rolename = "";
		long merchantId = 0;
		Users user = (Users) request.getSession().getAttribute("user");

		if (user == null)
		{
			System.out.println("Chua dang nhap");
			response.sendRedirect("login.htm");
			return null;
		}

		List<UserMeta> lst = (List<UserMeta>) request.getSession()
				.getAttribute("lstUserMeta");

		for (UserMeta meta : lst)
		{
			if (meta.getMeta_key().equals(Constants.rp_role))
			{
				rolename = meta.getMeta_value();
			}

			if (meta.getMeta_key().equals(Constants.rp_merchantId))
			{
				merchantId = Long.parseLong(meta.getMeta_value());
			}
		}

		Statistics s = null;

		ReportDAO reportDAO = new ReportDAOImpl();

		if (rolename.equalsIgnoreCase("admin"))
		{
			s = reportDAO.getStatisticDay();
		}
		else if (rolename.equalsIgnoreCase("merchant"))
		{
			s = reportDAO.getStatisticDay(merchantId);
		}

		return s;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getlastday", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Statistics getStatisticLastDay(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException
	{
		String rolename = "";
		long merchantId = 0;
		Users user = (Users) request.getSession().getAttribute("user");

		if (user == null)
		{
			System.out.println("Chua dang nhap");
			response.sendRedirect("login.htm");
			return null;
		}

		List<UserMeta> lst = (List<UserMeta>) request.getSession()
				.getAttribute("lstUserMeta");

		for (UserMeta meta : lst)
		{
			if (meta.getMeta_key().equals(Constants.rp_role))
			{
				rolename = meta.getMeta_value();
			}

			if (meta.getMeta_key().equals(Constants.rp_merchantId))
			{
				merchantId = Long.parseLong(meta.getMeta_value());
			}
		}

		Statistics s = null;

		ReportDAO reportDAO = new ReportDAOImpl();

		if (rolename.equalsIgnoreCase("admin"))
		{
			s = reportDAO.getStatisticLastDay();
		}
		else if (rolename.equalsIgnoreCase("merchant"))
		{
			s = reportDAO.getStatisticLastDay(merchantId);
		}

		return s;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getmonth", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Statistics getStatisticMonth(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException
	{
		String rolename = "";
		long merchantId = 0;
		Users user = (Users) request.getSession().getAttribute("user");

		if (user == null)
		{
			System.out.println("Chua dang nhap");
			response.sendRedirect("login.htm");
			return null;
		}

		List<UserMeta> lst = (List<UserMeta>) request.getSession()
				.getAttribute("lstUserMeta");

		for (UserMeta meta : lst)
		{
			if (meta.getMeta_key().equals(Constants.rp_role))
			{
				rolename = meta.getMeta_value();
			}

			if (meta.getMeta_key().equals(Constants.rp_merchantId))
			{
				merchantId = Long.parseLong(meta.getMeta_value());
			}
		}

		Statistics s = null;

		ReportDAO reportDAO = new ReportDAOImpl();

		if (rolename.equalsIgnoreCase("admin"))
		{
			s = reportDAO.getStatisticMonth();
		}
		else if (rolename.equalsIgnoreCase("merchant"))
		{
			s = reportDAO.getStatisticMonth(merchantId);
		}

		return s;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getlastmonth", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Statistics getStatisticLastMonth(
			HttpServletRequest request, HttpServletResponse response)
			throws IOException
	{
		String rolename = "";
		long merchantId = 0;
		Users user = (Users) request.getSession().getAttribute("user");

		if (user == null)
		{
			System.out.println("Chua dang nhap");
			response.sendRedirect("login.htm");
			return null;
		}

		List<UserMeta> lst = (List<UserMeta>) request.getSession()
				.getAttribute("lstUserMeta");

		for (UserMeta meta : lst)
		{
			if (meta.getMeta_key().equals(Constants.rp_role))
			{
				rolename = meta.getMeta_value();
			}

			if (meta.getMeta_key().equals(Constants.rp_merchantId))
			{
				merchantId = Long.parseLong(meta.getMeta_value());
			}
		}

		Statistics s = null;

		ReportDAO reportDAO = new ReportDAOImpl();

		if (rolename.equalsIgnoreCase("admin"))
		{
			s = reportDAO.getStatisticLastMonth();
		}
		else if (rolename.equalsIgnoreCase("merchant"))
		{
			s = reportDAO.getStatisticLastMonth(merchantId);
		}

		return s;
	}

}
