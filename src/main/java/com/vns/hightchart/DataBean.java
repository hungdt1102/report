/**
 * 
 */
package com.vns.hightchart;

import java.io.Serializable;
import java.util.List;

/**
 * @author hungdt
 *
 */
public class DataBean implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5131193691336194191L;
	private String				divId;
	private String				title;
	private String				yAxisTitle;
	private String				xAxisTitle;
	private List<String>		categories;
	private List<SeriesBean>	series;
	private List<PieChart>		series_pie;

	public DataBean(String divId, String title, String yAxisTitle,
			String xAxisTitle, List<String> categories, List<SeriesBean> series)
	{
		super();
		this.divId = divId;
		this.title = title;
		this.yAxisTitle = yAxisTitle;
		this.xAxisTitle = xAxisTitle;
		this.categories = categories;
		this.series = series;
	}

	public DataBean(List<PieChart> series_pie)
	{
		super();
		this.series_pie = series_pie;
	}

	public List<PieChart> getSeries_pie()
	{
		return series_pie;
	}

	public void setSeries_pie(List<PieChart> series_pie)
	{
		this.series_pie = series_pie;
	}

	public String getDivId()
	{
		return divId;
	}

	public void setDivId(String divId)
	{
		this.divId = divId;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getyAxisTitle()
	{
		return yAxisTitle;
	}

	public void setyAxisTitle(String yAxisTitle)
	{
		this.yAxisTitle = yAxisTitle;
	}

	public String getxAxisTitle()
	{
		return xAxisTitle;
	}

	public void setxAxisTitle(String xAxisTitle)
	{
		this.xAxisTitle = xAxisTitle;
	}

	public List<String> getCategories()
	{
		return categories;
	}

	public void setCategories(List<String> categories)
	{
		this.categories = categories;
	}

	public List<SeriesBean> getSeries()
	{
		return series;
	}

	public void setSeries(List<SeriesBean> series)
	{
		this.series = series;
	}

}
