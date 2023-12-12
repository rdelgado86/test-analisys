package br.com.sodexo.new4ccore.account.controller;

import br.com.sodexo.new4ccore.account.dto.request.UpdateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.dto.response.UpdateAccountResponseDTO;
import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import br.com.sodexo.new4ccore.account.utils.BaseResourceTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import java.time.LocalDate;

import static br.com.sodexo.new4ccore.account.enums.ErrorsEnum.ACCOUNT_NOT_FOUND;
import static br.com.sodexo.new4ccore.account.enums.RouteEnum.ACCOUNTS_API_BASE_PATH;
import static br.com.sodexo.new4ccore.account.utils.Utils.getGeneralResponse;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest extends BaseResourceTest {

    final String ACCOUNT_PATH = ACCOUNTS_API_BASE_PATH + "/{accountId}";
    final String UPDATE_ACCOUNT_PATH = ACCOUNTS_API_BASE_PATH + "/{accountId}";

    final String BLOCK_ACCOUNT_PATH = ACCOUNTS_API_BASE_PATH + "/{accountId}/block";

    final String UNBLOCK_ACCOUNT_PATH = ACCOUNTS_API_BASE_PATH + "/{accountId}/unblock";

    final Long ACCOUNT_ID = 10L;

    @Test
    void givenExistingAccountId_whenGetAccountById_thenShouldRespondWithStatus200_andReturnAccountObjectWithEveryField() throws Exception {
        when(accountService.getAccountById(ACCOUNT_ID)).thenReturn(mockGetAccountResponseDTO());

        mockMvc.perform(get(ACCOUNT_PATH, ACCOUNT_ID).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.consumerId", notNullValue()))
                .andExpect(jsonPath("$.customerId", notNullValue()))
                .andExpect(jsonPath("$.accountNumber", notNullValue()))
                .andExpect(jsonPath("$.externalAccountNumber", notNullValue()))
                .andExpect(jsonPath("$.productId", notNullValue()))
                .andExpect(jsonPath("$.status", notNullValue()))
                .andExpect(jsonPath("$.creationDate", notNullValue()))
                .andExpect(jsonPath("$.closureDate", notNullValue()));
    }

    @Test
    void blockAccountOk() throws Exception {
        when(blockAccountService.execute(ACCOUNT_ID)).thenReturn(getGeneralResponse());

        mockMvc.perform(post(BLOCK_ACCOUNT_PATH, ACCOUNT_ID).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    void blockAccountShouldRespondWithStatusBadRequest() throws Exception {
        mockMvc.perform(post(BLOCK_ACCOUNT_PATH, "error").contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unblockAccountOk() throws Exception {
        when(unblockAccountService.execute(ACCOUNT_ID)).thenReturn(getGeneralResponse());

        mockMvc.perform(post(UNBLOCK_ACCOUNT_PATH, ACCOUNT_ID).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", notNullValue()));
    }

    @Test
    void unblockAccountShouldRespondWithStatusBadRequest() throws Exception {
        mockMvc.perform(post(UNBLOCK_ACCOUNT_PATH, "error").contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void givenNonExistingAccountId_whenGetAccountById_thenShouldRespondWithStatusNotFound() throws Exception {
        when(accountService.getAccountById(any())).thenThrow(ACCOUNT_NOT_FOUND.newError());
        mockMvc.perform(get(ACCOUNT_PATH, 1L).contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvalidAccountId_whenGetAccountById_thenShouldRespondWithStatusBadRequest() throws Exception {
        when(accountService.getAccountById(any())).thenThrow(ACCOUNT_NOT_FOUND.newError());
        mockMvc.perform(get(ACCOUNT_PATH, "error").contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidAccountUpdateInformation_whenUpdateAccount_thenShouldRespondWithStatusOk_andReturnObjectWithEveryFieldSet() throws Exception {
        when(accountService.updateAccount(any(), any(UpdateAccountRequestDTO.class))).thenReturn(mockUpdateAccountResponseDTO());

        mockMvc.perform(
                        put(UPDATE_ACCOUNT_PATH, ACCOUNT_ID)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(UpdateAccountRequestDTO.builder()
                                        .status(AccountStatusEnum.BLOCKED.toString())
                                        .build())
                                )
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.consumerId", notNullValue()))
                .andExpect(jsonPath("$.customerId", notNullValue()))
                .andExpect(jsonPath("$.accountNumber", notNullValue()))
                .andExpect(jsonPath("$.externalAccountNumber", notNullValue()))
                .andExpect(jsonPath("$.productId", notNullValue()))
                .andExpect(jsonPath("$.creationDate", notNullValue()))
                .andExpect(jsonPath("$.closureDate", notNullValue()));
    }

    private UpdateAccountResponseDTO mockUpdateAccountResponseDTO() {
        LocalDate now = LocalDate.now();
        return UpdateAccountResponseDTO
                .builder()
                .id(1L)
                .consumerId(1L)
                .customerId(1L)
                .accountNumber(1L)
                .externalAccountNumber(1L)
                .productId(1L)
                .creationDate(now)
                .closureDate(now)
                .build();
    }

}