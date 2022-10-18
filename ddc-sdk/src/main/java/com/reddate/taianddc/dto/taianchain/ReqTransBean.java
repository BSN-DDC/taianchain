package com.reddate.taianddc.dto.taianchain;

import java.math.BigInteger;
import java.util.List;

/**
 * @author kuan
 * Created on 21/1/11.
 * @description
 */
public class ReqTransBean {
    private String contractAbi;
    private String contractBin;
    private String contractAddress;
    private String funcName;
    private List<Object> funcParam;
    private BigInteger blockNumber;
    private int groupId;

    public String getContractAbi() {
        return contractAbi;
    }

    public void setContractAbi(String contractAbi) {
        this.contractAbi = contractAbi;
    }

    public String getContractBin() {
        return contractBin;
    }

    public void setContractBin(String contractBin) {
        this.contractBin = contractBin;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }
    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public List<Object> getFuncParam() {
        return funcParam;
    }

    public void setFuncParam(List<Object> funcParam) {
        this.funcParam = funcParam;
    }

    public BigInteger getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        this.blockNumber = blockNumber;
    }


    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "ReqTransBean{" +
                "contractAbi='" + contractAbi + '\'' +
                ", contractBin='" + contractBin + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", funcName='" + funcName + '\'' +
                ", funcParam=" + funcParam +
                ", blockNumber=" + blockNumber +
                ", groupId=" + groupId +
                '}';
    }
}
