/**
 * 
 */
package com.vns.hightchart;

import java.io.Serializable;

/**
 * @author hungdt
 *
 */
public class SeriesBean implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 613239895406002861L;
	private String				name;
	private String				color;
	private String[]			data;
	private String				type;

	public SeriesBean(String name, String color, String[] data, String type)
	{
		super();
		this.name = name;
		this.color = color;
		this.data = data;
		this.type = type;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getColor()
	{
		return color;
	}

	public void setColor(String color)
	{
		this.color = color;
	}

	public String[] getData()
	{
		return data;
	}

	public void setData(String[] data)
	{
		this.data = data;
	}

}
