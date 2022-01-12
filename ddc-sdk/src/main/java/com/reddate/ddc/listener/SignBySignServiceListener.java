package com.reddate.ddc.listener;

import com.alibaba.fastjson.JSON;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.taianchain.ReqJsonRpcBean;
import com.reddate.ddc.dto.taianchain.RespJsonRpcBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.util.crypto.Secp256K1Handle;
import com.reddate.ddc.util.http.RestTemplateUtil;
import org.fisco.bcos.web3j.crypto.*;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.encoders.Hex;
import org.fisco.bcos.web3j.utils.Numeric;

import java.util.ArrayList;
import java.util.Collections;

public class SignBySignServiceListener implements SignEventListener {
    private String host;
    private RestTemplateUtil restTemplateUtil;

    public SignBySignServiceListener(RestTemplateUtil restTemplateUtil, String host) throws Exception {
        this.restTemplateUtil = restTemplateUtil;
        this.host = host;
    }

    @Override
    public String signEvent(SignEvent event) {
        ReqJsonRpcBean reqJsonRpcBean = new ReqJsonRpcBean();
        reqJsonRpcBean.setMethod("fisco_sign");
        reqJsonRpcBean.setJsonrpc("2.0");
        reqJsonRpcBean.setId(1);
        reqJsonRpcBean.setParams(Collections.singletonList(Hash.sha3(event.getEncodeTransaction())));
        RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(host, reqJsonRpcBean, RespJsonRpcBean.class);
        if (respJsonRpcBean.getError() != null) {
            throw new DDCException(ErrorMessage.SIG_IS_EMPTY);
        }
        ArrayList<String> result = (ArrayList<String>) respJsonRpcBean.getResult();
        byte[] r = hexToByte(result.get(0).replace("0x",""));
        byte[] s = hexToByte(result.get(1).replace("0x",""));
        byte v = hexToByte(result.get(2).replace("0x",""))[0];
        Sign.SignatureData signatureData = new Sign.SignatureData(v, r, s);
        ExtendedRawTransaction extendedRawTransaction = ExtendedTransactionDecoder.decode(event.getEncodeTransaction());
        byte[] signedMessage = ExtendedTransactionEncoder.encode(extendedRawTransaction, signatureData);
        return Numeric.toHexString(signedMessage);
    }

    /**
     * hex转byte数组
     * @param hex
     * @return
     */
    public static byte[] hexToByte(String hex){
        int m = 0, n = 0;
        int byteLen = hex.length() / 2; // 每两个字符描述一个字节
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = Byte.valueOf((byte)intVal);
        }
        return ret;
    }
}
