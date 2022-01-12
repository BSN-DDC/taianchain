package com.reddate.ddc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;

import com.reddate.ddc.config.ConfigInfo;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.service.BaseService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigUtils {

	public static final String REST_TEMPLATE = "restTemplate";
	
	public static final String CONN_TIME_OUT = "conTimeout";
	
	public static final String READ_TIME_OUT = "readTimeout";
	
	public static final String OPB_GATE_WAY_ADDRESS = "opbGatewayAddress";
	
	public static final String CONTRACT = "contract";
	
	public static final String DDC_721_CONTRACT_ADDR = "ddc721Addr";
	
	public static final String DDC_1155_CONRACT_ADDR = "ddc1155Addr";
	
	public static final String AUTHORITY_LOGIC_ADDR = "authorityLogicAddr";
	
	public static final String CHARGE_LOGIC_ADDR = "chargeLogicAddr";
	
	public static final String DDC721_ABI_FILE = "contract/DDC721.abi";
	
	public static final String DDC721_BIN_FILE = "contract/DDC721.bin";
	
	public static final String DDC1155_ABI_FILE = "contract/DDC1155.abi";
	
	public static final String DDC1155_BIN_FILE = "contract/DDC1155.bin";
	
	public static final String AUTHORITY_ABI_FILE = "contract/AuthorityLogic.abi";
	
	public static final String AUTHORITY_BIN_FILE = "contract/AuthorityLogic.bin";
	
	public static final String CHARGE_ABI_FILE = "contract/ChargeLogic.abi";
	
	public static final String CHARGE_BIN_FILE = "contract/AuthorityLogic.bin";
	
	public static final String PRIVATE_KEY_FILE = "cert/privateKey.pem";
	
	public static final String PUBLIC_KEY_FILE = "cert/publicKey.pem";
	
	public static final String CONFIG_YAML_FILE = "sdk-config.yml";
	
	public static final String QUERY_RECEPIT_WAINT_TIME = "queryRecepitWaitTime";
	
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
		String ddc721Address = (String)contractMap.get(DDC_721_CONTRACT_ADDR);
		String ddc1155Address = (String)contractMap.get(DDC_1155_CONRACT_ADDR);
		String authorityLogicAddress = (String)contractMap.get(AUTHORITY_LOGIC_ADDR);
		String chargeLogicAddress = (String)contractMap.get(CHARGE_LOGIC_ADDR);
		Integer queryRecepitWaitTime = (Integer)contractMap.get(QUERY_RECEPIT_WAINT_TIME);
		
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
		
		if(isEmpty(ddc721Address)) {
			throw new DDCException(ErrorMessage.DDC_721_ADDRESS_EMPTY);
		}
		configInfo.setDdc721Address(ddc721Address);
		
		if(isEmpty(ddc1155Address)) {
			throw new DDCException(ErrorMessage.DDC_1155_ADDRESS_EMPTY);
		}
		configInfo.setDdc1155Address(ddc1155Address);
		
		if(isEmpty(authorityLogicAddress)) {
			throw new DDCException(ErrorMessage.DDC_AUTHORITY_ADDRESS_EMPTY);
		}
		configInfo.setAuthorityLogicAddress(authorityLogicAddress);
		
		if(isEmpty(chargeLogicAddress)) {
			throw new DDCException(ErrorMessage.DDC_CHARGE_ADDRESS_EMPTY);
		}
		configInfo.setChargeLogicAddress(chargeLogicAddress);
		
//		if(isEmpty(signMethod)) {
//			throw new DDCException(ErrorMessage.SIGN_METHOD_EMPTY);
//		}
//		configInfo.setSignMethod(signMethod);
		
		String ddc721abi = readFileContent(DDC721_ABI_FILE);
		configInfo.setDdc721ABI(ddc721abi);
		
		String ddc721bin = readFileContent(DDC721_BIN_FILE);
		configInfo.setDdc721BIN(ddc721bin);
		
		String ddc1155abi = readFileContent(DDC1155_ABI_FILE);
		configInfo.setDdc1155ABI(ddc1155abi);
		
		String ddc1155bin = readFileContent(DDC1155_BIN_FILE);
		configInfo.setDdc1155BIN(ddc1155bin);
		
		String authorityLogicABI =readFileContent(AUTHORITY_ABI_FILE);
		configInfo.setAuthorityLogicABI(authorityLogicABI);
		
		String authorityLogicBIN = readFileContent(AUTHORITY_BIN_FILE);
		configInfo.setAuthorityLogicBIN(authorityLogicBIN);
		
		String chargeLogicABI = readFileContent(CHARGE_ABI_FILE);
		configInfo.setChargeLogicABI(chargeLogicABI);
		
		String chargeLogicBIN = readFileContent(CHARGE_BIN_FILE);
		configInfo.setChargeLogicBIN(chargeLogicBIN);
		
//		if(configInfo.isSignMethodPrivateKey()) {
//			String privateKey = null;
//			try {
//				privateKey = readFileContent(PRIVATE_KEY_FILE);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			configInfo.setPrivateKey(privateKey);
//			
//			String publicKey = null;
//			try {
//				publicKey = readFileContent(PUBLIC_KEY_FILE);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			configInfo.setPublicKey(publicKey);
//		}
		
		return configInfo;
	}
	
	
	private static boolean isEmpty(String str) {
		return (str == null || str.trim().isEmpty());
	}
	
	
	public static String readFileContent(String fileName) {
		InputStream inputStream = getInputStream(fileName);
		byte[] bytes = new byte[1024*1024];
		try {
			int count = inputStream.read(bytes);
			return new String(bytes,0,count);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new DDCException(ErrorMessage.FILE_NOT_EXISTS);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			} 
		}
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
