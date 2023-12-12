package br.com.sodexo.new4ccore.account.dto.response;

import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor 
@NoArgsConstructor
@Builder
public class GetAccountResponseDTO implements Serializable {
	private static final long serialVersionUID = 9215849484446616584L;
	private Long id;
	private Long consumerId;
	private Long customerId;
	private Long accountNumber;
	private Long externalAccountNumber;
	private Long productId;
	private AccountStatusEnum status;
	private LocalDate creationDate;
	private LocalDate closureDate;
	private Long beneficiaryId;
}
