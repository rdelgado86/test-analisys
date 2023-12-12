package br.com.sodexo.new4ccore.account.dto.response;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor
public class CustomerWalletListResponseDTO implements Serializable {
	
	private static final long serialVersionUID = -6861647469100956345L;
	private List<CustomerWalletResponseDTO> wallets;
	
}
