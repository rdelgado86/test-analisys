package br.com.sodexo.new4ccore.account.utils;

import java.time.LocalDate;
import java.util.Date;

import br.com.sodexo.new4ccore.account.service.action.BlockAccountService;
import br.com.sodexo.new4ccore.account.service.action.UnblockAccountService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.sodexo.new4ccore.account.dto.response.GetAccountResponseDTO;
import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import br.com.sodexo.new4ccore.account.service.AccountService;

@ExtendWith(SpringExtension.class)
public class BaseResourceTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected AccountService accountService;

    @MockBean
    protected BlockAccountService blockAccountService;

    @MockBean
    protected UnblockAccountService unblockAccountService;

    @Autowired
    protected ObjectMapper objectMapper;

    protected GetAccountResponseDTO mockGetAccountResponseDTO() {
        var now = LocalDate.now();
        return GetAccountResponseDTO.builder()
                .consumerId(1L)
                .accountNumber(1L)
                .closureDate(now)
                .creationDate(now)
                .id(1L)
                .externalAccountNumber(1L)
                .status(AccountStatusEnum.ACTIVE)
                .productId(1L)
                .customerId(1L)
                .externalAccountNumber(1L)
                .beneficiaryId(1L)
                .build();
    }

}
