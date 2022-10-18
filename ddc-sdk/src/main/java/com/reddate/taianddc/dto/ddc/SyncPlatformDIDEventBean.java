package com.reddate.taianddc.dto.ddc;

import lombok.Data;

import java.util.ArrayList;

@Data
public class SyncPlatformDIDEventBean extends BaseEventBean {
    /** 签名者 */
    String operator;
    /** 平台方DID集合 */
    ArrayList<String> dids;
}
