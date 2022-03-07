package com.reddate.ddc.constant;

public class AuthorityFunctions {
//    public static final String AddAccount = "addAccountByPlatform"; 

    public static final String AddAccountByOperator = "addAccountByOperator";

    public static final String DelAccount = "DelAccount";

    public static final String GetAccount = "getAccount";

    public static final String UpdateAccountState = "updateAccountState";

    public static final String GetFunction = "getFunctions";

    public static final String DelFunction = "delFunction";

    public static final String AddFunction = "addFunction";

    public static final String CrossPlatformApproval = "crossPlatformApproval";


    public static final String AddAccountEvent = "AddAccount(address,address)";
    public static final String DelAccountEvent = "DelAccount(address)";
    public static final String UpdateAccountStateEvent = "UpdateAccountState(address,uint8,uint8)";
    public static final String AddFunctionEvent = "AddFunction(address,Role,address,bytes4)";
    public static final String DelFunctionEvent = "DelFunction(address,Role,address,bytes4)";
}
