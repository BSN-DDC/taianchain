package com.reddate.ddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class AddAccountEventBean extends BaseEventBean {
	
	/** 签名者 */
    private String caller;
    
    /** 链账户地址 */
    private String account;

}
