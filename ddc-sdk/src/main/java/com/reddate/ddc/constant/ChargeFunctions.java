package com.reddate.ddc.constant;

public class ChargeFunctions {
	public static final String Recharge = "recharge";
	
	public static final String BalanceOf = "balanceOf";
	
	public static final String QueryFee = "queryFee";
    
	public static final String SelfRecharge = "selfRecharge";
	
	public static final String SetFee = "setFee";
	
	public static final String DelFee = "delFee";
	
	public static final String DelDDC = "delDDC";


	public static final String RechargeEvent = "Recharge(address,address,uint256)";
	public static final String PayEvent = "Pay(address,address,bytes4,uint32,uint256)";
	public static final String SetFeeEvent = "SetFee(address,byte4,uint)";
	public static final String DelFeeEvent = "DelFee(address,bytes4)";
	public static final String DelDDCEvent = "DelDDC(address)";
}
