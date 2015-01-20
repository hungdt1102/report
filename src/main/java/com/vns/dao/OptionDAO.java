/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : OptionDAO.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Dec 23, 2014 5:31:11 PM
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

import com.vns.model.Option;
import com.vns.model.ShareRevenue;

/**
 * @author Hung
 *
 */
public interface OptionDAO
{
	public List<Option> getAllOption();

	public Option getOptionByName(String optionName);
	
	public List<ShareRevenue> getAllShareReveue();
	
}
