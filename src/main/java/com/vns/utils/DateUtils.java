/**
 * 
 */
package com.vns.utils;

import java.text.DateFormatSymbols;
import java.util.Calendar;

/**
 * @author hungdt
 *
 */
public class DateUtils
{

	public static String getLastDayOfTheMonth() throws Exception
	{

		String lastDay = "";
		String lastDate;
		String month;
		String year;

		try
		{
			Calendar cal = Calendar.getInstance();
			if (cal.getActualMaximum(Calendar.DATE) < 10)
			{
				lastDate = "0" + cal.getActualMaximum(Calendar.DATE);
			}
			else
			{
				lastDate = String.valueOf(cal.getActualMaximum(Calendar.DATE));
			}

			if ((cal.get(Calendar.MONTH) + 1) < 10)
			{
				month = "0" + (cal.get(Calendar.MONTH) + 1);
			}
			else
			{
				month = String.valueOf(cal.get(Calendar.MONTH) + 1);
			}

			if (cal.get(Calendar.YEAR) < 10)
			{
				year = "0" + cal.get(Calendar.YEAR);
			}
			else
			{
				year = String.valueOf(cal.get(Calendar.YEAR));
			}
			lastDay = lastDate + "/" + month + "/" + year;
		}
		catch (Exception e)
		{
			throw new Exception("Exception in Date.getLastDateOfTheMonth."
					+ e.getMessage());
		}
		return lastDay;
	}

	public static String getLastDayOfTheLastMonth() throws Exception
	{

		String lastDay = "";
		String lastDate;
		String month;
		String year;

		try
		{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			if (cal.getActualMaximum(Calendar.DATE) < 10)
			{
				lastDate = "0" + cal.getActualMaximum(Calendar.DATE);
			}
			else
			{
				lastDate = String.valueOf(cal.getActualMaximum(Calendar.DATE));
			}

			if ((cal.get(Calendar.MONTH) + 1) < 10)
			{
				month = "0" + (cal.get(Calendar.MONTH) + 1);
			}
			else
			{
				month = String.valueOf(cal.get(Calendar.MONTH) + 1);
			}

			if (cal.get(Calendar.YEAR) < 10)
			{
				year = "0" + cal.get(Calendar.YEAR);
			}
			else
			{
				year = String.valueOf(cal.get(Calendar.YEAR));
			}
			lastDay = lastDate + "/" + month + "/" + year;
		}
		catch (Exception e)
		{
			throw new Exception("Exception in Date.getLastDateOfTheMonth."
					+ e.getMessage());
		}
		return lastDay;
	}

	public static String getShortMonthName(int month) throws Exception
	{

		String monthAcronym = "";

		try
		{
			String[] shortMonths = new DateFormatSymbols().getShortMonths();
			monthAcronym = shortMonths[month];
		}
		catch (Exception e)
		{
			throw new Exception("Exception in Date.getShortMonthName."
					+ e.getMessage());
		}
		return monthAcronym;
	}

	public static String getFirstDayOfTheMonth() throws Exception
	{

		String month;
		String year;
		String firstDate;
		String firstDay;
		try
		{
			Calendar cal = Calendar.getInstance();
			if (cal.getActualMinimum(Calendar.DATE) < 10)
			{
				firstDate = "0" + cal.getActualMinimum(Calendar.DATE);
			}
			else
			{
				firstDate = String.valueOf(cal.getActualMinimum(Calendar.DATE));
			}

			if ((cal.get(Calendar.MONTH) + 1) < 10)
			{
				month = "0" + (cal.get(Calendar.MONTH) + 1);
			}
			else
			{
				month = String.valueOf(cal.get(Calendar.MONTH) + 1);
			}

			if (cal.get(Calendar.YEAR) < 10)
			{
				year = "0" + cal.get(Calendar.YEAR);
			}
			else
			{
				year = String.valueOf(cal.get(Calendar.YEAR));
			}

			firstDay = firstDate + "/" + month + "/" + year;
		}
		catch (Exception e)
		{
			throw new Exception("Exception in Date.getFirstDayOfTheMonth."
					+ e.getMessage());
		}
		return firstDay;
	}

	public static String getFirstDayOfTheLastMonth() throws Exception
	{

		String month;
		String year;
		String firstDate;
		String firstDay;
		try
		{
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			if (cal.getActualMinimum(Calendar.DATE) < 10)
			{
				firstDate = "0" + cal.getActualMinimum(Calendar.DATE);
			}
			else
			{
				firstDate = String.valueOf(cal.getActualMinimum(Calendar.DATE));
			}

			if ((cal.get(Calendar.MONTH) + 1) < 10)
			{
				month = "0" + (cal.get(Calendar.MONTH) + 1);
			}
			else
			{
				month = String.valueOf(cal.get(Calendar.MONTH) + 1);
			}

			if (cal.get(Calendar.YEAR) < 10)
			{
				year = "0" + cal.get(Calendar.YEAR);
			}
			else
			{
				year = String.valueOf(cal.get(Calendar.YEAR));
			}

			firstDay = firstDate + "/" + month + "/" + year;
		}
		catch (Exception e)
		{
			throw new Exception("Exception in Date.getFirstDayOfTheMonth."
					+ e.getMessage());
		}
		return firstDay;
	}

	public static void main(String[] args) throws Exception
	{
		System.out.println(getFirstDayOfTheMonth());
		System.out.println(getLastDayOfTheMonth());
	}

}
