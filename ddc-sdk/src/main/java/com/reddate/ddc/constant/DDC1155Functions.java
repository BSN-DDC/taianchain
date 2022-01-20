package com.reddate.ddc.constant;

public class DDC1155Functions {
    public static final String Mint = "safeMint";
    public static final String MintBatch = "safeMintBatch";
    public static final String SetApprovalForAll = "setApprovalForAll";
    public static final String IsApprovedForAll = "isApprovedForAll";
    public static final String SafeTransferFrom = "safeTransferFrom";
    public static final String SafeBatchTransferFrom = "safeBatchTransferFrom";
    public static final String Freeze  = "freeze";
    public static final String UnFreeze  = "unFreeze";
    public static final String Burn = "burn";
    public static final String BurnBatch = "burnBatch";
    public static final String BalanceOf = "balanceOf";
    public static final String BalanceOfBatch = "balanceOfBatch";
    public static final String DDCURI = "ddcURI";
    public static final String SetURI = "setURI";

    public static final String DDC1155TransferSingleEvent = "TransferSingle(address,address,address,uint256,uint256)";
    public static final String DDC1155TransferBatchEvent = "TransferBatch(address,address,address,uint256[],uint256[])";
    public static final String DDC1155FreezeEvent = "EnterBlacklist(address,uint256)";
    public static final String DDC1155UnFreezeEvent = "ExitBlacklist(address,uint256)";
}
