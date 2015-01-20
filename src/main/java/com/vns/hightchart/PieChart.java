/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : PieChart.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Jan 9, 2015 5:59:44 PM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Jan 9, 2015       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.hightchart;

import java.io.Serializable;

/**
 * @author Hung
 *
 */
public class PieChart implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7953918176472861998L;

	private String name;
	private double value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
