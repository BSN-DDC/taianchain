package com.reddate.taianddc.service;

import com.alibaba.fastjson.JSONObject;
import com.reddate.taianddc.config.ConfigCache;
import com.reddate.taianddc.constant.*;
import com.reddate.taianddc.dto.ddc.*;
import com.reddate.taianddc.dto.taianchain.BlockInfoBean;
import com.reddate.taianddc.dto.taianchain.TransactionInfoBean;
import com.reddate.taianddc.dto.taianchain.TransactionRecepitBean;
import com.reddate.taianddc.util.AnalyzeChainInfoUtils;
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

import static com.reddate.taianddc.util.AnalyzeChainInfoUtils.assembleBeanByReflect;

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
        // 进行事件方法与实体类的绑定 key为 address+方法名称用来保证唯一
        eventBeanMap.put(ConfigCache.get().getAuthorityLogicAddress() + AuthorityFunctions.SET_SWITCHER_STATE_OF_PLATFORM_EVENT, SetSwitcherStateOfPlatformEventBean.class);
        eventBeanMap.put(ConfigCache.get().getAuthorityLogicAddress() + AuthorityFunctions.ADD_ACCOUNT_EVENT, AddAccountEventBean.class);
        eventBeanMap.put(ConfigCache.get().getAuthorityLogicAddress() + AuthorityFunctions.ADD_BATCH_ACCOUNT_EVENT, AddBatchAccountEventBean.class);
        eventBeanMap.put(ConfigCache.get().getAuthorityLogicAddress() + AuthorityFunctions.UPDATE_ACCOUNT_STATE_EVENT, UpdateAccountStateEventBean.class);
        eventBeanMap.put(ConfigCache.get().getAuthorityLogicAddress() + AuthorityFunctions.CROSS_PLATFORM_APPROVAL_EVENT, CrossPlatformApprovalEventBean.class);
        eventBeanMap.put(ConfigCache.get().getAuthorityLogicAddress() + AuthorityFunctions.SYNC_PLATFORM_DID_EVENT, SyncPlatformDIDEventBean.class);

        eventBeanMap.put(ConfigCache.get().getChargeLogicAddress() + ChargeFunctions.RECHARGE_EVENT, ReChargeEventBean.class);
        eventBeanMap.put(ConfigCache.get().getChargeLogicAddress() + ChargeFunctions.RECHARGE_BATCH_EVENT, ReChargeBatchEventBean.class);
        eventBeanMap.put(ConfigCache.get().getChargeLogicAddress() + ChargeFunctions.PAY_EVENT, PayEventBean.class);
        eventBeanMap.put(ConfigCache.get().getChargeLogicAddress() + ChargeFunctions.SET_FEE_EVENT, SetFeeEventBean.class);
        eventBeanMap.put(ConfigCache.get().getChargeLogicAddress() + ChargeFunctions.DEL_FEE_EVENT, DeleteFeeEventBean.class);
        eventBeanMap.put(ConfigCache.get().getChargeLogicAddress() + ChargeFunctions.DEL_DDC_EVENT, DeleteDDCEventBean.class);

        eventBeanMap.put(ConfigCache.get().getDdc721Address() + DDC721Functions.DDC_721_TRANSFER_EVENT, DDC721TransferEventBean.class);
        eventBeanMap.put(ConfigCache.get().getDdc721Address() + DDC721Functions.DDC_721_TRANSFER_BATCH_EVENT, DDC721TransferBatchEventBean.class);
        eventBeanMap.put(ConfigCache.get().getDdc721Address() + DDC721Functions.DDC_721_FREEZE_EVENT, DDC721FreezeEventBean.class);
        eventBeanMap.put(ConfigCache.get().getDdc721Address() + DDC721Functions.DDC_721_UN_FREEZE_EVENT, DDC721UnFreezeEventBean.class);
        eventBeanMap.put(ConfigCache.get().getDdc721Address() + DDC721Functions.DDC_721_SET_URI_EVENT, DDC721SetURIEventBean.class);
        eventBeanMap.put(ConfigCache.get().getDdc721Address() + DDC721Functions.DDC_721_META_TRANSFER_EVENT, DDC721TransferEventBean.class);
        eventBeanMap.put(ConfigCache.get().getDdc721Address() + DDC721Functions.DDC_721_META_TRANSFER_BATCH_EVENT, DDC721TransferBatchEventBean.class);

        eventBeanMap.put(ConfigCache.get().getDdc1155Address() + DDC1155Functions.DDC_1155_TRANSFER_SINGLE_EVENT, DDC1155TransferSingleEventBean.class);
        eventBeanMap.put(ConfigCache.get().getDdc1155Address() + DDC1155Functions.DDC_1155_TRANSFER_BATCH_EVENT, DDC1155TransferBatchEventBean.class);
        eventBeanMap.put(ConfigCache.get().getDdc1155Address() + DDC1155Functions.DDC_1155_FREEZE_EVENT, DDC1155FreezeEventBean.class);
        eventBeanMap.put(ConfigCache.get().getDdc1155Address() + DDC1155Functions.DDC_1155_UN_FREEZE_EVENT, DDC1155UnFreezeEventBean.class);
        eventBeanMap.put(ConfigCache.get().getDdc1155Address() + DDC1155Functions.DDC_1155_SET_URI_EVENT, DDC1155SetURIEventBean.class);
        eventBeanMap.put(ConfigCache.get().getDdc1155Address() + DDC1155Functions.DDC_1155_META_TRANSFER_SINGLE_EVENT, DDC1155TransferSingleEventBean.class);
        eventBeanMap.put(ConfigCache.get().getDdc1155Address() + DDC1155Functions.DDC_1155_META_TRANSFER_BATCH_EVENT, DDC1155TransferBatchEventBean.class);

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
    public <T extends BaseEventBean> ArrayList<T> getBlockEvent(String blockNumber) throws BaseException, IOException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        ArrayList<T> arrayList = new ArrayList<>();
        // 1. 获取区块信息
        BlockInfoBean blockInfoBean = getBlockInfo(blockNumber);

        // 2. 获取交易
        for (int i = 0; i < blockInfoBean.getTransactions().size(); i++) {

            TransactionInfoBean transaction = blockInfoBean.getTransactions().get(i);
            ArrayList<T> transactionArrayList = analyzeEventsByTransaction(transaction, blockInfoBean);
            arrayList.addAll(transactionArrayList);
        }
        log.info("块高 {} 解析到区块事件 {}", blockNumber, JSONObject.toJSONString(arrayList));
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
    private <T extends BaseEventBean> ArrayList<T> analyzeEventsByTransaction(TransactionInfoBean transaction, BlockInfoBean blockInfoBean) throws BaseException, IOException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        ArrayList<T> arrayList = new ArrayList<>();
        // 获取交易回执
        TransactionRecepitBean transactionRecepitBean = getTransactionReceipt(transaction.getHash());

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
            arrayList.addAll(analyzeEventsByLog(log.getAddress().toLowerCase(), abi, bin, transaction, blockInfoBean, logs));
        }

        return arrayList;
    }

    /**
     * 通过Log解析事件
     *
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
    private <T extends BaseEventBean> ArrayList<T> analyzeEventsByLog(String logAddress, String abi, String bin, TransactionInfoBean transaction, BlockInfoBean blockInfoBean, ArrayList<Log> logs) throws BaseException, IOException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        ArrayList<T> arrayList = new ArrayList<>();
        // 解析交易回执中的事件
        Map<String, List<List<EventResultEntity>>> map = AnalyzeChainInfoUtils.analyzeEventLog(abi, bin, JSONObject.toJSONString(logs));

        // 将回执事件转换为对象
        for (Map.Entry<String, Class> entry : eventBeanMap.entrySet()) {
            String keyWithAddress = entry.getKey();
            // 合约地址
            String mapAddress = keyWithAddress.substring(0, logAddress.length());
            // 合约方法
            String mapFunction = keyWithAddress.substring(logAddress.length());
            if (!(map.containsKey(mapFunction) && logAddress.equals(mapAddress.toLowerCase()))) {
                continue;
            }

            List<List<EventResultEntity>> eventLists = map.get(mapFunction);
            for (List<EventResultEntity> eventList : eventLists) {
                T eventBean = (T) assembleBeanByReflect(eventList, entry.getValue());
                eventBean.setBlockHash(blockInfoBean.getHash());
                eventBean.setTransactionInfoBean(transaction);
                eventBean.setBlockNumber(blockInfoBean.getNumber());
                eventBean.setTimestamp(blockInfoBean.getTimestamp());
                arrayList.add(eventBean);

            }
        }
        return arrayList;
    }

}
