package br.com.sodexo.new4ccore.account.dto.response;

import java.io.Serializable;

import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor
public class GetAccountByCCPResponseDTO implements Serializable {
	private static final long serialVersionUID = 9215849484446616584L;
	private Long id;
	private Long consumerId;
	private Long customerId;
	private Long accountNumber;
	private Long externalAccountNumber;
	private Long productId;
	private AccountStatusEnum status;
}
