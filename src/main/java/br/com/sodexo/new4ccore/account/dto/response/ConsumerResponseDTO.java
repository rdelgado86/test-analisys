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
public class ConsumerResponseDTO implements Serializable {
	private static final long serialVersionUID = -4874799001334303315L;

	private String name;
	private Long externalConsumerId;
	
}
