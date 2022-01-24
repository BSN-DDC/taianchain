package com.reddate.ddc.config;

public class ConfigInfo {

	//HTTP timeout unit : s
	private Integer conTimeout = 60;

	private Integer readTimeout = 60;

	private String opbGatewayAddress = "";

	//开放联盟链网关启用x-api-key后需设置将该值
	private String apiKey = "";

	private String ddc721ABI;

	private String ddc721BIN;

	private String ddc721Address;

	private String ddc1155ABI;

	private String ddc1155BIN;

	private String ddc1155Address;

	private String authorityLogicABI;

	private String authorityLogicBIN;

	private String authorityLogicAddress;

	private String chargeLogicABI;

	private String chargeLogicBIN;

	private String chargeLogicAddress;

	private Long queryRecepitWaitTime = 300L;

	private Integer queryRecepitRetryCount = 20;

	public Integer getConTimeout() {
		return conTimeout;
	}

	public void setConTimeout(Integer conTimeout) {
		this.conTimeout = conTimeout;
	}

	public Integer getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getOpbGatewayAddress() {
		return opbGatewayAddress;
	}

	public void setOpbGatewayAddress(String opbGatewayAddress) {
		this.opbGatewayAddress = opbGatewayAddress;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getDdc721ABI() {
		return ddc721ABI;
	}

	public void setDdc721ABI(String ddc721abi) {
		ddc721ABI = ddc721abi;
	}

	public String getDdc721BIN() {
		return ddc721BIN;
	}

	public void setDdc721BIN(String ddc721bin) {
		ddc721BIN = ddc721bin;
	}

	public String getDdc721Address() {
		return ddc721Address;
	}

	public void setDdc721Address(String ddc721Address) {
		this.ddc721Address = ddc721Address;
	}

	public String getDdc1155ABI() {
		return ddc1155ABI;
	}

	public void setDdc1155ABI(String ddc1155abi) {
		ddc1155ABI = ddc1155abi;
	}

	public String getDdc1155BIN() {
		return ddc1155BIN;
	}

	public void setDdc1155BIN(String ddc1155bin) {
		ddc1155BIN = ddc1155bin;
	}

	public String getDdc1155Address() {
		return ddc1155Address;
	}

	public void setDdc1155Address(String ddc1155Address) {
		this.ddc1155Address = ddc1155Address;
	}

	public String getAuthorityLogicABI() {
		return authorityLogicABI;
	}

	public void setAuthorityLogicABI(String authorityLogicABI) {
		this.authorityLogicABI = authorityLogicABI;
	}

	public String getAuthorityLogicBIN() {
		return authorityLogicBIN;
	}

	public void setAuthorityLogicBIN(String authorityLogicBIN) {
		this.authorityLogicBIN = authorityLogicBIN;
	}

	public String getAuthorityLogicAddress() {
		return authorityLogicAddress;
	}

	public void setAuthorityLogicAddress(String authorityLogicAddress) {
		this.authorityLogicAddress = authorityLogicAddress;
	}

	public String getChargeLogicABI() {
		return chargeLogicABI;
	}

	public void setChargeLogicABI(String chargeLogicABI) {
		this.chargeLogicABI = chargeLogicABI;
	}

	public String getChargeLogicBIN() {
		return chargeLogicBIN;
	}

	public void setChargeLogicBIN(String chargeLogicBIN) {
		this.chargeLogicBIN = chargeLogicBIN;
	}

	public String getChargeLogicAddress() {
		return chargeLogicAddress;
	}

	public void setChargeLogicAddress(String chargeLogicAddress) {
		this.chargeLogicAddress = chargeLogicAddress;
	}

	public Long getQueryRecepitWaitTime() {
		return queryRecepitWaitTime;
	}

	public void setQueryRecepitWaitTime(Long queryRecepitWaitTime) {
		this.queryRecepitWaitTime = queryRecepitWaitTime;
	}

	public Integer getQueryRecepitRetryCount() {
		return queryRecepitRetryCount;
	}

	public void setQueryRecepitRetryCount(Integer queryRecepitRetryCount) {
		this.queryRecepitRetryCount = queryRecepitRetryCount;
	}

}
