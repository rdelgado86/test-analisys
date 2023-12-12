package br.com.sodexo.new4ccore.account.utils;

import br.com.sodexo.new4ccore.account.dto.response.CardDTO;
import br.com.sodexo.new4ccore.account.dto.response.GeneralResponseDTO;
import br.com.sodexo.new4ccore.account.entity.AccountEntity;
import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Utils {

    public static List<CardDTO> getCardResponse() {
        return List.of(CardDTO.builder()
                .id(1L)
                .build());
    }

    public static GeneralResponseDTO getGeneralResponse() {
        return GeneralResponseDTO.builder()
                .message("Teste")
                .build();
    }

    public static Optional<AccountEntity> getOptionalAccountEntity() {
        return Optional.of(AccountEntity.builder()
                .id(1L)
                .accountNumber(33333333L)
                .productId(3L)
                .consumerId(4L)
                .customerId(5L)
                .externalAccountNumber(4433L)
                .status(AccountStatusEnum.ACTIVE)
                .creationDate(LocalDate.now().minusDays(2))
                .closureDate(null)
                .build());
    }
}
