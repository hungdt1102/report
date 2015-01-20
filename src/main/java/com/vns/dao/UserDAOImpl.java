/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : UserDAOImpl.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Dec 22, 2014 2:43:01 PM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Dec 22, 2014       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.vns.model.UserMeta;
import com.vns.model.Users;
import com.vns.sql.Database;

/**
 * @author Hung
 *
 */
public class UserDAOImpl implements UserDAO
{
	
	public Users checkLogin(String username, String password)
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Users user = null;
		String sql = "";
		try
		{
			sql = "select * from rp_users where LOWER(USER_LOGIN) = ? and USER_PASS = ?";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, username);
			stmt.setString(2, password);

			rs = stmt.executeQuery();
			if (rs.next())
			{
				user = new Users();
				user.setUserId(rs.getLong("ID"));
				user.setUser_login(rs.getString("USER_LOGIN"));
				user.setDisplay_name(rs.getString("DISPLAY_NAME"));
				user.setUser_email(rs.getString("USER_EMAIL"));
				user.setUser_status(rs.getInt("USER_STATUS"));
				user.setUser_url(rs.getString("USER_URL"));
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
		return user;
	}

	public UserMeta getUserMeta(String meta_key, long userId)
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		UserMeta uMeta = null;

		try
		{
			sql = "select * from RP_USERMETA where meta_key = ? and user_Id = ?";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, meta_key);
			stmt.setLong(2, userId);

			rs = stmt.executeQuery();
			if (rs.next())
			{
				uMeta = new UserMeta();
				uMeta.setMetaId(rs.getLong("ID"));
				uMeta.setUserId(rs.getLong("USER_ID"));
				uMeta.setMeta_key(rs.getString("META_KEY"));
				uMeta.setMeta_value(rs.getString("META_VALUE"));
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

		return uMeta;
	}

	public List<UserMeta> getAllUserMeta(long userId)
	{
		List<UserMeta> lstUMeta = new ArrayList<UserMeta>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		UserMeta uMeta = null;
		try
		{
			sql = "select * from RP_USERMETA where user_Id = ?";
			conn = Database.getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setLong(1, userId);
			rs = stmt.executeQuery();
			while (rs.next())
			{
				uMeta = new UserMeta();
				uMeta.setMetaId(rs.getLong("ID"));
				uMeta.setUserId(rs.getLong("USER_ID"));
				uMeta.setMeta_key(rs.getString("META_KEY"));
				uMeta.setMeta_value(rs.getString("META_VALUE"));
				lstUMeta.add(uMeta);
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

		return lstUMeta;
	}

	public void inserUser(Users user)
	{
		// TODO Auto-generated method stub

	}

	public void updateUser(Users user)
	{
		// TODO Auto-generated method stub

	}

	public void deleteUser(long id)
	{
		// TODO Auto-generated method stub

	}

	public List<Users> getAllUser()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
