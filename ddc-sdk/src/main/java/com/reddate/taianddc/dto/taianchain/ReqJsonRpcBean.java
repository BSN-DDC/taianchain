package com.reddate.taianddc.dto.taianchain;

import java.util.List;

/**
 * @author kuan
 * Created on 21/1/12.
 * @description
 */
public class ReqJsonRpcBean {

    /**
     * jsonrpc : 2.0
     * method : getBlockNumber
     * params : [1]
     * id : 1
     */

    private String jsonrpc = "2.0";
    private String method;
    private int id = 1;
    private List<Object> params;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Object> getParams() {
        return params;
    }

    public void setParams(List<Object> params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "ReqJsonRpcBean{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", method='" + method + '\'' +
                ", id=" + id +
                ", params=" + params +
                '}';
    }
}
