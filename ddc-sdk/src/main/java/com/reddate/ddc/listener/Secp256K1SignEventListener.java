package com.reddate.ddc.listener;

import org.fisco.bcos.web3j.tx.TransactionAssembleManager;

import com.reddate.ddc.util.crypto.Secp256K1Handle;


/**
 * 签名事件的Secp256K1算法实现
 * 
 * @author 
 *
 */
public class Secp256K1SignEventListener implements SignEventListener {

	private Secp256K1Handle secp256K1Handle;
	
	public Secp256K1SignEventListener(String privateKey, String publicKey) throws Exception {
		secp256K1Handle = new Secp256K1Handle(privateKey, publicKey);
	}
	
	/**
	 * 签名事件的Secp256K1算法处理实现
	 * 
	 * 
	 */
	@Override
	public String signEvent(SignEvent event) {
		String signedStr = TransactionAssembleManager.signMessageByEncryptType(event.getEncodeTransaction(), secp256K1Handle.getKeyPair(), secp256K1Handle.getEncryptType());
		
		return signedStr;
	}

	
	
	
}
