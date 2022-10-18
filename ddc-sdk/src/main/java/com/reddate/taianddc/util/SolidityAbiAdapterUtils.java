package com.reddate.taianddc.util;

/**
 * @author kuan
 */
public class SolidityAbiAdapterUtils {

    /**
     * Convert solidityversion abi from 0.8 to version 0.4
     * @param abi
     * @return
     */
    public static String abiAdapter(String abi) {
        String lowLevelViewFunctionKeyWord = "\"constant\":true,";
        String highLevelViewFunctionKeyWord = "\"stateMutability\":\"view\",";
        String result = abi.replace(highLevelViewFunctionKeyWord,highLevelViewFunctionKeyWord+lowLevelViewFunctionKeyWord);
        return result;

    }

}
