package com.reddate.ddc.dto.ddc;

import lombok.Data;

import java.math.BigInteger;

@Data
public class DDC1155SetURIEventBean extends BaseEventBean{
    private BigInteger ddcId;
    private String ddcURI;
}
