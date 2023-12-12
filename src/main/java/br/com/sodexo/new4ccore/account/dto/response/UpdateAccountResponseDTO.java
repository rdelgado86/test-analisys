package br.com.sodexo.new4ccore.account.dto.response;

import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor 
@NoArgsConstructor
@Builder
public class UpdateAccountResponseDTO implements Serializable {
	private static final long serialVersionUID = -4798126140664535510L;
	private Long id;
	private Long consumerId;
	private Long customerId;
	private Long accountNumber;
	private Long externalAccountNumber;
	private Long productId;
	private LocalDate creationDate;
	private LocalDate closureDate;

	@Enumerated(EnumType.STRING)
	private AccountStatusEnum status;

}
