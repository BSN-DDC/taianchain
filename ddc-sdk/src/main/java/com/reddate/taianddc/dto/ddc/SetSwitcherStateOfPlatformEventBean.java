package com.reddate.taianddc.dto.ddc;

import lombok.Data;

@Data
public class SetSwitcherStateOfPlatformEventBean extends BaseEventBean{
    /** 签名者 */
    String operator;
    /** 是否开通 */
    Boolean isOpen;
}
