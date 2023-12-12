package br.com.sodexo.new4ccore.account.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor
public class AccountBaasResponseDTO implements Serializable {
	private static final long serialVersionUID = -4874799001334303315L;

	private Long id;
}
