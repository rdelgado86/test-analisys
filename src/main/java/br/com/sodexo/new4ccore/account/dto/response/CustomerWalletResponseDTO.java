package br.com.sodexo.new4ccore.account.dto.response;

import br.com.sodexo.new4ccore.account.enums.WalletStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerWalletResponseDTO implements Serializable {

    private static final long serialVersionUID = 479961202216657225L;

    private Long id;
    private Long customerId;
    private Long walletId;
    private String name;
    private WalletStatusEnum status;
}
