/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vns.model;

import java.io.Serializable;

/**
 *
 * @author hungdt
 */
public class Statistics implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2891371391231475887L;
	public String revenue = "";
    public String mo = "";

    public String getRevenue() {
        return revenue;
    }

    public void setRevenue(String revenue) {
        this.revenue = revenue;
    }

    public String getMo() {
        return mo;
    }

    public void setMo(String mo) {
        this.mo = mo;
    }

}
