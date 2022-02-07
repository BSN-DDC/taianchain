package com.reddate.ddc.dto.taianchain;

import lombok.Data;

@Data
public class ReqCallRpcBean {
    String from;
    String to;
    String value;
    String data;
}
