package com.reddate.taianddc.dto.ddc;

import lombok.Data;

@Data
public class DeleteDDCEventBean extends BaseEventBean {
	
	/** 业务主合约 */
    String ddcAddr;
}
