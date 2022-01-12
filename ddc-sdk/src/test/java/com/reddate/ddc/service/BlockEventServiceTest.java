package com.reddate.ddc.service;

import com.alibaba.fastjson.JSON;
import com.reddate.ddc.DDCSdkClient;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class BlockEventServiceTest {
    static {
        new DDCSdkClient().init();
    }

    BlockEventService blockEventService = new BlockEventService();

    @Test
    void getBlockEvent() throws BaseException, IOException, InterruptedException {
        ArrayList<Object> result = new ArrayList<>();
        for (int i = 99743; i < 99744; i++) {
            result.addAll(blockEventService.getBlockEvent(String.valueOf(i)));
        }
        log.info("");
        log.info("");
        log.info("");

        result.forEach( t -> {
            log.info("{}:{}",t.getClass(),t);
        });

    }
}