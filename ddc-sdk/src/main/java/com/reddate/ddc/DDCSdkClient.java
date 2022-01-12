package com.reddate.ddc;


import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.config.ConfigInfo;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.service.AuthorityService;
import com.reddate.ddc.service.ChargeService;
import com.reddate.ddc.service.DDC1155Service;
import com.reddate.ddc.service.DDC721Service;
import com.reddate.ddc.util.ConfigUtils;

public class DDCSdkClient {

	private SignEventListener signEventListener;
	
	/**
	 * SDK 初始化方法，在此方法中解析SDK的配置文件，并且放到程序缓存中。
	 * 
	 * 
	 */
	public void init(){
		ConfigInfo configInfo = ConfigUtils.loadConfigFromFile();
		ConfigCache.initCache(configInfo);
	}
	
	/**
	 * SDK注册全局的签名事件，所有发起的交易将通过此事件进行签名处理
	 * 
	 * 
	 * @param signEventListener 签名事件
	 */
	public void registerSignListener(SignEventListener signEventListener) {
		this.signEventListener = signEventListener;
	}
	
	/**
	 * 获取权限管理服务的示例
	 * 
	 * @return 返回权限管理服务的实例
	 */
	public AuthorityService getAuthorityService() {
		return new AuthorityService(signEventListener);
	}
	
	/**
	 * 获取费用管理服务的实例
	 * 
	 * @return 返回费用管理服务的实例
	 */
	public ChargeService getChargeService() {
		return new ChargeService(signEventListener);
	}
	
	/**
	 * 获取BSN-DDC-1155合约服务的实例
	 * 
	 * @return 返回BSN-DDC-1155合约服务的实例
	 */
	public DDC1155Service getDDC1155Service() {
		return new DDC1155Service(signEventListener);
	}
	
	/**
	 * 获取BSN-DDC-721合约服务的实例
	 * 
	 * @return 返回BSN-DDC-721合约服务的实例
	 */
	public DDC721Service getDDC721Service() {
		return new DDC721Service(signEventListener);
	}
	
}
