package com.reddate.ddc.config;


import java.util.concurrent.ConcurrentHashMap;

public class ConfigCache {

	private static final String DDC_SDK_CACHE_KEY = "ddc_sdk_config";
	
	private static final ConcurrentHashMap<String,ConfigInfo> MAP = new ConcurrentHashMap<>();
	
	public static void initCache(ConfigInfo configInfo) {
		MAP.put(DDC_SDK_CACHE_KEY, configInfo);
	}
	
	public static final ConfigInfo get() {
		return MAP.get(DDC_SDK_CACHE_KEY);
	}
	
}
