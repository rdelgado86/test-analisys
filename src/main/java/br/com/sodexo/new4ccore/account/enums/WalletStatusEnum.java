package br.com.sodexo.new4ccore.account.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum WalletStatusEnum {
	@JsonProperty("ACTIVE")
    ACTIVE,
    
	@JsonProperty("INACTIVE")
    INACTIVE;
}
