package com.reddate.taianddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class DDC721SetURIEventBean extends BaseEventBean{
    /** 操作者 */
    private String operator;
    /** DDC唯一标识 */
    private BigInteger ddcId;
    /** DDC资源标识符 */
    private String ddcURI;
}
