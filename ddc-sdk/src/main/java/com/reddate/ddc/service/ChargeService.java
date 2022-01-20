package com.reddate.ddc.service;

import com.reddate.ddc.config.ConfigCache;
import com.reddate.ddc.constant.ChargeFunctions;
import com.reddate.ddc.constant.ErrorMessage;
import com.reddate.ddc.dto.taianchain.ReqJsonRpcBean;
import com.reddate.ddc.dto.taianchain.RespJsonRpcBean;
import com.reddate.ddc.exception.DDCException;
import com.reddate.ddc.listener.SignEventListener;
import com.reddate.ddc.util.AddressUtils;
import com.reddate.ddc.util.HexUtils;
import org.fisco.bcos.web3j.abi.datatypes.Address;
import org.fisco.bcos.web3j.tx.txdecode.InputAndOutputResult;
import org.fisco.bcos.web3j.utils.Strings;

import java.math.BigInteger;
import java.util.ArrayList;

public class ChargeService extends BaseService {
	
	public ChargeService(SignEventListener signEventListener) {
		super.signEventListener = signEventListener;
	}
	
	/**
	 * 运营方、平台方调用该接口为所属同一方的同一级别账户或者下级账户充值；
	 * 
	 * @param sender 调用者地址
	 * @param to 充值账户的地址
	 * @param amount 充值金额
	 * @return 返回交易哈希
	 * @throws Exception
	 */
	public String recharge(String sender,String to, BigInteger amount) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }
        
		if(Strings.isEmpty(to)) {
    		throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_EMPTY);
    	}
		
		if(!AddressUtils.isValidAddress(to)) {
    		throw new DDCException(ErrorMessage.TO_ACCOUNT_IS_NOT_ADDRESS_FORMAT);
    	}
		
		if(amount == null || amount.compareTo(BigInteger.valueOf(0L)) <= 0) {
			 throw new DDCException(ErrorMessage.AMOUNT_IS_EMPOTY);
		}
		
    	ArrayList<Object> arrayList = new ArrayList<>();
    	arrayList.add(new Address(to));
    	arrayList.add(amount);
    	
    	ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(sender,ChargeFunctions.Recharge,arrayList);
    	RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(),reqJsonRpcBean, RespJsonRpcBean.class);
    	resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
	}
	
	/**
	 * 查询指定账户的余额。
	 * 
	 * @param accAddr 查询的账户地址
	 * @return 返回账户所对应的业务费余额
	 * @throws Exception
	 */
	public BigInteger balanceOf(String accAddr) throws Exception {

		if(Strings.isEmpty(accAddr)) {
    		throw new DDCException(ErrorMessage.ACC_ADDR_IS_EMPTY);
    	}
		
		if(!AddressUtils.isValidAddress(accAddr)) {
    		throw new DDCException(ErrorMessage.ACC_ADDR_IS_NOT_ADDRESS_FORMAT);
    	}
		
		ArrayList<Object> arrayList = new ArrayList<>();
    	arrayList.add(new Address(accAddr));
    	
    	ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(ZeroAddress,ChargeFunctions.BalanceOf,arrayList);
    	RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(),reqJsonRpcBean, RespJsonRpcBean.class);
    	resultCheck(respJsonRpcBean);
    	
    	InputAndOutputResult inputAndOutputResult = analyzeTransactionRecepitOutput(ConfigCache.get().getChargeLogicABI(),ConfigCache.get().getChargeLogicBIN(),(String)respJsonRpcBean.getResult());
    	return (BigInteger)inputAndOutputResult.getResult().get(0).getData();
	}
	
	/**
	 * 查询指定的DDC业务主逻辑合约的方法所对应的调用业务费用。
	 * 
	 * @param ddcAddr DDC业务主逻辑合约地址
	 * @param sig Hex格式的合约方法ID
	 * @return 返回DDC合约业务费
	 * @throws Exception
	 */
	public BigInteger queryFee(String ddcAddr, String sig) throws Exception {

		if(Strings.isEmpty(ddcAddr)) {
    		throw new DDCException(ErrorMessage.DDC_ADDR_IS_EMPTY);
    	}
		
		if(!AddressUtils.isValidAddress(ddcAddr)) {
    		throw new DDCException(ErrorMessage.DDC_ADDR_IS_NOT_ADDRESS_FORMAT);
    	}
		
		if(Strings.isEmpty(sig)) {
    		throw new DDCException(ErrorMessage.SIG_IS_EMPTY);
    	}
		
		if(!HexUtils.isValid4ByteHash(sig)) {
			throw new DDCException(ErrorMessage.SIG_IS_NOT_4BYTE_HASH);
		}
		
		ArrayList<Object> arrayList = new ArrayList<>();
    	arrayList.add(new Address(ddcAddr));
    	arrayList.add(sig);
    	
    	ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(ZeroAddress,ChargeFunctions.QueryFee,arrayList);
    	RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(),reqJsonRpcBean, RespJsonRpcBean.class);
    	resultCheck(respJsonRpcBean);
    	
    	InputAndOutputResult inputAndOutputResult = analyzeTransactionRecepitOutput(ConfigCache.get().getChargeLogicABI(),ConfigCache.get().getChargeLogicBIN(),(String)respJsonRpcBean.getResult());
        return (BigInteger)inputAndOutputResult.getResult().get(0).getData();
	}
	
	/**
	 * 运营方调用为自己的账户增加业务费。
	 * 
	 * @param sender 调用者地址
	 * @param amount 对运营方账户进行充值的业务费
	 * @return 返回交易哈希
	 * @throws Exception
	 */
	public String selfRecharge(String sender,BigInteger amount) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }
        
		if(amount == null || amount.compareTo(BigInteger.valueOf(0L)) <= 0) {
			 throw new DDCException(ErrorMessage.AMOUNT_IS_EMPOTY);
		}
		
    	ArrayList<Object> arrayList = new ArrayList<>();
    	arrayList.add(amount);
    	
    	ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(sender,ChargeFunctions.SelfRecharge,arrayList);
    	RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(),reqJsonRpcBean, RespJsonRpcBean.class);
    	resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
	}
	
	/**
	 * 运营方调用接口设置指定的DDC主合约的方法调用费用。
	 * 
	 * @param sender 调用者地址
	 * @param ddcAddr DDC业务主逻辑合约地址
	 * @param sig Hex格式的合约方法ID
	 * @param amount 业务费用
	 * @return 返回交易哈希
	 * @throws Exception
	 */
	public String setFee(String sender,String ddcAddr,String sig,BigInteger amount) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }
        
		if(Strings.isEmpty(ddcAddr)) {
    		throw new DDCException(ErrorMessage.DDC_ADDR_IS_EMPTY);
    	}
		
		if(!AddressUtils.isValidAddress(ddcAddr)) {
    		throw new DDCException(ErrorMessage.DDC_ADDR_IS_NOT_ADDRESS_FORMAT);
    	}
		
		if(Strings.isEmpty(sig)) {
    		throw new DDCException(ErrorMessage.SIG_IS_EMPTY);
    	}
		
		if(!HexUtils.isValid4ByteHash(sig)) {
			throw new DDCException(ErrorMessage.SIG_IS_NOT_4BYTE_HASH);
		}
		
		if(amount == null) {
			 throw new DDCException(ErrorMessage.AMOUNT_IS_EMPOTY);
		}
		
		if(amount == null || amount.compareTo(BigInteger.valueOf(0L)) < 0) {
			 throw new DDCException(ErrorMessage.AMOUNT_LT_ZERO);
		}
		
    	ArrayList<Object> arrayList = new ArrayList<>();
    	arrayList.add(ddcAddr);
    	arrayList.add(sig);
    	arrayList.add(amount);
    	
    	ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(sender,ChargeFunctions.SetFee,arrayList);
    	RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(),reqJsonRpcBean, RespJsonRpcBean.class);
    	resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
	}
	
	/**
	 * 运营方调用接口删除指定的DDC主合约的方法调用费用。
	 * 
	 * @param sender 调用者地址
	 * @param ddcAddr DDC业务主逻辑合约地址
	 * @param sig Hex格式的合约方法ID
	 * @return 返回交易哈希
	 * @throws Exception
	 */
	public String delFee(String sender,String ddcAddr, String sig) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }
        
		if(Strings.isEmpty(ddcAddr)) {
    		throw new DDCException(ErrorMessage.DDC_ADDR_IS_EMPTY);
    	}
		
		if(!AddressUtils.isValidAddress(ddcAddr)) {
    		throw new DDCException(ErrorMessage.DDC_ADDR_IS_NOT_ADDRESS_FORMAT);
    	}
		
		if(Strings.isEmpty(sig)) {
    		throw new DDCException(ErrorMessage.SIG_IS_EMPTY);
    	}
		
		if(!HexUtils.isValid4ByteHash(sig)) {
			throw new DDCException(ErrorMessage.SIG_IS_NOT_4BYTE_HASH);
		}
		
    	ArrayList<Object> arrayList = new ArrayList<>();
    	arrayList.add(ddcAddr);
    	arrayList.add(sig);
    	

    	ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(sender,ChargeFunctions.DelFee,arrayList);
    	RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(),reqJsonRpcBean, RespJsonRpcBean.class);
    	resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
	}
	
	/**
	 * 运营方调用该接口删除指定的DDC业务主逻辑合约授权。
	 * 
	 * @param sender 调用者地址
	 * @param ddcAddr DDC业务主逻辑合约地址
	 * @return 返回交易哈希
	 * @throws Exception
	 */
	public String delDDC(String sender,String ddcAddr) throws Exception {
        if (Strings.isEmpty(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_EMPTY);
        }

        if (!AddressUtils.isValidAddress(sender)) {
            throw new DDCException(ErrorMessage.SENDER_IS_NOT_ADDRESS_FORMAT);
        }
        
		if(Strings.isEmpty(ddcAddr)) {
    		throw new DDCException(ErrorMessage.DDC_ADDR_IS_EMPTY);
    	}
		
		if(!AddressUtils.isValidAddress(ddcAddr)) {
    		throw new DDCException(ErrorMessage.DDC_ADDR_IS_NOT_ADDRESS_FORMAT);
    	}
		
    	ArrayList<Object> arrayList = new ArrayList<>();
    	arrayList.add(new Address(ddcAddr));

    	ReqJsonRpcBean reqJsonRpcBean = assembleChargeTransaction(sender,ChargeFunctions.DelDDC,arrayList);
    	RespJsonRpcBean respJsonRpcBean = restTemplateUtil.sendPost(ConfigCache.get().getOpbGatewayAddress(),reqJsonRpcBean, RespJsonRpcBean.class);
    	resultCheck(respJsonRpcBean);
        return (String) respJsonRpcBean.getResult();
	}
	
	
    private ReqJsonRpcBean assembleChargeTransaction(String sender, String functionName, ArrayList<Object> params) throws Exception {
    	return assembleTransaction(sender, getBlockNumber(),ConfigCache.get().getChargeLogicABI(), ConfigCache.get().getChargeLogicAddress(), functionName,params);
    }
}
