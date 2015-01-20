package com.vns.memcached;

public class GlobalCache
{
	protected static MemcacheClientPool	cachedPool;

	public static final int				TIME_CACHE_2_MIN	= 120;
	public static final int				TIME_CACHE_5_MIN	= 300;
	public static final int				TIME_CACHE_10_MIN	= 600;
	public static final int				TIME_CACHE_15_MIN	= 900;
	public static final int				TIME_CACHE_30_MIN	= 1800;
	public static final int				TIME_CACHE_1_HOUR	= 3600;
	public static final int				TIME_CACHE_2_HOUR	= 7200;

	static
	{
		cachedPool = new MemcacheClientPool();
		cachedPool.start("localhost:9501", "dataservice");
	}

	public static Object getCache(String aKey)
	{
		return cachedPool.borrowClient().get(aKey);
	}

	public static void setCache(String aKey, int aExpiration, Object aSerializableObj)
	{
		cachedPool.borrowClient().set(aKey, aExpiration, aSerializableObj);
	}

	public static void deletCache(String aKey)
	{
		cachedPool.borrowClient().delete(aKey);
	}
}
