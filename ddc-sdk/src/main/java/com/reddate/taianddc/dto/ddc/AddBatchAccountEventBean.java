package com.reddate.taianddc.dto.ddc;

import lombok.Data;

import java.util.ArrayList;

@Data
public class AddBatchAccountEventBean extends BaseEventBean{
    /** 签名者 **/
    private String caller;
    /** 链账户地址集合 **/
    private ArrayList<String> accounts;
}
