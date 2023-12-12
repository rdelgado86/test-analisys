package br.com.sodexo.new4ccore.account.controller;

import br.com.sodexo.new4ccore.account.dto.PaginationDTO;
import br.com.sodexo.new4ccore.account.dto.request.CreateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.dto.response.GetAccountResponseDTO;
import br.com.sodexo.new4ccore.account.enums.RouteEnum;
import br.com.sodexo.new4ccore.account.util.PaginationUtils;
import br.com.sodexo.new4ccore.account.utils.BaseResourceTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsumerController.class)
public class ConsumerControllerTest extends BaseResourceTest {

    final String GET_ACCOUNTS_PATH = RouteEnum.CONSUMERS_API_BASE_PATH + "/{consumerId}/accounts";
    final String POST_CREATE_ACCOUNT_PATH = RouteEnum.CONSUMERS_API_BASE_PATH + "/{consumerId}/accounts";

    final Long CONSUMER_ID = 10L;

    @Test
    void givenOneAccount_whenGetAccountList_thenShouldReturnCorrespondingAccountWithEveryFieldSet() throws Exception {
        when(accountService.getAccountList(anyLong(),any(),any(), any(PageRequest.class))).thenReturn(mockGetAccountListResponse());

        mockMvc.perform(get(GET_ACCOUNTS_PATH, CONSUMER_ID).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts[0]", notNullValue()))
                .andExpect(jsonPath("$.accounts", hasSize(1)))
                .andExpect(jsonPath("$.accounts[0].id", notNullValue()))
                .andExpect(jsonPath("$.accounts[0].consumerId", notNullValue()))
                .andExpect(jsonPath("$.accounts[0].customerId", notNullValue()))
                .andExpect(jsonPath("$.accounts[0].accountNumber", notNullValue()))
                .andExpect(jsonPath("$.accounts[0].externalAccountNumber", notNullValue()))
                .andExpect(jsonPath("$.accounts[0].productId", notNullValue()))
                .andExpect(jsonPath("$.accounts[0].status", notNullValue()))
                .andExpect(jsonPath("$.accounts[0].creationDate", notNullValue()))
                .andExpect(jsonPath("$.accounts[0].closureDate", notNullValue()));
    }

    @Test
    void givenNoAccount_whenGetAccountList_thenShouldReturnEmptyAccountList() throws Exception {
        when(accountService.getAccountList(anyLong(),anyString(),any(), any(PageRequest.class))).thenReturn(mockEmptyGetAccountListResponse());

        mockMvc.perform(get(GET_ACCOUNTS_PATH, CONSUMER_ID).contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts", hasSize(0)));
    }

    @Test
    void givenCreateAccountRequest_whenCreateAccount_thenShouldRespondWithStatus201_andReturnAccountObjectWithEveryFieldSet() throws Exception {
        when(accountService.createAccount(anyLong(), any(CreateAccountRequestDTO.class))).thenReturn(mockGetAccountResponseDTO());

        mockMvc.perform(post(POST_CREATE_ACCOUNT_PATH, CONSUMER_ID)
                        .contentType(APPLICATION_JSON)
                        .content(mockCreateAccountRequestContent())
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.consumerId", notNullValue()))
                .andExpect(jsonPath("$.customerId", notNullValue()))
                .andExpect(jsonPath("$.accountNumber", notNullValue()))
                .andExpect(jsonPath("$.externalAccountNumber", notNullValue()))
                .andExpect(jsonPath("$.beneficiaryId", notNullValue()))
                .andExpect(jsonPath("$.productId", notNullValue()))
                .andExpect(jsonPath("$.status", notNullValue()))
                .andExpect(jsonPath("$.creationDate", notNullValue()))
                .andExpect(jsonPath("$.closureDate", notNullValue()));
    }

    private String mockCreateAccountRequestContent() throws JsonProcessingException {
        return objectMapper.writeValueAsString(
                CreateAccountRequestDTO.builder()
                        .customerId(1L)
                        .productId(1L)
                        .build()
        );
    }

    private PaginationDTO<GetAccountResponseDTO> mockEmptyGetAccountListResponse() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<GetAccountResponseDTO> pageResponse = new PageImpl<>(Collections.emptyList(), pageable, 0);
        return PaginationUtils.buildResponse(pageResponse);
    }

    private PaginationDTO<GetAccountResponseDTO> mockGetAccountListResponse() {
        PaginationDTO<GetAccountResponseDTO> response = new PaginationDTO<>();
        response.setAccounts(List.of(mockGetAccountResponseDTO()));
        return response;
    }

}
