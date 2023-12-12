package br.com.sodexo.new4ccore.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponseDTO implements Serializable {
    private Long productId;
    private String name;
    private Long cardProcessorId;
    private String cardProcessorName;
}
