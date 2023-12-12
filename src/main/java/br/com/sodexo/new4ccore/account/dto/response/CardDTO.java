package br.com.sodexo.new4ccore.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDTO {

    private Long id;
    private Long ararasId;
    private String maskedNumber;
    private Long accountId;
    private Long consumerId;
    private String expirationDate;
    private String cancellationDate;
    private String reason;
    private Long productId;
    private String status;
    private Long externalCardId;
    private Boolean virtual;

}
