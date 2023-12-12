package br.com.sodexo.new4ccore.account.service;

import br.com.sodexo.new4ccore.account.dto.CreateAccountTopicDTO;
import br.com.sodexo.new4ccore.account.dto.PaginationDTO;
import br.com.sodexo.new4ccore.account.dto.request.CreateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.dto.request.UpdateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.dto.response.*;
import br.com.sodexo.new4ccore.account.entity.AccountEntity;
import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import br.com.sodexo.new4ccore.account.interceptor.LoggerInterceptor;
import br.com.sodexo.new4ccore.account.repository.AccountRepository;
import br.com.sodexo.new4ccore.account.service.action.CancelCardsByAccountService;
import br.com.sodexo.new4ccore.account.service.baas.BaasIntegration;
import br.com.sodexo.new4ccore.account.service.integration.*;
import br.com.sodexo.new4ccore.account.service.strategy.CardProcessorStrategy;
import br.com.sodexo.new4ccore.account.service.strategy.CardProcessorStrategyFactory;
import br.com.sodexo.new4ccore.account.util.PaginationUtils;
import br.com.sodexo.new4ccore.accounteventslib.dto.AccountEventDTO;
import br.com.sodexo.new4ccore.accounteventslib.enums.EventEnum;
import br.com.sodexo.new4ccore.accounteventslib.service.PostMessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static br.com.sodexo.new4ccore.account.enums.AccountStatusEnum.CANCELLED;
import static br.com.sodexo.new4ccore.account.enums.ErrorsEnum.*;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerService customerService;
    private final ConsumerService consumerService;
    private final CustomerWalletService customerWalletService;
    private final AccountWalletService accountWalletService;
    private final ModelMapper modelMapper;
    private final BaasIntegration baasIntegration;
    private final PostMessageService postMessageService;
    private final TopicAccountCreateService topicAccountCreateService;
    private final CancelCardsByAccountService cancelCardsByAccountService;
    private final ProductService productService;
    private final CardProcessorStrategyFactory cardProcessorStrategyFactory;

    public GetAccountResponseDTO createAccount(Long consumerId, CreateAccountRequestDTO accountDTO) throws Exception {
        AccountEntity accountEntity = modelMapper.map(accountDTO, AccountEntity.class);
        accountEntity.setConsumerId(consumerId);

        log.info("Validate account number: {}", accountEntity.getAccountNumber());
        validateCreateAccount(accountEntity);

        var cardProcessorName = productService.getProduct(accountDTO.getProductId()).getCardProcessorName();
        CardProcessorStrategy cardProcessorStrategy = cardProcessorStrategyFactory.findStrategy(cardProcessorName);
        cardProcessorStrategy.updateAccount(accountDTO, accountEntity);

        accountEntity.setAccountNumber(accountRepository.getNextAccountNumber());
        accountEntity.setStatus(AccountStatusEnum.ACTIVE);
        accountEntity.setCreationDate(LocalDate.now());

        log.info("Save account: {}", accountEntity.getAccountNumber());
        var accountSaved = accountRepository.save(accountEntity);

        CreateAccountTopicDTO accountTopic = modelMapper.map(accountEntity, CreateAccountTopicDTO.class);
        accountTopic.setRequestId(LoggerInterceptor.REQUEST_UUID);
        topicAccountCreateService.send(accountTopic);

        createAccountWallets(accountDTO, accountEntity);

        postMessageService.executeAsync(createEventDTO(EventEnum.CREATED_ACCOUNT, accountSaved.getId(), null));

        return modelMapper.map(accountEntity, GetAccountResponseDTO.class);
    }

    public UpdateAccountResponseDTO updateAccount(Long accountId, UpdateAccountRequestDTO accountDTO) throws AccountException {
        AccountEntity accountEntity = accountRepository.findById(accountId).orElseThrow(ACCOUNT_NOT_FOUND::newError);
        validateUpdateAccount(accountDTO, accountEntity);

        accountEntity.setStatus(AccountStatusEnum.valueOf(accountDTO.getStatus()));
        if (accountDTO.getStatus().equals(CANCELLED.toString())) {
            accountEntity.setClosureDate(LocalDate.now());
            cancelCardsByAccountService.execute(accountId);
        }

        log.info("BAAS Account Integration");
        Object response = baasIntegration.updateAccount(accountEntity.getExternalAccountNumber(), AccountStatusEnum.valueOf(accountDTO.getStatus()));
        log.info("Account updated at Baas provider: {}", response);

        accountRepository.save(accountEntity);

        CreateAccountTopicDTO accountTopic = modelMapper.map(accountEntity, CreateAccountTopicDTO.class);
        accountTopic.setRequestId(LoggerInterceptor.REQUEST_UUID);
        topicAccountCreateService.send(accountTopic);

        postMessageService.executeAsync(createEventDTO(getCorrespondingEvent(accountDTO.getStatus()), accountEntity.getId(), null));

        return modelMapper.map(accountEntity, UpdateAccountResponseDTO.class);
    }

    private EventEnum getCorrespondingEvent(String accountEvent) {
        return switch (accountEvent) {
            case "ACTIVE" -> EventEnum.UNBLOCK_ACCOUNT;
            case "BLOCKED" -> EventEnum.BLOCK_ACCOUNT;
            case "CANCELLED" -> EventEnum.CANCEL_ACCOUNT;
            default -> null;
        };
    }

    public PaginationDTO<GetAccountResponseDTO> getAccountList(Long consumerId, String status,Long productId, PageRequest pageRequest) {
        Page<AccountEntity> accountPageResult = accountRepository.findAll((root, query, builder) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(builder.equal(root.get("consumerId"), consumerId));

            if (!status.isEmpty())
                predicates.add(builder.equal(root.get("status"), AccountStatusEnum.fromValue(status.trim().toUpperCase())));
            if (productId!=null)
                predicates.add(builder.equal(root.get("productId"), productId));

            return builder.and(predicates.toArray(new Predicate[]{}));
        }, pageRequest);
        List<GetAccountResponseDTO> accountList = accountPageResult.getContent()
                .stream()
                .map(p -> modelMapper.map(p, GetAccountResponseDTO.class))
                .collect(Collectors.toList());
        Pageable pageable = PageRequest.of(accountPageResult.getNumber(), accountPageResult.getSize(), accountPageResult.getSort());
        Page<GetAccountResponseDTO> pageResponse = new PageImpl<>(accountList, pageable, accountPageResult.getTotalElements());
        return PaginationUtils.buildResponse(pageResponse);
    }

    public GetAccountResponseDTO getAccountById(Long accountId) throws AccountException {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(ACCOUNT_NOT_FOUND::newError);
        return modelMapper.map(account, GetAccountResponseDTO.class);
    }
    public GetAccountResponseDTO getAccountByIdAndProduct(Long accountId, Long productId) throws AccountException {
        AccountEntity account = accountRepository.findByIdAndProductId(accountId, productId)
                .orElseThrow(ACCOUNT_NOT_FOUND::newError);
        return modelMapper.map(account, GetAccountResponseDTO.class);
    }

    public GetAccountByCCPResponseDTO getAccountByCCP(Long consumerId, Long customerId, Long productId) throws AccountException {
        AccountEntity account = accountRepository.findByConsumerIdAndCustomerIdAndProductIdAndStatusNot(consumerId, customerId, productId, CANCELLED)
                .orElseThrow(ACCOUNT_NOT_FOUND::newError);
        return modelMapper.map(account, GetAccountByCCPResponseDTO.class);
    }

    public PaginationDTO<GetAccountResponseDTO> getAccountsByCustomerId(Long customerId, PageRequest pageRequest) {
        var accountPageResult = accountRepository.findByCustomerIdAndStatusNot(customerId, CANCELLED, pageRequest);

        return PaginationUtils.buildResponse(new PageImpl<>(
                accountPageResult.getContent().stream()
                        .map(p -> modelMapper.map(p, GetAccountResponseDTO.class)).toList(),
                PageRequest.of(accountPageResult.getNumber(), accountPageResult.getSize(), accountPageResult.getSort()),
                accountPageResult.getTotalElements())
        );
    }

    private void validateUpdateAccount(UpdateAccountRequestDTO accountDTO, AccountEntity accountEntity) throws AccountException {
        if (accountEntity.getStatus().equals(CANCELLED)) {
            throw ACCOUNT_ALREADY_CANCELLED.newError();
        }

        if (accountEntity.getStatus().equals(AccountStatusEnum.ACTIVE) && accountDTO.getStatus().equals(AccountStatusEnum.ACTIVE.toString())) {
            throw ACCOUNT_ALREADY_ACTIVE.newError();
        }
    }

    private void validateCreateAccount(AccountEntity accountEntity) throws Exception {
        Future<Boolean> customerExistsAsync = customerService.customerExistsAsync(accountEntity.getCustomerId());
        Future<Boolean> consumerExistsAsync = consumerService.consumerExistsAsync(accountEntity.getConsumerId());
        if (consumerExistsAsync.get() && customerExistsAsync.get()) {
            Boolean existsAccount = accountRepository.existsByConsumerIdAndCustomerIdAndProductIdAndStatusNot(
                    accountEntity.getConsumerId(),
                    accountEntity.getCustomerId(),
                    accountEntity.getProductId(),
                    CANCELLED);
            if (existsAccount) {
                throw ACCOUNT_ALREADY_EXIST.newError();
            }
        }
    }

    private void createAccountWallets(CreateAccountRequestDTO accountDTO, AccountEntity accountEntity) throws AccountException {
        CustomerWalletListResponseDTO customerWalletList = customerWalletService.getCustomerWalletList(accountDTO.getCustomerId(), accountEntity.getProductId());
        for (CustomerWalletResponseDTO wallet : customerWalletList.getWallets()) {
            log.info("Creating wallet {}", wallet.getName());
            accountWalletService.createAccountWallet(accountEntity.getId(), wallet.getWalletId());
        }
    }

    private AccountEventDTO createEventDTO(EventEnum event, Long accountId, String reason) {
        return AccountEventDTO.builder()
                .event(event)
                .date(LocalDateTime.now())
                .accountId(accountId)
                .reason(reason)
                .build();
    }

}
