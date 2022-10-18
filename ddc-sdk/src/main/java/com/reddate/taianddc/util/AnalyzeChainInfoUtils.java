package com.reddate.taianddc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.reddate.taianddc.dto.ddc.BaseEventBean;
import com.reddate.taianddc.dto.ddc.PayEventBean;
import org.fisco.bcos.web3j.crypto.EncryptType;
import org.fisco.bcos.web3j.crypto.gm.sm2.util.encoders.Hex;
import org.fisco.bcos.web3j.protocol.ObjectMapperFactory;
import org.fisco.bcos.web3j.protocol.core.methods.response.Log;
import org.fisco.bcos.web3j.protocol.exceptions.TransactionException;
import org.fisco.bcos.web3j.tx.txdecode.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @author kuan
 * Created on 21/12/11.
 * @description
 */
public class AnalyzeChainInfoUtils {
    // secp256k1 : 0     sm2 : 1
    public static EncryptType encryptType = new EncryptType(0);

    /**
     * 解析input
     *
     * @param abi   abi
     * @param bin   bin
     * @param input input
     * @return InputAndOutputResult
     * @throws BaseException        BaseException
     * @throws TransactionException TransactionException
     */
    public static InputAndOutputResult analyzeTransactionInput(String abi, String bin, String input) throws BaseException, TransactionException {
        TransactionDecoder txDecodeSampleDecoder = new TransactionDecoder(abi, bin);
        return txDecodeSampleDecoder.decodeInputReturnObject(input);
    }

    /**
     * 解析output
     *
     * @param abi    abi
     * @param bin    bin
     * @param input  input
     * @param output output
     * @return InputAndOutputResult
     * @throws BaseException        BaseException
     * @throws TransactionException TransactionException
     */
    public static InputAndOutputResult analyzeTransactionOutput(String abi, String bin, String input, String output) throws BaseException, TransactionException {
        TransactionDecoder txDecodeSampleDecoder = new TransactionDecoder(abi, bin);
        return txDecodeSampleDecoder.decodeOutputReturnObject(input, output);
    }

    /**
     * 解析事件
     *
     * @param abi      abi
     * @param bin      bin
     * @param eventLog eventLog
     * @return Map<String, List < List < EventResultEntity>>>
     * @throws BaseException BaseException
     * @throws IOException   IOException
     */
    public static Map<String, List<List<EventResultEntity>>> analyzeEventLog(String abi, String bin, String eventLog) throws BaseException, IOException {
        TransactionDecoder txDecodeSampleDecoder = new TransactionDecoder(abi, bin);
        ObjectMapper mapper = ObjectMapperFactory.getObjectMapper();
        CollectionType listType = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Log.class);
        List<Log> logList = (List) mapper.readValue(eventLog, listType);
        return txDecodeSampleDecoder.decodeEventReturnObject(logList);
    }

    public static <T extends BaseEventBean> T assembleBeanByReflect(List<EventResultEntity> eventResultEntityList, Class<T> clazz) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        T t = clazz.getConstructor().newInstance();
        Field[] fields = t.getClass().getDeclaredFields();

        for (Field field : fields) {
            EventResultEntity eventResultEntity = null;

            for (int i = 0; i < eventResultEntityList.size(); i++) {
                if (field.getName().equals(eventResultEntityList.get(i).getName())) {
                    eventResultEntity = eventResultEntityList.get(i);
                    //remove以减少遍历次数
                    eventResultEntityList.remove(i);
                }
            }

            if (null == eventResultEntity) {
                StringBuilder stringBuilder = new StringBuilder("AssembleBeanByReflect failed:Unknown type ");
                stringBuilder.append(field.getType());
                stringBuilder.append(" ");
                stringBuilder.append(field.getName());
                throw new IllegalAccessException(stringBuilder.toString());
            }

            field.setAccessible(true);
            if (t instanceof PayEventBean && field.getName().equals("sig")) {
                ((PayEventBean) t).setSig("0x" + Hex.toHexString((byte[]) eventResultEntity.getTypeObject().getValue()));
            } else {
                field.set(t, eventResultEntity.getData());
            }
        }

        return t;
    }

}
