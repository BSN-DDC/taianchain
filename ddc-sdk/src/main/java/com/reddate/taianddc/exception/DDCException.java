package com.reddate.taianddc.exception;


import com.alibaba.fastjson.JSONObject;
import com.reddate.taianddc.constant.ErrorMessage;
import com.reddate.taianddc.dto.taianchain.RespJsonRpcBean;

public class DDCException extends RuntimeException{
    private int code;
    private String msg;

    public DDCException(ErrorMessage errorMessage) {
    	super(errorMessage.getMessage());
        this.code = errorMessage.getCode();
        this.msg = errorMessage.getMessage();
    }

    public DDCException(ErrorMessage errorMessage, RespJsonRpcBean.ErrorBean appendError) {
    	super(JSONObject.toJSONString(appendError));
        this.code = errorMessage.getCode();
        //this.msg = JSONObject.toJSONString(appendError);
        this.msg = super.getMessage();
    }

    public DDCException(int code, String msg){
    	super(msg);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
