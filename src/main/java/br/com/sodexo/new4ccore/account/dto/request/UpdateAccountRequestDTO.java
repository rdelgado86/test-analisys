package br.com.sodexo.new4ccore.account.dto.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import br.com.sodexo.new4ccore.account.validator.EnumValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAccountRequestDTO implements Serializable {

	private static final long serialVersionUID = 6524173433770074913L;

	@NotNull
    @EnumValidator(
            enumClass = AccountStatusEnum.class,
            message = "{invalid_account_status}"
    )
	private String status;

}
