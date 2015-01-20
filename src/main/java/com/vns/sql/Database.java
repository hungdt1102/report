/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : Database.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Jan 7, 2015 3:28:43 PM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Jan 7, 2015       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.vns.utils.AppException;
import com.vns.utils.AppProperties;
import com.vns.utils.ReportConfig;

/**
 * @author Hung
 *
 */
public class Database
{
	public static ComboPooledDataSource	appDatasource	= null;
	private static Object				mutex			= "mutex";

	{

	}

	public static int getNumberConnection() throws Exception
	{
		return appDatasource.getNumConnectionsDefaultUser();
	}

	public static int getNumberBusyConnection() throws Exception
	{
		return appDatasource.getNumBusyConnectionsDefaultUser();
	}

	public static int getNumberIdleConnection() throws Exception
	{
		return appDatasource.getNumIdleConnectionsDefaultUser();
	}

	public static Connection getConnection() throws Exception
	{
		if (appDatasource == null)
		{
			synchronized (mutex)
			{
				if (appDatasource == null)
				{
					AppProperties configProvider = new AppProperties();
					configProvider.loadFromFile(ReportConfig.configPath + "ServerConfig.txt");
					System.setProperty("com.mchange.v2.c3p0.cfg.xml",
							configProvider.getString("c3p0-config"));

					appDatasource = new ComboPooledDataSource();
				}
			}
		}

		try
		{

			Connection connection = appDatasource.getConnection();
			connection.setAutoCommit(true);

			return connection;
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	public static long getSequence(Connection connection, String sequence)
			throws Exception
	{
		long seqValue = 0;

		PreparedStatement stmtSequence = null;

		ResultSet rsSequence = null;

		try
		{
			stmtSequence = connection.prepareStatement("Select " + sequence
					+ ".nextVal from dual");

			rsSequence = stmtSequence.executeQuery();

			if (rsSequence.next())
			{
				seqValue = rsSequence.getLong(1);
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			closeObject(rsSequence);
			closeObject(stmtSequence);
		}

		return seqValue;
	}

	public static long getSequence(String sequence) throws Exception
	{
		Connection connection = null;

		long value = 0;

		try
		{
			value = getSequence(connection, sequence);
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			closeObject(connection);
		}

		return value;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Execute query and get first value from database
	 * 
	 * @param cn
	 *            opened connection
	 * @param strTableName
	 *            table to query
	 * @param strFieldName
	 *            field need to get value
	 * @param strCondition
	 *            condition
	 * @throws Exception
	 * @return String
	 */
	// ///////////////////////////////////////////////////////////////
	public static String getValue(Connection cn, String strTableName,
			String strFieldName, String strCondition) throws Exception
	{
		// Get query data
		Statement stmt = cn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT " + strFieldName + " FROM "
				+ strTableName + " WHERE " + strCondition);

		// Validation
		if (!rs.next())
		{
			rs.close();
			stmt.close();
			throw new AppException("FSS-00009", "getValue", strTableName + "."
					+ strFieldName);
		}

		String strReturn = rs.getString(1);
		rs.close();
		stmt.close();

		return strReturn;
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Get sequence value from database
	 * 
	 * @param cn
	 *            opened connection
	 * @param sequenceName
	 *            Name of sequence
	 * @throws Exception
	 * @return String
	 */
	// ///////////////////////////////////////////////////////////////
	public static String getSequenceValue(Connection cn, String sequenceName)
			throws Exception
	{
		return getSequenceValue(cn, sequenceName, true);
	}

	// ///////////////////////////////////////////////////////////////
	public static String getSequenceValue(Connection cn, String sequenceName,
			boolean bAutoCreate) throws Exception
	{
		// SQL command to sequence value
		String strSQL = "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";

		// Get query data
		try
		{
			Statement stmt = cn.createStatement();
			ResultSet rs = stmt.executeQuery(strSQL);
			rs.next();
			String strReturn = rs.getString(1);
			rs.close();
			stmt.close();
			return strReturn;
		}
		catch (Exception e)
		{
			if (e.getMessage() != null
					&& e.getMessage().startsWith("ORA-02289"))
			{
				if (!bAutoCreate)
					throw new AppException("FSS-00018", "getSequenceValue",
							sequenceName);
				else
				{
					Statement stmt = cn.createStatement();
					stmt.executeUpdate("CREATE SEQUENCE " + sequenceName
							+ " START WITH 2");
					stmt.close();
					return "1";
				}
			}
			else
				throw e;
		}
	}

	public static void rollback(Connection connection)
	{
		if (connection != null)
		{
			try
			{
				connection.rollback();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment, BatchStatement
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(CallableStatement obj)
	{
		try
		{
			if (obj != null)
				obj.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment, BatchStatement
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(Statement obj)
	{
		try
		{
			if (obj != null)
				obj.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment, BatchStatement
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(ResultSet obj)
	{
		try
		{
			if (obj != null)
				obj.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////////////////////////////
	/**
	 * Close objects which are used to interact with database
	 * 
	 * @param obj
	 *            Object need to be closed such as Statment, PrepareStatment,
	 *            Connection, ResuletSet, CallableStatment, BatchStatement
	 */
	// ///////////////////////////////////////////////////////////////
	public static void closeObject(Connection obj)
	{
		try
		{
			if ((obj != null))
			{
				if (!obj.isClosed() && !obj.getAutoCommit())
				{
					try
					{
						obj.rollback();
					}
					catch (Exception e)
					{
						// e.printStackTrace();
					}
				}

				obj.setAutoCommit(true);

				obj.close();
			}
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}
	}

	public static String getString(ResultSet rs, String field, String nullValue)
			throws SQLException
	{
		String value = nullValue;

		if (rs != null)
		{
			value = rs.getString(field);

			if (value == null)
			{
				value = nullValue;
			}
		}

		return value;
	}

	public static String getString(ResultSet rs, String field)
			throws SQLException
	{
		return getString(rs, field, "");
	}

	public static String getDomainValue(String type, String alias)
			throws Exception
	{
		Connection connection = null;
		PreparedStatement stmtDomain = null;
		ResultSet rsDomain = null;

		String value = "";

		try
		{
			connection = getConnection();
			stmtDomain = connection
					.prepareStatement("Select * From AppDomain Where type_ = ? and alias_ = ? ");
			stmtDomain.setString(1, type);
			stmtDomain.setString(2, alias);

			rsDomain = stmtDomain.executeQuery();

			if (rsDomain.next())
			{
				value = rsDomain.getString("value");
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			closeObject(rsDomain);
			closeObject(stmtDomain);
			closeObject(connection);
		}

		return value;
	}

	public static String getSysParam(String type, String alias)
			throws Exception
	{
		Connection connection = null;
		PreparedStatement stmtParam = null;
		ResultSet rsParam = null;

		String value = "";

		try
		{
			connection = getConnection();
			stmtParam = connection
					.prepareStatement("Select * From AppParam Where type_ = ? and alias_ = ? ");
			stmtParam.setString(1, type);
			stmtParam.setString(2, alias);

			rsParam = stmtParam.executeQuery();

			if (rsParam.next())
			{
				value = rsParam.getString("value");
			}
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			closeObject(rsParam);
			closeObject(stmtParam);
			closeObject(connection);
		}

		return value;
	}

	public static void updateSysParam(String type, String alias, String value)
			throws Exception
	{
		Connection connection = null;
		PreparedStatement stmtParam = null;

		try
		{
			connection = getConnection();

			stmtParam = connection
					.prepareStatement("Update AppParam Set value = ? Where type_ = ? and alias_ = ? ");
			stmtParam.setString(1, value);
			stmtParam.setString(2, type);
			stmtParam.setString(3, alias);

			stmtParam.execute();
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			closeObject(stmtParam);
			closeObject(connection);
		}
	}
}
