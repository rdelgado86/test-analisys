package br.com.sodexo.new4ccore.account.dto;

import java.time.LocalDate;

import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder
public class CreateAccountTopicDTO {
	private Long id;
	private Long accountNumber;
	private Long productId;
	private Long consumerId;
	private Long customerId;
	private Long externalAccountNumber;
	private AccountStatusEnum status;
    private LocalDate creationDate;
    private LocalDate closureDate;
    private String requestId;
}
