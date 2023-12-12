package br.com.sodexo.new4ccore.account.service.strategy;

import br.com.sodexo.new4ccore.account.dto.request.CreateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.entity.AccountEntity;
import br.com.sodexo.new4ccore.account.enums.CardProcessorStrategyName;
import br.com.sodexo.new4ccore.account.enums.ErrorsEnum;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static br.com.sodexo.new4ccore.account.enums.ErrorsEnum.BENEFICIARY_ID_IS_MANDATORY;
import static br.com.sodexo.new4ccore.account.enums.ErrorsEnum.EXTERNAL_ACCOUNT_NUMBER_IS_MANDATORY;
import static java.util.Objects.isNull;

@Slf4j
@Component
@AllArgsConstructor
public class DxcStrategy implements CardProcessorStrategy {
    @Override
    public AccountEntity updateAccount(CreateAccountRequestDTO accountDTO, AccountEntity accountEntity) throws AccountException {
        if (isNull(accountDTO.getBeneficiaryId())) {
            ErrorsEnum.raise(BENEFICIARY_ID_IS_MANDATORY);
        }

        if (isNull(accountDTO.getExternalAccountNumber())) {
            ErrorsEnum.raise(EXTERNAL_ACCOUNT_NUMBER_IS_MANDATORY);
        }
        
        accountEntity.setBeneficiaryId(accountDTO.getBeneficiaryId());
        accountEntity.setExternalAccountNumber(accountDTO.getExternalAccountNumber());
        
        return accountEntity;
    }

    @Override
    public CardProcessorStrategyName getStrategyName() {
        return CardProcessorStrategyName.DXC;
    }
}
