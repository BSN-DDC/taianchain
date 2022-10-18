package com.reddate.taianddc.dto.ddc;

import lombok.Data;

@Data
public class CrossPlatformApprovalEventBean extends BaseEventBean {
    /** 授权账户 */
    String from;
    /** 授权账户 */
    String to;
    /** 授权账户 */
    Boolean approved;
}
