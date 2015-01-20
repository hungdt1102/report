/**
 * 
 */
package com.vns.model;

import java.io.Serializable;

/**
 * @author hungdt
 *
 */
public class ShareRevenue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private long partnerid;
	private String shortCode;
	private String price;
	private long telcoId;
	private long merchantId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(long partnerid) {
		this.partnerid = partnerid;
	}

	public String getShortCode() {
		return shortCode;
	}

	public void setShortCode(String shortCode) {
		this.shortCode = shortCode;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public long getTelcoId() {
		return telcoId;
	}

	public void setTelcoId(long telcoId) {
		this.telcoId = telcoId;
	}

	public long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(long merchantId) {
		this.merchantId = merchantId;
	}

}
