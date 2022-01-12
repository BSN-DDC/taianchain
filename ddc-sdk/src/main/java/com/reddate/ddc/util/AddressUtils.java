package com.reddate.ddc.util;

import org.fisco.bcos.web3j.crypto.WalletUtils;

public class AddressUtils {

	/**
	 * 验证传入参数是否为有效的区块链账户地址格式
	 * 
	 * @param address 地址
	 * @return 返回验证结果，true或者false。
	 */
	public static boolean isValidAddress(String address) {
		return WalletUtils.isValidAddress(address);
	}
	
}
