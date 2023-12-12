package br.com.sodexo.new4ccore.account.service;

import br.com.sodexo.mbpcore.dock.dto.response.CreateAccountDockResponseDTO;
import br.com.sodexo.new4ccore.account.configuration.ModelMapperConfiguration;
import br.com.sodexo.new4ccore.account.dto.PaginationDTO;
import br.com.sodexo.new4ccore.account.dto.request.CreateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.dto.request.UpdateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.dto.response.*;
import br.com.sodexo.new4ccore.account.entity.AccountEntity;
import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import br.com.sodexo.new4ccore.account.enums.WalletStatusEnum;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import br.com.sodexo.new4ccore.account.exception.CardProcessorStrategyNotFoundException;
import br.com.sodexo.new4ccore.account.repository.AccountRepository;
import br.com.sodexo.new4ccore.account.service.action.CancelCardsByAccountService;
import br.com.sodexo.new4ccore.account.service.baas.BaasIntegration;
import br.com.sodexo.new4ccore.account.service.integration.*;
import br.com.sodexo.new4ccore.account.service.strategy.CardProcessorStrategyFactory;
import br.com.sodexo.new4ccore.account.service.strategy.DockStrategy;
import br.com.sodexo.new4ccore.account.service.strategy.DxcStrategy;
import br.com.sodexo.new4ccore.accounteventslib.dto.AccountEventDTO;
import br.com.sodexo.new4ccore.accounteventslib.enums.EventEnum;
import br.com.sodexo.new4ccore.accounteventslib.service.PostMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static br.com.sodexo.new4ccore.account.enums.WalletStatusEnum.INACTIVE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    private final AccountRepository accountRepository = mock(AccountRepository.class);
    private final CustomerService customerService = mock(CustomerService.class);
    private final ConsumerService consumerService = mock(ConsumerService.class);
    private final CustomerWalletService customerWalletService = mock(CustomerWalletService.class);
    private final AccountWalletService accountWalletService = mock(AccountWalletService.class);
    private final ModelMapper modelMapper = new ModelMapperConfiguration().modelMapper();
    private final BaasIntegration baasIntegration = mock(BaasIntegration.class);
    private final TopicAccountCreateService topicAccountCreateService = mock(TopicAccountCreateService.class);

    private final PostMessageService postMessageService = mock(PostMessageService.class);

    private final CancelCardsByAccountService cancelCardsByAccountService = mock(CancelCardsByAccountService.class);
    private final ProductService productService = mock(ProductService.class);
    private final CardProcessorStrategyFactory cardProcessorStrategyFactory = new CardProcessorStrategyFactory(
            Set.of(new DxcStrategy(), new DockStrategy(consumerService, baasIntegration)));
    private AccountService accountService;

    @BeforeEach
    void setUp() throws AccountException {
        when(customerService.customerExistsAsync(anyLong())).thenReturn(CompletableFuture.completedFuture(true));
        when(consumerService.consumerExistsAsync(anyLong())).thenReturn(CompletableFuture.completedFuture(true));
        when(consumerService.getConsumer(anyLong())).thenReturn(ResponseEntity.ok(getConsumerName()));
        when(customerService.getCustomer(anyLong())).thenReturn(ResponseEntity.ok(getCustomerResponseDTO()));
        when(baasIntegration.createAccount(any(), any())).thenReturn(getCreateAccountDockResponseDTO());
        when(customerWalletService.getCustomerWalletList(any(), any())).thenReturn(getCustomerWalletListResponseDTO());
        when(accountRepository.getNextAccountNumber()).thenReturn(10L);
        when(accountRepository.findById(any())).thenReturn(getOptionalAccountEntity());
        when(accountRepository.findByConsumerIdAndCustomerIdAndProductIdAndStatusNot(any(), any(), anyLong(), any())).thenReturn(getOptionalAccountEntity());
        when(productService.getProduct(anyLong())).thenReturn(getProduct4CResponseDTO());

        Pageable pageable = PageRequest.of(1, 1);
        List<AccountEntity> accountEntities = List.of(getOptionalAccountEntity().get());
        Page<AccountEntity> pageResponse = new PageImpl<>(accountEntities, pageable, accountEntities.size());
        when(accountRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(pageResponse);

        accountService = new AccountService(
                accountRepository,
                customerService,
                consumerService,
                customerWalletService,
                accountWalletService,
                modelMapper,
                baasIntegration,
                postMessageService,
                topicAccountCreateService,
                cancelCardsByAccountService,
                productService,
                cardProcessorStrategyFactory
        );
    }

    @Test
    void givenValidAccountCreationData_whenCreateAccount_thenAccountShouldBeCreated() throws Exception {
        when(accountRepository.save(any())).thenReturn(getOptionalAccountEntity().get());
        GetAccountResponseDTO account = accountService.createAccount(1L, getCreateAccountRequestDTO());

        verify(consumerService, times(1)).getConsumer(anyLong());
        verify(baasIntegration, times(1)).createAccount(any(), any());
        verify(accountRepository, times(1)).getNextAccountNumber();
        verify(accountRepository, times(1)).save(any());
        verify(accountWalletService, times(2)).createAccountWallet(any(), anyLong());
        verify(postMessageService, times(1)).executeAsync(any());

        assertNotNull(account);
        assertEquals(10L, account.getAccountNumber());
        assertEquals(15L, account.getExternalAccountNumber());
        assertEquals(AccountStatusEnum.ACTIVE, account.getStatus());
        assertNotNull(account.getCreationDate());
    }

    @Test
    void givenAlreadyExistingAccount_whenCreateAccount_thenShouldThrowAccountException() throws AccountException {
        when(accountRepository.existsByConsumerIdAndCustomerIdAndProductIdAndStatusNot(any(), any(), anyLong(), any())).thenReturn(true);
        assertThrows(AccountException.class, () -> accountService.createAccount(1L, getCreateAccountRequestDTO()));

        verify(baasIntegration, times(0)).createAccount(any(), any());
        verify(accountRepository, times(0)).getNextAccountNumber();
        verify(accountRepository, times(0)).save(any());
        verify(accountWalletService, times(0)).createAccountWallet(any(), anyLong());
    }

    @Test
    void givenNonExistingCustomer_whenCreateAccount_thenShouldThrowException() throws AccountException {
        when(customerService.customerExistsAsync(any())).thenThrow(AccountException.class);
        assertThrows(AccountException.class, () -> accountService.createAccount(1L, getCreateAccountRequestDTO()));

        verify(baasIntegration, times(0)).createAccount(any(), any());
        verify(accountRepository, times(0)).getNextAccountNumber();
        verify(accountRepository, times(0)).save(any());
        verify(accountWalletService, times(0)).createAccountWallet(any(), anyLong());
    }

    @Test
    void givenNonExistingConsumer_whenCreateAccount_thenShouldThrowException() throws AccountException {
        when(consumerService.consumerExistsAsync(any())).thenThrow(AccountException.class);
        assertThrows(AccountException.class, () -> accountService.createAccount(1L, getCreateAccountRequestDTO()));

        verify(baasIntegration, times(0)).createAccount(any(), any());
        verify(accountRepository, times(0)).getNextAccountNumber();
        verify(accountRepository, times(0)).save(any());
        verify(accountWalletService, times(0)).createAccountWallet(any(), anyLong());
    }

    @Test
    void givenBaasIntegrationError_whenCreateAccount_thenShouldThrowException() throws AccountException {
        when(baasIntegration.createAccount(any(), any())).thenThrow(AccountException.class);
        assertThrows(AccountException.class, () -> accountService.createAccount(1L, getCreateAccountRequestDTO()));

        verify(baasIntegration, times(1)).createAccount(any(), any());
        verify(accountRepository, times(0)).getNextAccountNumber();
        verify(accountRepository, times(0)).save(any());
        verify(accountWalletService, times(0)).createAccountWallet(any(), anyLong());
    }

    @Test
    void givenErrorWhileCreatingAccountWallet_whenCreateAccount_thenShouldThrowException() throws AccountException {
        when(accountWalletService.createAccountWallet(any(), any())).thenThrow(AccountException.class);
        assertThrows(AccountException.class, () -> accountService.createAccount(1L, getCreateAccountRequestDTO()));

        verify(baasIntegration, times(1)).createAccount(any(), any());
        verify(accountRepository, times(1)).getNextAccountNumber();
        verify(accountRepository, times(1)).save(any());
        verify(accountWalletService, times(1)).createAccountWallet(any(), anyLong());
    }

    @Test
    void givenValidUpdateAccountInformation_whenUpdateAccount_thenAccountShouldBeUpdated() throws AccountException {
        UpdateAccountRequestDTO updateAccountRequestDTO = getUpdateAccountRequestDTO();
        updateAccountRequestDTO.setStatus(AccountStatusEnum.BLOCKED.toString());

        UpdateAccountResponseDTO responseDTO = accountService.updateAccount(1L, updateAccountRequestDTO);

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getCreationDate());
        assertEquals(AccountStatusEnum.BLOCKED, responseDTO.getStatus());
        assertNull(responseDTO.getClosureDate());

        verify(baasIntegration, times(1)).updateAccount(any(), any());
        verify(accountRepository, times(1)).save(any());
    }

    @Test
    void givenValidUpdateAccountInformation_whenUpdateAccount_withBlockedStatus_thenAccountShouldBeUpdated_withCorrespondingEvent() throws AccountException {
        var eventCaptor = ArgumentCaptor.forClass(AccountEventDTO.class);
        UpdateAccountRequestDTO updateAccountRequestDTO = getUpdateAccountRequestDTO();
        updateAccountRequestDTO.setStatus(AccountStatusEnum.BLOCKED.toString());

        UpdateAccountResponseDTO responseDTO = accountService.updateAccount(1L, updateAccountRequestDTO);

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getCreationDate());
        assertEquals(AccountStatusEnum.BLOCKED, responseDTO.getStatus());
        assertNull(responseDTO.getClosureDate());

        verify(baasIntegration, times(1)).updateAccount(any(), any());
        verify(accountRepository, times(1)).save(any());
        verify(postMessageService, times(1)).executeAsync(eventCaptor.capture());

        assertEquals(EventEnum.BLOCK_ACCOUNT, eventCaptor.getValue().getEvent());
    }

    @Test
    void givenValidUpdateAccountInformation_whenUpdateAccount_withActiveStatus_thenAccountShouldBeUpdated_withCorrespondingEvent() throws AccountException {
        Optional<AccountEntity> optionalAccountEntity = getOptionalAccountEntity();
        optionalAccountEntity.get().setStatus(AccountStatusEnum.BLOCKED);
        when(accountRepository.findById(any())).thenReturn(optionalAccountEntity);
        var eventCaptor = ArgumentCaptor.forClass(AccountEventDTO.class);
        UpdateAccountRequestDTO updateAccountRequestDTO = getUpdateAccountRequestDTO();
        updateAccountRequestDTO.setStatus(AccountStatusEnum.ACTIVE.toString());

        UpdateAccountResponseDTO responseDTO = accountService.updateAccount(1L, updateAccountRequestDTO);

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getCreationDate());
        assertEquals(AccountStatusEnum.ACTIVE, responseDTO.getStatus());
        assertNull(responseDTO.getClosureDate());

        verify(baasIntegration, times(1)).updateAccount(any(), any());
        verify(accountRepository, times(1)).save(any());
        verify(postMessageService, times(1)).executeAsync(eventCaptor.capture());

        assertEquals(EventEnum.UNBLOCK_ACCOUNT, eventCaptor.getValue().getEvent());
    }

    @Test
    void givenValidUpdateAccountInformation_whenUpdateAccount_withCancelledStatus_thenAccountShouldBeUpdated_withCorrespondingEvent() throws AccountException {
        var eventCaptor = ArgumentCaptor.forClass(AccountEventDTO.class);
        UpdateAccountRequestDTO updateAccountRequestDTO = getUpdateAccountRequestDTO();
        updateAccountRequestDTO.setStatus(AccountStatusEnum.CANCELLED.toString());

        UpdateAccountResponseDTO responseDTO = accountService.updateAccount(1L, updateAccountRequestDTO);

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getCreationDate());
        assertEquals(AccountStatusEnum.CANCELLED, responseDTO.getStatus());
        assertNotNull(responseDTO.getClosureDate());

        verify(baasIntegration, times(1)).updateAccount(any(), any());
        verify(accountRepository, times(1)).save(any());
        verify(postMessageService, times(1)).executeAsync(eventCaptor.capture());

        assertEquals(EventEnum.CANCEL_ACCOUNT, eventCaptor.getValue().getEvent());
    }

    @Test
    void givenAccountUpdateWithCancelledStatus_whenUpdateAccount_thenAccountShouldBeUpdated_andClosureDateSet() throws AccountException {
        UpdateAccountRequestDTO updateAccountRequestDTO = getUpdateAccountRequestDTO();
        updateAccountRequestDTO.setStatus(AccountStatusEnum.CANCELLED.toString());

        UpdateAccountResponseDTO responseDTO = accountService.updateAccount(1L, updateAccountRequestDTO);

        assertNotNull(responseDTO);
        assertNotNull(responseDTO.getCreationDate());
        assertEquals(AccountStatusEnum.CANCELLED, responseDTO.getStatus());
        assertNotNull(responseDTO.getClosureDate());

        verify(baasIntegration, times(1)).updateAccount(any(), any());
        verify(accountRepository, times(1)).save(any());
        verify(cancelCardsByAccountService, times(1)).execute(any());
    }

    @Test
    void givenCancelledAccount_whenUpdateAccountWithCancelledStatus_thenShouldThrowException() throws AccountException {
        AccountEntity ae = getOptionalAccountEntity().get();
        ae.setStatus(AccountStatusEnum.CANCELLED);
        when(accountRepository.findById(any())).thenReturn(Optional.of(ae));

        UpdateAccountRequestDTO updateAccountRequestDTO = getUpdateAccountRequestDTO();
        updateAccountRequestDTO.setStatus(AccountStatusEnum.CANCELLED.toString());

        assertThrows(AccountException.class, () -> accountService.updateAccount(1L, updateAccountRequestDTO));

        verify(baasIntegration, times(0)).updateAccount(any(), any());
        verify(accountRepository, times(0)).save(any());
        verify(cancelCardsByAccountService, times(0)).execute(any());
    }

    @Test
    void givenActiveAccount_whenUpdateAccountWithActiveStatus_thenShouldThrowException() throws AccountException {
        AccountEntity ae = getOptionalAccountEntity().get();
        ae.setStatus(AccountStatusEnum.ACTIVE);
        when(accountRepository.findById(any())).thenReturn(Optional.of(ae));

        UpdateAccountRequestDTO updateAccountRequestDTO = getUpdateAccountRequestDTO();
        updateAccountRequestDTO.setStatus(AccountStatusEnum.ACTIVE.toString());

        assertThrows(AccountException.class, () -> accountService.updateAccount(1L, updateAccountRequestDTO));

        verify(baasIntegration, times(0)).updateAccount(any(), any());
        verify(accountRepository, times(0)).save(any());
    }

    @Test
    void givenExistingAccounts_whenGetAccountList_thenShouldReturnAccountList() {
        PaginationDTO<GetAccountResponseDTO> accountList = accountService.getAccountList(1L, "",39L, PageRequest.of(1, 10));
        assertNotNull(accountList);
        assertEquals(1L, accountList.getAccounts().size());
    }

    @Test
    void givenExistingAccount_whenGetAccountById_thenShouldReturnAccount() throws AccountException {
        GetAccountResponseDTO accountById = accountService.getAccountById(1L);
        assertNotNull(accountById);
    }

    @Test
    void givenNonExistentAccountId_whenGetAccountById_thenShouldThrowException() {
        when(accountRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(AccountException.class, () -> accountService.getAccountById(1L));
    }

    @Test
    void givenValidCCPIds_whenGetAccountByCCP_thenShouldReturnCorrespondingAccount() throws AccountException {
        GetAccountByCCPResponseDTO accountByCCP = accountService.getAccountByCCP(1L, 1L, 1L);
        assertNotNull(accountByCCP);
    }

    @Test
    void getAccountByCCP() {
        when(accountRepository.findByConsumerIdAndCustomerIdAndProductIdAndStatusNot(any(), any(), anyLong(), any())).thenReturn(Optional.empty());
        assertThrows(AccountException.class, () -> accountService.getAccountByCCP(1L, 1L, 1L));
    }

    @Test
    void givenCreateDXCAccountRequest_whenAccountDataIsCorrect_thenAccountShouldBeCreatedAndBaasIntegrationShouldNotBeCalled() throws Exception {
        when(accountRepository.save(any())).thenReturn(getOptionalAccountEntity().get());
        when(productService.getProduct(anyLong())).thenReturn(getProduct3CResponseDTO());
        GetAccountResponseDTO account = accountService.createAccount(1L, getCreateDXCAccountRequestDTO());

        verify(consumerService, times(0)).getConsumer(anyLong());
        verify(baasIntegration, times(0)).createAccount(any(), any());
        verify(accountRepository, times(1)).getNextAccountNumber();
        verify(accountRepository, times(1)).save(any());
        verify(accountWalletService, times(2)).createAccountWallet(any(), anyLong());
        verify(postMessageService, times(1)).executeAsync(any());

        assertNotNull(account);
        assertEquals(10L, account.getAccountNumber());
        assertEquals(12345678L, account.getExternalAccountNumber());
        assertEquals(AccountStatusEnum.ACTIVE, account.getStatus());
        assertNotNull(account.getCreationDate());
    }

    @Test
    void givenCreateDXCAccountRequest_whenDXCBeneficiaryIdIsNotPresent_thenShouldThrowException() throws Exception {
        when(productService.getProduct(anyLong())).thenReturn(getProduct3CResponseDTO());

        assertThrows(AccountException.class, () -> accountService.createAccount(1L, getCreateAccountRequestDTO()));
    }

    @Test
    void givenCreateDXCAccountRequest_whenDXCAccountNumberIsNotPresent_thenShouldThrowException() throws Exception {
        var request = getCreateDXCAccountRequestDTO();
        request.setExternalAccountNumber(null);
        
        when(productService.getProduct(anyLong())).thenReturn(getProduct3CResponseDTO());

        assertThrows(AccountException.class, () -> accountService.createAccount(1L, request));
    }
    
    @Test
    void givenAccountCreationRequest_whenCardProcessorHasNoMappedStrategy_thenShouldThrowException() throws Exception {
        when(productService.getProduct(anyLong())).thenReturn(getProductWithInvalidCardProcessResponseDTO());

        assertThrows(CardProcessorStrategyNotFoundException.class, () -> accountService.createAccount(1L, getCreateAccountRequestDTO()));
    }

    @Test
    void givenCreateDockAccountRequest_whenAccountExternalIdIsPresent_thenAccountShouldBeCreatedAndBaasIntegrationShouldNotBeCalled() throws Exception {
        when(accountRepository.save(any())).thenReturn(getOptionalAccountEntity().get());
        when(productService.getProduct(anyLong())).thenReturn(getProduct4CResponseDTO());
        
        var request = getCreateAccountRequestDTO();
        request.setExternalAccountId(123L);
        
        GetAccountResponseDTO account = accountService.createAccount(1L, request);

        verify(consumerService, times(0)).getConsumer(anyLong());
        verify(baasIntegration, times(0)).createAccount(any(), any());
        verify(accountRepository, times(1)).getNextAccountNumber();
        verify(accountRepository, times(1)).save(any());
        verify(accountWalletService, times(2)).createAccountWallet(any(), anyLong());
        verify(postMessageService, times(1)).executeAsync(any());

        assertNotNull(account);
        assertEquals(10L, account.getAccountNumber());
        assertEquals(123L, account.getExternalAccountNumber());
        assertEquals(AccountStatusEnum.ACTIVE, account.getStatus());
        assertNotNull(account.getCreationDate());
    }
    
    private CustomerWalletListResponseDTO getCustomerWalletListResponseDTO() {
        return new CustomerWalletListResponseDTO(
                List.of(CustomerWalletResponseDTO.builder()
                                .id(1L)
                                .customerId(1L)
                                .walletId(1L)
                                .name("customerWalletName")
                                .status(WalletStatusEnum.ACTIVE)
                                .build(),
                        CustomerWalletResponseDTO.builder()
                                .id(2L)
                                .customerId(2L)
                                .walletId(2L)
                                .name("customerWalletName_2")
                                .status(INACTIVE)
                                .build()
                )
        );
    }

    private CreateAccountDockResponseDTO getCreateAccountDockResponseDTO() {
        return CreateAccountDockResponseDTO.builder()
                .id(15L)
                .idProduto(3)
                .idOrigemComercial(1)
                .idPessoa(3)
                .idStatusConta(4)
                .diaVencimento(5)
                .melhorDiaCompra(10)
                .valorRenda(4000)
                .idProposta(20)
                .funcaoAtiva("funcao_ativa")
                .possuiOverLimit(true)
                .build();
    }

    private CustomerResponseDTO getCustomerResponseDTO() {
        return CustomerResponseDTO.builder()
                .id(1L)
                .document("document")
                .ararasId(2L)
                .name("customerName")
                .tradeName("customerTradeName")
                .build();
    }

    private ConsumerResponseDTO getConsumerName() {
        return ConsumerResponseDTO.builder()
                .name("consumerName")
                .externalConsumerId(12L)
                .build();
    }

    private UpdateAccountRequestDTO getUpdateAccountRequestDTO() {
        return UpdateAccountRequestDTO.builder()
                .status(AccountStatusEnum.BLOCKED.toString())
                .build();
    }

    private Optional<AccountEntity> getOptionalAccountEntity() {
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
                .beneficiaryId(123L)
                .build());
    }

    private CreateAccountRequestDTO getCreateAccountRequestDTO() {
        return CreateAccountRequestDTO.builder()
                .customerId(1L)
                .productId(39L)
                .build();
    }

    private CreateAccountRequestDTO getCreateDXCAccountRequestDTO() {
        return CreateAccountRequestDTO.builder()
                .customerId(1L)
                .productId(34L)
                .beneficiaryId(123L)
                .externalAccountNumber(12345678L)
                .build();
    }

    private ProductResponseDTO getProduct4CResponseDTO() {
        return ProductResponseDTO.builder()
                .cardProcessorId(1L)
                .cardProcessorName("Dock")
                .name("New 4C")
                .productId(39L)
                .build();
    }

    private ProductResponseDTO getProduct3CResponseDTO() {
        return ProductResponseDTO.builder()
                .cardProcessorId(2L)
                .cardProcessorName("DXC")
                .name("3C")
                .productId(36L)
                .build();
    }

    private ProductResponseDTO getProductWithInvalidCardProcessResponseDTO() {
        return ProductResponseDTO.builder()
                .cardProcessorId(3L)
                .cardProcessorName("Invalid Processor")
                .name("5C")
                .productId(666L)
                .build();
    }
}