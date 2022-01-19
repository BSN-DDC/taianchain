package com.reddate.ddc.service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.constant.*;
import com.reddate.ddc.dto.ddc.*;
import com.reddate.ddc.dto.taianchain.BlockInfoBean;
import com.reddate.ddc.dto.taianchain.TransactionInfoBean;
import com.reddate.ddc.dto.taianchain.TransactionRecepitBean;
import com.reddate.ddc.util.AnalyzeChainInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.protocol.core.methods.response.Log;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.fisco.bcos.web3j.tx.txdecode.EventResultEntity;
import org.fisco.bcos.web3j.utils.Strings;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.reddate.ddc.util.AnalyzeChainInfoUtils.assembleBeanByReflect;

/**
 * @author kuan
 * Created on 21/12/11.
 * @description
 */
@Slf4j
public class BlockEventService extends BaseService {
    // 外部可通过修改hashMap内的属性增加或删除需要解析的事件
    public HashMap<String, Class> eventBeanMap = new HashMap<>();

    public BlockEventService() {
        // 进行事件方法与实体类的绑定
        eventBeanMap.put(AuthorityFunctions.AddAccountEvent, AddAccountEventBean.class);
        eventBeanMap.put(AuthorityFunctions.UpdateAccountStateEvent, UpdateAccountStateEventBean.class);

        eventBeanMap.put(ChargeFunctions.RechargeEvent,ReChargeEventBean.class);
        eventBeanMap.put(ChargeFunctions.PayEvent, PayEventBean.class);
        eventBeanMap.put(ChargeFunctions.SetFeeEvent, SetFeeEventBean.class);
        eventBeanMap.put(ChargeFunctions.DelFeeEvent, DeleteFeeEventBean.class);
        eventBeanMap.put(ChargeFunctions.DelDDCEvent, DeleteDDCEventBean.class);

        eventBeanMap.put(DDC721Functions.DDC721TransferEvent, DDC721TransferEventBean.class);
        eventBeanMap.put(DDC721Functions.DDC721FreezeEvent, DDC721FreezeEventBean.class);
        eventBeanMap.put(DDC721Functions.DDC721UnFreezeEvent, DDC721UnFreezeEventBean.class);

        eventBeanMap.put(DDC1155Functions.DDC1155TransferSingleEvent, DDC1155TransferSingleEventBean.class);
        eventBeanMap.put(DDC1155Functions.DDC1155TransferBatchEvent, DDC1155TransferBatchEventBean.class);
        eventBeanMap.put(DDC1155Functions.DDC1155FreezeEvent, DDC1155FreezeEventBean.class);
        eventBeanMap.put(DDC1155Functions.DDC1155UnFreezeEvent, DDC1155UnFreezeEventBean.class);
    }

    /**
     * 获取区块事件并解析
     * 1. 根据块高获取区块信息
     * 2. 根据块中交易获取交易回执
     * 3. 遍历交易回执中的事件并解析
     *
     * @param blockNumber blockNumber
     * @return ArrayList<Object>
     * @throws BaseException BaseException
     * @throws IOException   IOException
     */
    public <T extends BaseEventBean> ArrayList<T> getBlockEvent(String blockNumber) throws BaseException, IOException, InterruptedException {
        ArrayList<T> arrayList = new ArrayList<>();
        // 1. 获取区块信息
        BlockInfoBean blockInfoBean = getBlockInfo(blockNumber);

        // 2. 获取交易
        for (int i = 0; i < blockInfoBean.getTransactions().size(); i++) {

            TransactionInfoBean transaction = blockInfoBean.getTransactions().get(i);
            ArrayList<T> transactionArrayList = analyzeEventsByTransaction(transaction,blockInfoBean);
            arrayList.addAll(transactionArrayList);
        }
        log.info("块高 {} 解析到区块事件 {}",blockNumber, JSONObject.toJSONString(arrayList));
        return arrayList;
    }

