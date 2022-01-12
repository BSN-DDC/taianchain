package com.reddate.ddc;

import org.junit.jupiter.api.Test;

import com.reddate.ddc.listener.Secp256K1SignEventListener;
import com.reddate.ddc.listener.SignEventListener;

public class DDCSdkClientTestt {

	 @Test
	public void sdkInitTest() throws Exception {
		 DDCSdkClient sdk = new DDCSdkClient();
		 sdk.init();
		 String privateKey = "";
		 String publicKey = "";
		 SignEventListener signEventListener =  new Secp256K1SignEventListener(privateKey, publicKey);
		 sdk.registerSignListener(signEventListener);
		 
	}
	
}
