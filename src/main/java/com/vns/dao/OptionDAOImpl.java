/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : OptionDAOImpl.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Dec 23, 2014 5:33:53 PM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Dec 23, 2014       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.vns.memcached.IMemcacheClient;
import com.vns.model.Option;
import com.vns.model.ShareRevenue;
import com.vns.sql.Database;
import com.vns.utils.CacheInit;

/**
 * @author Hung
 *
 */
public class OptionDAOImpl extends CacheInit implements OptionDAO
{
	Logger						log			= Logger.getLogger(OptionDAOImpl.class);

	private static final String	NAME_SPACE	= "share";
	private static final int	TIME_CACHE	= 300;

	public List<Option> getAllOption()
	{
		List<Option> lstOp = new ArrayList<Option>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		Option op = null;

		try
		{
			sql = "select * from rp_options where autoload = 1";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);

			rs = stmt.executeQuery();
			while (rs.next())
			{
				op = new Option();
				op.setId(rs.getLong("ID"));
				op.setOption_name(rs.getString("option_name"));
				op.setOption_value(rs.getString("option_value"));
				op.setAutoload(rs.getInt("autoload"));
				lstOp.add(op);
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

		return lstOp;
	}

	public Option getOptionByName(String optionName)
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		Option op = null;

		try
		{
			sql = "select * from rp_options where autoload = 1 and option_name = ?";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, optionName);
			rs = stmt.executeQuery();
			while (rs.next())
			{
				op = new Option();
				op.setId(rs.getLong("ID"));
				op.setOption_name(rs.getString("option_name"));
				op.setOption_value(rs.getString("option_value"));
				op.setAutoload(rs.getInt("autoload"));
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

		return op;
	}

	public List<ShareRevenue> getAllShareReveue()
	{

		try
		{
			if (isUseCache)
			{
				IMemcacheClient client = cachedPool.borrowClient();
				List<ShareRevenue> lst = null;
				try
				{
					String key = NAME_SPACE + "getAllShareReveue";
					lst = (List<ShareRevenue>) client.get(key);
					if (lst == null)
					{
						lst = getAllShareDB();
						client.set(key, TIME_CACHE, lst);
					}
				}
				catch (Exception e)
				{
					log.error(e.getMessage(), e);
				}

				cachedPool.returnClient(client);
				return lst;
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}

		return getAllShareDB();
	}

	// Caching
	public List<ShareRevenue> getAllShareDB()
	{
		List<ShareRevenue> lst = new ArrayList<ShareRevenue>();

		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		ShareRevenue share = null;

		try
		{
			sql = "select * from rp_share_revenue";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			rs = stmt.executeQuery();
			while (rs.next())
			{
				share = new ShareRevenue();
				share.setId(rs.getInt("id"));
				share.setMerchantId(rs.getLong("merchantId"));
				share.setPartnerid(rs.getLong("partnerId"));
				share.setPrice(rs.getString("price"));
				share.setShortCode(rs.getString("shortCode"));
				share.setTelcoId(rs.getLong("telcoId"));
				lst.add(share);
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

		return lst;
	}

}
