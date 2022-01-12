package com.reddate.ddc.dto.ddc;

import lombok.Data;
import java.math.BigInteger;

@Data
public class DDC1155FreezeEventBean extends BaseEventBean {
	
	/** 签名者 */
    String sender;
    
    /** DDCID */
    BigInteger ddcId;
}
