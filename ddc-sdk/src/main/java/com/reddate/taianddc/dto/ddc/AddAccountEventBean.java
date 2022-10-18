package com.reddate.taianddc.dto.ddc;

import lombok.Data;

@Data
public class AddAccountEventBean extends BaseEventBean {
	
	/** 签名者 */
    private String caller;
    
    /** 链账户地址 */
    private String account;

}
