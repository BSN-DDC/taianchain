package com.reddate.ddc.service;

import com.reddate.ddc.DDCSdkClient;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.web3j.tx.txdecode.BaseException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

@Slf4j
class BlockEventServiceTest extends BaseServiceTest{

    BlockEventService blockEventService = new BlockEventService();

    @Test
    void getBlockEvent() throws BaseException, IOException, InterruptedException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        ArrayList<Object> result = new ArrayList<>();
        for (int i = 1466705; i < 1466706; i++) {
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