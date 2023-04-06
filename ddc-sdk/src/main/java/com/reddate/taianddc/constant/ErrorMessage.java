package com.reddate.taianddc.constant;


public enum ErrorMessage {
    // Param check  1
    // Chain error  2
    // other 9

    UNKNOWN_ERROR(9999, "unknown error"),
    ACCOUNT_NAME_IS_EMPTY(1001, "accountName is empty"),
    ACCOUNT_IS_EMPTY(1002, "account is empty"),
    ACCOUNT_STATUS_IS_EMPTY(1003, "account status is empty"),
    ACCOUNT_LEADER_DID_IS_EMPTY(1004, "leader DID is empty"),
    ACCOUNT_IS_NOT_ADDRESS_FORMAT(1005, "account is not a standard address format"),
    AMOUNT_IS_EMPTY(1006, "amount is empty"),
    TO_ACCOUNT_IS_EMPTY(1007, "to account is empty"),
    TO_ACCOUNT_IS_NOT_ADDRESS_FORMAT(1008, "to account is not a standard address format"),
    FROM_ACCOUNT_IS_EMPTY(1009, "from account is empty"),
    FROM_ACCOUNT_IS_NOT_ADDRESS_FORMAT(1010, "from account is not a standard address format"),
    ACC_ADDR_IS_EMPTY(1011, "accAddr is empty"),
    ACC_ADDR_IS_NOT_ADDRESS_FORMAT(1012, "accAddr is not a standard address format"),
    DDC_ADDR_IS_EMPTY(1013, "ddcAddr is empty"),
    DDC_ADDR_IS_NOT_ADDRESS_FORMAT(1014, "ddcAddr is not a standard address format"),
    AMOUNT_LT_ZERO(1015, "amount is less than 0"),
    DDCID_IS_WRONG(1016, "ddcId is wrong"),
    DDCURI_IS_EMPTY(1017, "ddcURI is empty"),
    REST_TEMPLATE_CONNT_TIMEOUT_EMPTY(1018,"rest template connection time out is empty"),
    REST_TEMPLATE_READ_TIMEOUT_EMPTY(1019,"rest template read time out is empty"),
    OPB_GATEWAY_ADDRESS_EMPTY(1020,"OPB gateway address is empty"),
    DDC_721_ADDRESS_EMPTY(1021,"DDC721 contract address is empty"),
    DDC_1155_ADDRESS_EMPTY(1022,"DDC1155 contract address is empty"),
    DDC_AUTHORITY_ADDRESS_EMPTY(1022,"DDC authority logic contract address is empty"),
    DDC_CHARGE_ADDRESS_EMPTY(1024,"DDC charge logic contract address is empty"),
    FILE_NOT_EXISTS(1025,"file is not exists"),
    READ_FILE_FAILED(1026,"raed file is failed"),
    SIGN_METHOD_EMPTY(1027,"sign method is empty"),
    NO_SIGN_EVENT_LISTNER(1028,"not register sign event listener"),
    ROLE_IS_EMPTY(1029, "role is empty"),
    SENDER_IS_EMPTY(1030, "sender is empty"),
    SENDER_IS_NOT_ADDRESS_FORMAT(1031, "sender is not a standard address format"),
    
    SIG_IS_EMPTY(1032, "sig is empty"),
    SIG_IS_NOT_4BYTE_HASH(1033, "sig is not 4 byte hash"),
    DID_IS_EMPTY(1034, "did is empty"),
    OWNER_IS_EMPTY(1035, "owner is empty"),
    OWNER_IS_NOT_ADDRESS_FORMAT(1036, "owner is not a standard address format"),
    NAME_IS_EMPTY(1037, "name is empty"),
    SYMBOL_IS_EMPTY(1038, "symbol is empty"),
    DEADLINE_IS_WRONG(1039, "deadline is wrong"),
    ID_V_IS_WRONG(1040, "id v is wrong"),
    SIG_OUTPUT_R_IS_EMPTY(1041, "sig output r is empty"),
    SIG_OUTPUT_S_IS_EMPTY(1042, "sig output s is empty"),
    TO_CHAIN_ID_IS_EMPTY(1043, "to Chain ID is empty"),
    TO_CHAIN_ID_IS_WRONG(1044, "to Chain ID is wrong"),
    TO_CC_ADDR_IS_EMPTY(1045, "to CC Addr is empty"),
    TO_CC_ADDR_IS_NOT_ADDRESS_FORMAT(1046, "to CC Addr is not address format"),
    FUNCNAME_IS_EMPTY(1047, "funcname is empty"),
    DDC_TYPE_IS_WRONG(1048, "ddc type is wrong"),
    CROSS_CHAIN_ID_IS_WRONG(1049, "cross chain id type is wrong"),
    DDCS_IS_EMPTY(1050, "ddcs is empty"),
    DDCS_OWNER_IS_EMPTY(1051, "ddcs owner is empty"),
    ISLOCK_IS_EMPTY(1052, "islock is empty"),
    CROSS_CHAIN_STATE_IS_EMPTY(1053, "cross chain state is empty"),
    ILLEGAL_STATE_PARAMETER(1054, "illegal state parameter"),
    REMARK_IS_EMPTY(1055, "remark is empty"),
    REQUEST_FAILED(2001, ""),
    ;

    private Integer code;

    private String message;

    private ErrorMessage(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Integer getCode() {
        return code;
    }

    public static String getMessage(Integer code) {
        for (ErrorMessage error : ErrorMessage.values()) {
            if (error.code.equals(code)) {
                return error.message;
            }
        }
        return null;
    }

    public static String getMessage(ErrorMessage errorMessage) {
        for (ErrorMessage error : ErrorMessage.values()) {
            if (error.code.equals(errorMessage.code)) {
                return error.message;
            }
        }
        return null;
    }
}