    /**
     * 根据交易进行事件解析
     *
     * @param transaction transaction
     * @return ArrayList<Object>
     * @throws BaseException BaseException
     * @throws IOException   IOException
     */
    private <T extends BaseEventBean> ArrayList<T> analyzeEventsByTransaction(TransactionInfoBean transaction, BlockInfoBean blockInfoBean) throws BaseException, IOException, InterruptedException {
        ArrayList<T> arrayList = new ArrayList<>();
        // 获取交易回执
        TransactionRecepitBean transactionRecepitBean = getTransactionRecepit(transaction.getHash());

        for (int i = 0; i < transactionRecepitBean.getLogs().size(); i++) {

            // 根据交易日志匹配需要解析的信息
            Log log = transactionRecepitBean.getLogs().get(i);

            if (Strings.isEmpty(log.getAddress())) {
                continue;
            }

            String abi = "";
            String bin = "";

            if (log.getAddress().toLowerCase().equals(ConfigCache.get().getAuthorityLogicAddress().toLowerCase())) {
                abi = ConfigCache.get().getAuthorityLogicABI();
                bin = ConfigCache.get().getAuthorityLogicBIN();
            } else if (log.getAddress().toLowerCase().equals(ConfigCache.get().getChargeLogicAddress().toLowerCase())) {
                abi = ConfigCache.get().getChargeLogicABI();
                bin = ConfigCache.get().getChargeLogicBIN();
            } else if (log.getAddress().toLowerCase().equals(ConfigCache.get().getDdc721Address().toLowerCase())) {
                abi = ConfigCache.get().getDdc721ABI();
                bin = ConfigCache.get().getDdc721BIN();
            } else if (log.getAddress().toLowerCase().equals(ConfigCache.get().getDdc1155Address().toLowerCase())) {
                abi = ConfigCache.get().getDdc1155ABI();
                bin = ConfigCache.get().getDdc1155BIN();
            } else {
                // 其他合约不解析
                continue;
            }
            ArrayList<Log> logs = new ArrayList<>();
            logs.add(log);
            arrayList.addAll(analyzeEventsByLog(abi,bin,transaction,blockInfoBean,logs));
        }

        return arrayList;
    }

    /**
     * 通过Log解析事件
     * @param abi
     * @param bin
     * @param transaction
     * @param blockInfoBean
     * @param logs
     * @param <T>
     * @return
     * @throws BaseException
     * @throws IOException
     */
    private <T extends BaseEventBean> ArrayList<T> analyzeEventsByLog(String abi, String bin, TransactionInfoBean transaction, BlockInfoBean blockInfoBean, ArrayList<Log> logs) throws BaseException, IOException {
        ArrayList<T> arrayList = new ArrayList<>();
        // 解析交易回执中的事件
        Map<String, List<List<EventResultEntity>>> map = AnalyzeChainInfoUtils.analyzeEventLog(abi, bin, JSONObject.toJSONString(logs));

        // 将回执事件转换为对象
        for (Map.Entry<String, Class> entry : eventBeanMap.entrySet()) {
            if (!map.containsKey(entry.getKey())) {
                continue;
            }

            List<List<EventResultEntity>> eventLists = map.get(entry.getKey());
            for (List<EventResultEntity> eventList : eventLists) {
                try {
                    T eventBean = (T) assembleBeanByReflect(eventList, entry.getValue());
                    eventBean.setBlockHash(blockInfoBean.getHash());
                    eventBean.setTransactionInfoBean(transaction);
                    eventBean.setBlockNumber(blockInfoBean.getNumber());
                    eventBean.setTimestamp(blockInfoBean.getTimestamp());
                    arrayList.add(eventBean);
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | InstantiationException e) {
                    e.printStackTrace();
//                    try {
//                        throw e;
//                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException ex) {
//                        ex.printStackTrace();
//                    }
                }
            }
        }
        return arrayList;
    }

}
