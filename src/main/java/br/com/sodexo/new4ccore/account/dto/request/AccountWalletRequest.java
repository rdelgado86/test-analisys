package br.com.sodexo.new4ccore.account.dto.request;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data @AllArgsConstructor @NoArgsConstructor 
@Builder
@Table(name = "account_wallet_entity")
public class AccountWalletRequest {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_wallet_id")
	private Long id;
	
	private Long account_account_id;
	private Long wallet_wallet_id;
	private BigDecimal balance;	
}

