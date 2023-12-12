package br.com.sodexo.new4ccore.account.service.action;

import br.com.sodexo.new4ccore.account.dto.response.ErrorResponseDTO;
import br.com.sodexo.new4ccore.account.dto.response.GeneralResponseDTO;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import br.com.sodexo.new4ccore.account.exception.BadRequestException;
import br.com.sodexo.new4ccore.account.exception.ResourceNotFoundException;
import br.com.sodexo.new4ccore.account.repository.AccountRepository;
import br.com.sodexo.new4ccore.account.service.baas.BaasIntegration;
import br.com.sodexo.new4ccore.accounteventslib.dto.AccountEventDTO;
import br.com.sodexo.new4ccore.accounteventslib.enums.EventEnum;
import br.com.sodexo.new4ccore.accounteventslib.service.PostMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static br.com.sodexo.new4ccore.account.enums.AccountStatusEnum.ACTIVE;
import static br.com.sodexo.new4ccore.account.enums.AccountStatusEnum.BLOCKED;

@Service
@Slf4j
public class UnblockAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PostMessageService postMessageService;

    @Autowired
    private BaasIntegration baasIntegration;


    public GeneralResponseDTO execute(Long accountId) throws AccountException {

        var account = accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!BLOCKED.equals(account.getStatus())) {
            throw new BadRequestException(ErrorResponseDTO.builder()
                    .message("Status invalid of account")
                    .build());
        }

        account.setStatus(ACTIVE);
        account.setStatusDate(LocalDateTime.now());

        baasIntegration.updateAccount(account.getExternalAccountNumber(), ACTIVE);

        accountRepository.save(account);

        postMessageService.executeAsync(createEventDTO(EventEnum.UNBLOCK_ACCOUNT, account.getId(), null));

        return GeneralResponseDTO.builder()
                .message("Account has been unlocked successfully.")
                .build();
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
