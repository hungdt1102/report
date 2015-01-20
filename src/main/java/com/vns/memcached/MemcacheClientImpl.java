package com.vns.memcached;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import net.spy.memcached.MemcachedClient;

class MemcacheClientImpl extends AbstractMemcacheClient
{
	private static final Logger	logger		= Logger.getLogger(MemcacheClientImpl.class);

	private MemcachedClient		mClient;
	private String				mNameSpace;
	private String				host		= "localhost";
	private int					port		= 9501;

	private final int			MAX_KEY_LEN	= 450;

	public MemcacheClientImpl(String aAddrList, String aNameSpace) throws Throwable
	{
		this.mClient = new MemcachedClient(new InetSocketAddress(host, port));
		this.mNameSpace = aNameSpace;
	}

	public void set(String aKey, int aExpiration, Object aSerializableObj)
	{
		if (aSerializableObj != null)
		{
			try
			{
				String fullKey = this.mNameSpace + "_" + aKey;
				fullKey.replace("/", "SsS");
				if (fullKey.length() <= MAX_KEY_LEN)
				{
					this.mClient.set(fullKey, aExpiration, aSerializableObj);
				}
			}
			catch (Throwable t)
			{
				logger.error("[MEMCACHE]", t);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public Object get(String aKey)
	{
		Object valObj = null;
		String fullKey = this.mNameSpace + "_" + aKey;
		fullKey.replace("/", "SsS");
		if (fullKey.length() <= MAX_KEY_LEN)
		{
			Future f = this.mClient.asyncGet(fullKey);
			try
			{
				valObj = f.get(5L, TimeUnit.SECONDS);
			}
			catch (Throwable t)
			{
				logger.warn("[MEMCACHE] get key = " + fullKey + " timeout within 5 seconds.");
				f.cancel(true);
			}
		}

		return valObj;
	}

	public void delete(String aKey)
	{
		String fullKey;
		try
		{
			fullKey = this.mNameSpace + "_" + aKey;
			fullKey.replace("/", "SsS");
			this.mClient.delete(fullKey);
		}
		catch (Throwable t)
		{
			logger.error("[MEMCACHE]", t);
		}
	}

	public void close()
	{
		try
		{
			this.mClient.shutdown();
		}
		catch (Throwable t)
		{
			logger.error("[MEMCACHE]", t);
		}
	}
}