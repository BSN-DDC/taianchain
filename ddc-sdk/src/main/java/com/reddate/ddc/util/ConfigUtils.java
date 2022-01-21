package com.reddate.ddc.util;

import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.config.ConfigInfo;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.exception.DDCException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

@Slf4j
public class ConfigUtils {

	public static final String REST_TEMPLATE = "restTemplate";
	
	public static final String CONN_TIME_OUT = "conTimeout";
	
	public static final String READ_TIME_OUT = "readTimeout";
	
	public static final String OPB_GATE_WAY_ADDRESS = "opbGatewayAddress";
	
	public static final String CONTRACT = "contract";
	
	public static final String CONFIG_YAML_FILE = "sdk-config.yml";
	
	public static final String QUERY_RECEPIT_WAINT_TIME = "queryRecepitWaitTime";
	
	public static final String QUERY_RECEPIT_RETRY_COUNT = "queryRecepitRetryCount";

	/**
	 * 解析默认位置的位置文件，并验证配置项，组装成配置对象
	 * 
	 * @return 返回配置对象
	 */
	public static ConfigInfo loadConfigFromFile() {
		InputStream inputStream = getInputStream(CONFIG_YAML_FILE);
		Yaml yaml = new Yaml();
		Map<String, Object> configMap = (Map<String, Object>) yaml.load(inputStream);
		if(inputStream != null) {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map<String, Object> restTemplateMap = (Map<String, Object>)configMap.get(REST_TEMPLATE);
		Integer conTimeout = (Integer)restTemplateMap.get(CONN_TIME_OUT);
		Integer readTimeout = (Integer)restTemplateMap.get(READ_TIME_OUT);
		String opbGatewayAddress = (String)restTemplateMap.get(OPB_GATE_WAY_ADDRESS);
		
		Map<String, Object> contractMap = (Map<String, Object>)configMap.get(CONTRACT);

		Integer queryRecepitWaitTime = (Integer)contractMap.get(QUERY_RECEPIT_WAINT_TIME);
		Integer queryRecepitRetryCount = (Integer)contractMap.get(QUERY_RECEPIT_RETRY_COUNT);
		
		ConfigInfo configInfo = new ConfigInfo();
		
		if(conTimeout == null) {
			throw new DDCException(ErrorMessage.REST_TEMPLATE_CONNT_TIMEOUT_EMPTY);
		}
		configInfo.setConTimeout(conTimeout);
		
		if(readTimeout == null) {
			throw new DDCException(ErrorMessage.REST_TEMPLATE_READ_TIMEOUT_EMPTY);
		}
		configInfo.setReadTimeout(readTimeout);
		
		if(isEmpty(opbGatewayAddress)) {
			throw new DDCException(ErrorMessage.OPB_GATEWAY_ADDRESS_EMPTY);
		}
		configInfo.setOpbGatewayAddress(opbGatewayAddress);
		
		if(queryRecepitWaitTime != null) {
			configInfo.setQueryRecepitWaitTime(new Long(queryRecepitWaitTime.intValue()));
		}
		
		if(queryRecepitRetryCount != null) {
			configInfo.setQueryRecepitRetryCount(queryRecepitRetryCount);
		}
		
		if(isEmpty(ConfigCache.DDC_721_ADDRESS)) {
			throw new DDCException(ErrorMessage.DDC_721_ADDRESS_EMPTY);
		}
		configInfo.setDdc721Address(ConfigCache.DDC_721_ADDRESS);
		
		if(isEmpty(ConfigCache.DDC_1155_ADDRESS)) {
			throw new DDCException(ErrorMessage.DDC_1155_ADDRESS_EMPTY);
		}
		configInfo.setDdc1155Address(ConfigCache.DDC_1155_ADDRESS);
		
		if(isEmpty(ConfigCache.AUTHORITY_ADDRESS)) {
			throw new DDCException(ErrorMessage.DDC_AUTHORITY_ADDRESS_EMPTY);
		}
		configInfo.setAuthorityLogicAddress(ConfigCache.AUTHORITY_ADDRESS);
		
		if(isEmpty(ConfigCache.CHARGE_ADDRESS)) {
			throw new DDCException(ErrorMessage.DDC_CHARGE_ADDRESS_EMPTY);
		}
		configInfo.setChargeLogicAddress(ConfigCache.CHARGE_ADDRESS);
		
		configInfo.setDdc721ABI(ConfigCache.DDC_721_ABI);
		
		configInfo.setDdc721BIN(ConfigCache.DDC_721_BIN);
		
		configInfo.setDdc1155ABI(ConfigCache.DDC_1155_ABI);
		
		configInfo.setDdc1155BIN(ConfigCache.DDC_1155_BIN);
		
		configInfo.setAuthorityLogicABI(ConfigCache.AUTHORITY_ABI);
		configInfo.setAuthorityLogicBIN(ConfigCache.AUTHORITY_BIN);
		
		configInfo.setChargeLogicABI(ConfigCache.CHARGE_ABI);
		configInfo.setChargeLogicBIN(ConfigCache.CHARGE_BIN);

		return configInfo;
	}
	
	
	private static boolean isEmpty(String str) {
		return (str == null || str.trim().isEmpty());
	}
	
	public static InputStream getInputStream(String fileName) {
		InputStream inputStream = null;
		try {
			log.info("try load config file {} from current directory",fileName);
			inputStream = new FileInputStream(fileName);
		} catch (FileNotFoundException e1) {
			//e1.printStackTrace();
		}
		try {
			if(inputStream == null) {
				log.info("try load config file {} from ClassPath",fileName);
				inputStream = ConfigUtils.class.getClassLoader().getResourceAsStream(fileName);
			}
			if(inputStream == null) {
				log.info("try load config file {} from URI",fileName);
				File configYamlFile = ResourceUtils.getFile(fileName);
				inputStream = new FileInputStream(configYamlFile);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new DDCException(ErrorMessage.FILE_NOT_EXISTS);
		}
		
		return inputStream;
	}
	
}
