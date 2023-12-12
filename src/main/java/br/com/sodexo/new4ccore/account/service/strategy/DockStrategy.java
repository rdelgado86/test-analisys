package br.com.sodexo.new4ccore.account.service.strategy;

import br.com.sodexo.mbpcore.dock.dto.response.CreateAccountDockResponseDTO;
import br.com.sodexo.new4ccore.account.dto.request.CreateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.entity.AccountEntity;
import br.com.sodexo.new4ccore.account.enums.CardProcessorStrategyName;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import br.com.sodexo.new4ccore.account.service.baas.BaasIntegration;
import br.com.sodexo.new4ccore.account.service.integration.ConsumerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static br.com.sodexo.new4ccore.account.enums.CardProcessorStrategyName.DOCK;

@Slf4j
@Component
@AllArgsConstructor
public class DockStrategy implements CardProcessorStrategy {

    private final ConsumerService consumerService;
    private final BaasIntegration baasIntegration;

    @Override
    public AccountEntity updateAccount(CreateAccountRequestDTO accountDTO, AccountEntity accountEntity) throws AccountException {
        if (Objects.isNull(accountDTO.getExternalAccountId())) {
            Long externalConsumerId = consumerService.getConsumer(accountEntity.getConsumerId()).getBody().getExternalConsumerId();

            log.info("BAAS Account Integration");
            CreateAccountDockResponseDTO response = baasIntegration.createAccount(accountDTO, externalConsumerId);
            log.info("Account created at Baas provider: {}", response.getId());

            accountEntity.setExternalAccountNumber(response.getId());            
        } else {
            accountEntity.setExternalAccountNumber(accountDTO.getExternalAccountId());
        }

        return accountEntity;
    }

    @Override
    public CardProcessorStrategyName getStrategyName() {
        return DOCK;
    }
}
