package br.com.sodexo.new4ccore.account.dto.response;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor
@Builder
public class CustomerResponseDTO implements Serializable {
	
	private static final long serialVersionUID = -7686921654780520644L;
	private Long id;
	private String name;
	private String tradeName;
	private String document;
	private Long ararasId;
}
