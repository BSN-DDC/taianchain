package com.reddate.taianddc;

import org.junit.jupiter.api.Test;

import com.reddate.taianddc.listener.Secp256K1SignEventListener;
import com.reddate.taianddc.listener.SignEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaianDDCSdkClientTestt {

	String a = "";

	 @Test
	public void sdkInitTest() throws Exception {
		 DDCSdkClient sdk = new DDCSdkClient("");
		 String privateKey = "";
		 String publicKey = "";
		 SignEventListener signEventListener =  new Secp256K1SignEventListener(privateKey, publicKey);
		 sdk.registerSignListener(signEventListener);
		 
	}

	@Test
	public void testAddAccountByOperator() {
		String input = "1,,";

		List<Object> arrList = new ArrayList(Arrays.asList(input.toString().split(",")));

		System.out.println(arrList.size());
		for (int i = 0; i < arrList.size(); i++) {
			System.out.println(i+ " : ["+arrList.get(i) + "]");
		}
	}
	
}
