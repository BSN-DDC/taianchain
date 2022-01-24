package com.reddate.ddc;


import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.service.AuthorityService;
import com.reddate.ddc.service.ChargeService;
import com.reddate.ddc.service.DDC1155Service;
import com.reddate.ddc.service.DDC721Service;

public class DDCSdkClient {

	// 请求网关地址
	public DDCSdkClient(String gatewayUrl) {
		ConfigCache.setGatewayUrl(gatewayUrl);
	}


	private SignEventListener signEventListener;
	
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
