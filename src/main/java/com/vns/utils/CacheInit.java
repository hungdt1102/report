/**
 *-----------------------------------------------------------------------------
 * @ Copyright(c) 2012  VNS Telecom. JSC. All Rights Reserved.
 *-----------------------------------------------------------------------------
 * FILE  NAME             : CacheInit.java
 * DESCRIPTION            :
 * PRINCIPAL AUTHOR       : Do Tien Hung
 * SYSTEM NAME            : report
 * MODULE NAME            : 
 * LANGUAGE               : Java
 * DATE OF FIRST RELEASE  : 
 *-----------------------------------------------------------------------------
 * @ Datetime Jan 7, 2015 11:06:44 AM
 * @ Release 1.0.0.0
 * @ Version 1.0
 * -----------------------------------------------------------------------------------
 * Date	              Author	       Version          Description
 * -----------------------------------------------------------------------------------
 * Jan 7, 2015       hungdt            1.0 	       Initial Create
 * -----------------------------------------------------------------------------------
 */
package com.vns.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.vns.memcached.MemcacheClientPool;

/**
 * @author Hung
 *
 */
public class CacheInit {
	private static final Logger mLog = Logger.getLogger(CacheInit.class);

	public static String lineSeperator = "";
	public static String fileSeperator = "";

	private static final String REPORT_NAMESPACE = "rpstart";
	private static final String SERVER_LIST = "127.0.0.1";

	public static final int TIME_CACHE = 600;

	public static boolean isUseCache = true;

	protected static MemcacheClientPool cachedPool;

	public static MemcacheClientPool getCachedPool() {
		return cachedPool;
	}

	public static void finishCache() {
		isUseCache = false;
		cachedPool.shutdown();
	}

	public static void initCache() {
		try {
			AppProperties configProvider = new AppProperties();
			configProvider.loadFromFile(ReportConfig.configPath
					+ "ServerConfig.txt");

			String host = configProvider.getString("memcachedServer",
					SERVER_LIST);
			String port = configProvider.getString("memcachedPort", "9501");

			String server = host + ":" + port;

			cachedPool = new MemcacheClientPool();
			cachedPool.start(server, REPORT_NAMESPACE);

		} catch (Exception e) {
			cachedPool = new MemcacheClientPool();
			cachedPool.start(SERVER_LIST, REPORT_NAMESPACE);

			mLog.log(Level.ERROR, "Loi memcached", e);
		}
	}

	/**
	 * Copyright definition
	 */
	public static final String copyright = "# Copyright (c) 2014 VNSTelecom JSC "
			+ lineSeperator
			+ "# This product includes software developed by VNSTelecom by mR.kAKa copyright "
			+ lineSeperator
			+ "# and know-how are retained, all rights reserved.";

	public static final String projectName = "# ProjectName: "
			+ Constants.project_name;
	public static final String version = "# Version: " + Constants.version;

}
