package br.com.sodexo.new4ccore.account.dto.request;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.io.Serializable;

@Data
@Builder
public class CreateAccountRequestDTO implements Serializable {

    private static final long serialVersionUID = 1686560625289751068L;

    @Positive(message = "{must_be_positive}")
    @Min(value = 1, message = "{consumerId.min_size}")
    @Max(value = 99999999999999L, message = "{consumerId.max_size}")
    private Long customerId;

    @Positive(message = "{must_be_positive}")
    @Min(value = 1, message = "{productId.min_size}")
    @Max(value = 99L, message = "{productId.max_size}")
    private Long productId;
    
    private Long beneficiaryId;

    private Long externalAccountId;
    
    private Long externalAccountNumber;
}
