/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : LoginController.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Dec 22, 2014 1:55:50 PM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Dec 22, 2014       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.vns.dao.OptionDAO;
import com.vns.dao.OptionDAOImpl;
import com.vns.dao.UserDAO;
import com.vns.dao.UserDAOImpl;
import com.vns.model.Option;
import com.vns.model.UserMeta;
import com.vns.model.Users;

/**
 * @author Hung
 *
 */
@Controller
@RequestMapping("/login")
public class LoginController
{

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView loginPage(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		ModelAndView mv = new ModelAndView("login");

		String act = request.getParameter("act") == null ? "" : request.getParameter("act");

		if (act.equals("logout"))
		{
			request.getSession().removeAttribute("user");
			request.getSession().removeAttribute("lstUserMeta");
		}
		else
		{
			if (request.getSession().getAttribute("user") != null && request.getSession().getAttribute("lstUserMeta") != null)
			{
				response.sendRedirect("home.htm");
			}
		}

		return mv;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView checkLogin(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		ModelAndView mv = new ModelAndView("login");

		String error = "";

		String username = request.getParameter("username");
		String password = request.getParameter("password");

		UserDAO userDAO = new UserDAOImpl();
		OptionDAO optionDAO = new OptionDAOImpl();

		Users user = userDAO.checkLogin(username.toLowerCase(), password);

		if (user != null)
		{
			System.out.println("User " + user.getUser_login() + " login succesfully!");
			List<UserMeta> lstUserMeta = userDAO.getAllUserMeta(user.getUserId());
			List<Option> lstOption = optionDAO.getAllOption();

			request.getSession().setAttribute("user", user);
			request.getSession().setAttribute("lstUserMeta", lstUserMeta);
			request.getSession().setAttribute("option", lstOption);

			response.sendRedirect("home.htm");
		}
		else
		{
			error = "Username Or Password Invalid!";
		}

		mv.addObject("error", error);
		return mv;
	}
}
