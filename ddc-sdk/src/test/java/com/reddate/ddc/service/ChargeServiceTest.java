package com.reddate.ddc.service;

import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.util.ArrayList;

import com.reddate.ddc.config.ConfigCache;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import com.reddate.ddc.DDCSdkClient;
import com.reddate.ddc.listener.Secp256K1SignEventListener;

@Slf4j
public class ChargeServiceTest {

	String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
			"MIGEAgEAMBAGByqGSM49AgEGBSuBBAAKBG0wawIBAQQgseEExMPXTcSpExzejzYZ\n" +
			"wcLWikQtoZ3BRhWergMR2LGhRANCAATCEQFr8dEbUI6ZYChl4+pE3UopdpWknZiv\n" +
			"rK7WWNymFHQQyIN15nsq5ZZat8G+iPNLtCdRSaU3h769ObArmg11\n" +
			"-----END PRIVATE KEY-----";

	String publicKey = "-----BEGIN PUBLIC KEY-----\n" +
			"MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEjRHf7EbOKvUwRJW/kn4N6Vmf++n/gBu0\n" +
			"WEBUzovj+TAxwvgB26tCfoqk9X2gTdjwwKh6o/hvtx66EDB9GlzgTA==\n" +
			"-----END PUBLIC KEY-----";
	
	String sender = "0x81072375a506581cadbd90734bd00a20cddbe48b";
	static {
		DDCSdkClient sdk = new DDCSdkClient();
		sdk.init();
	}
	
	@Test
	public void recharge() throws Exception {
		ChargeService chargeService = getChargeService();
		
//		String to = Keys.getAddress(new BigInteger("10411698110993959739535609003328767528005678182467896878050524806097812542225230327763618090295889890389743624855091682652783845527766539103610648004292062"));
		String to = "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63";
		BigInteger amount = new BigInteger("300000");
		
		String txhash = chargeService.recharge(sender,to, amount);
		log.info(txhash);
		assertNotNull(txhash);

	}
	
	
	@Test
	public void balanceOf() throws Exception {
		ChargeService chargeService = getChargeService();
		
//		String accAddr = Keys.getAddress(new BigInteger("10411698110993959739535609003328767528005678182467896878050524806097812542225230327763618090295889890389743624855091682652783845527766539103610648004292062"));
		String accAddr = "0xb0031Aa7725A6828BcCE4F0b90cFE451C31c1e63";

		BigInteger amont = chargeService.balanceOf(accAddr);
		log.info(amont.toString());
		assertNotNull(amont);
	}
	
	
	@Test
	public void queryFee() throws Exception {
		ChargeService chargeService = getChargeService();

		String ddcAddr = ConfigCache.get().getDdc1155Address();

		String sig = "0xd0def521";
		
		BigInteger fee = chargeService.queryFee(ddcAddr, sig);
		assertNotNull(fee);
		log.info("result  {} ",fee);
	}
	
	
	@Test
	public void selfRecharge() throws Exception {
		ChargeService chargeService = getChargeService();
		
		BigInteger amount = new BigInteger("10000000");
		
		String txHash = chargeService.selfRecharge(sender,amount);
		assertNotNull(txHash);
	}
	
	
	@Test
	public void set721Fee() throws Exception {
		ChargeService chargeService = getChargeService();

		String ddcAddr = ConfigCache.get().getDdc721Address();
		BigInteger amount = new BigInteger("10");

		ArrayList<String> sigList = new ArrayList<>();
		// 721
		String mint = "0xd0def521";
		String approve = "0x095ea7b3";
		String setApprovalForAll = "0xa22cb465";
		String supportsInterface = "0x01ffc9a7";
		String safeTransferFrom = "0xb88d4fde";
		String transferFrom = "0x23b872dd";
		String burn = "0x42966c68";
		String freeeze = "0xd7a78db8";
		String unFreeze = "0xd302b0dc";

		sigList.add(mint);
		sigList.add(freeeze);
		sigList.add(unFreeze);
		sigList.add(approve);
		sigList.add(setApprovalForAll);
		sigList.add(safeTransferFrom);
		sigList.add(transferFrom);
		sigList.add(burn);

		for (int i = 0; i < sigList.size(); i++) {
			String txHash = chargeService.setFee(sender,ddcAddr, sigList.get(i), amount);
			assertNotNull(txHash);
		}

	}

	@Test
	public void set1155Fee() throws Exception {
		ChargeService chargeService = getChargeService();

		String ddcAddr = ConfigCache.get().getDdc1155Address();
		BigInteger amount = new BigInteger("10");

		ArrayList<String> sigList = new ArrayList<>();

		// 1155
		String safeMint              = "0xb55bc617";
		String safeMintBatch         = "0x63570355";
		String setApprovalForAll     = "0xa22cb465";
		String isApprovedForAll      = "0xe985e9c5";
		String safeTransferFrom      = "0xf242432a";
		String safeBatchTransferFrom = "0x2eb2c2d6";
		String freeze                = "0xd7a78db8";
		String unFreeze              = "0xd302b0dc";
		String burn                  = "0x9dc29fac";
		String burnBatch             = "0xb2dc5dc3";
		String balanceOf             = "0x00fdd58e";
		String balanceOfBatch        = "0x4e1273f4";
		String ddcURI                = "0x293ec97c";

		sigList.add(safeMint);
		sigList.add(safeMintBatch);
		sigList.add(setApprovalForAll);
		sigList.add(isApprovedForAll);
		sigList.add(safeTransferFrom);
		sigList.add(safeBatchTransferFrom);
		sigList.add(freeze);
		sigList.add(unFreeze);
		sigList.add(burn);
		sigList.add(burnBatch);
		sigList.add(balanceOf);
		sigList.add(balanceOfBatch);
		sigList.add(ddcURI);

		for (int i = 0; i < sigList.size(); i++) {
			String txHash = chargeService.setFee(sender,ddcAddr, sigList.get(i), amount);
			assertNotNull(txHash);
		}

	}
	
	
	@Test
	public void delFee() throws Exception {
		ChargeService chargeService = getChargeService();
		
		String ddcAddr = "0x1f961199f2A8811f0A4bF1bF6C0Fffb97475AF22";
		String sig = "0x70a08231";
		
		String txHash = chargeService.delFee(sender,ddcAddr, sig);
		assertNotNull(txHash);
	}
	
	
	@Test
	public void delDDC() throws Exception {
		ChargeService chargeService = getChargeService();
		
		String ddcAddr = "0x1f961199f2A8811f0A4bF1bF6C0Fffb97475AF23";
		
		String txHash = chargeService.delDDC(sender,ddcAddr);
		assertNotNull(txHash);
	}
	
	private ChargeService getChargeService() {
		ChargeService chargeService = null;
		try {
			chargeService = new ChargeService(new Secp256K1SignEventListener(privateKey, publicKey));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return chargeService;
	}
}
