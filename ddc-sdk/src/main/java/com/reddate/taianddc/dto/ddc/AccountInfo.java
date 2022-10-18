package com.reddate.taianddc.dto.ddc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {

	/** DDC用户链账户地址  */
	private String accountDID;
	
	/** DDC账户对应的账户名称  */
	private String accountName;
	
	/** 账户角色 */
	private AccountRole accountRole;
	
	/** 账户上级管理者  */
	private String leaderDID;
	
	/** 平台管理账户状态  */
	private AccountState platformState;
	
	/** 运营管理账户状态 */
	private AccountState operatorState;
	
	/** 冗余字段 */
	private String field;

}
