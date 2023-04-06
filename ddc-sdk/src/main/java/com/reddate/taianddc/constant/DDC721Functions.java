package com.reddate.taianddc.constant;

/**
 * @author wxq
 * @create 2021/12/9 10:51
 * @description DDC-721 contract functions
 */
public class DDC721Functions {
    public static final String MINT = "mint";
    public static final String SAFE_MINT = "safeMint";
    public static final String APPROVE = "approve";
    public static final String GET_APPROVED = "getApproved";
    public static final String SET_APPROVAL_FOR_ALL = "setApprovalForAll";
    public static final String IS_APPROVED_FOR_ALL = "isApprovedForAll";
    public static final String SAFE_TRANSFER_FROM = "safeTransferFrom";
    public static final String TRANSFER_FROM = "transferFrom";
    public static final String FREEZE = "freeze";
    public static final String UNFREEZE = "unFreeze";
    public static final String BURN = "burn";
    public static final String BALANCE_OF = "balanceOf";
    public static final String OWNER_OF = "ownerOf";
    public static final String NAME = "name";
    public static final String SYMBOL = "symbol";
    public static final String DDC_URI = "ddcURI";
    public static final String SET_URI = "setURI";

    public static final String MINT_BATCH = "mintBatch";
    public static final String SAFE_MINT_BATCH = "safeMintBatch";
    public static final String META_MINT = "metaMint";
    public static final String META_MINT_BATCH = "metaMintBatch";
    public static final String META_SAFE_MINT = "metaSafeMint";
    public static final String META_SAFE_MINT_BATCH = "metaSafeMintBatch";
    public static final String SAFE_BATCH_TRANSFER_FROM = "safeBatchTransferFrom";
    public static final String SET_NAME_AND_SYMBOL = "setNameAndSymbol";
    public static final String GET_LATEST_DDCID = "getLatestDDCId";
    public static final String META_TRANSFER_FROM = "metaTransferFrom";
    public static final String META_SAFE_TRANSFER_FROM = "metaSafeTransferFrom";
    public static final String META_BURN = "metaBurn";
    public static final String GET_NONCE = "getNonce";


    public static final String DDC_721_TRANSFER_EVENT = "Transfer(address,address,uint256)";
    public static final String DDC_721_TRANSFER_BATCH_EVENT = "TransferBatch(address,address,address,uint256[])";
    public static final String DDC_721_META_TRANSFER_EVENT = "MetaTransfer(address,address,address,uint256)";
    public static final String DDC_721_META_TRANSFER_BATCH_EVENT = "MetaTransferBatch(address,address,address,uint256[])";
    public static final String DDC_721_FREEZE_EVENT = "EnterBlacklist(address,uint256)";
    public static final String DDC_721_UN_FREEZE_EVENT = "ExitBlacklist(address,uint256)";
    public static final String DDC_721_SET_URI_EVENT = "SetURI(uint256,string)";
    public static final String DDC_721_APPROVE_BATCH_EVENT = "ApprovalBatch(address[],address,uint256[])";

}
