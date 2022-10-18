package com.reddate.taianddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class DDC1155SetURIEventBean extends BaseEventBean{
    /** DDC唯一标识 */
    private BigInteger ddcId;
    /** DDC资源标识符 */
    private String ddcURI;
}
