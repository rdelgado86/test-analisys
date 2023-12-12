package br.com.sodexo.new4ccore.account.service.strategy;

import br.com.sodexo.new4ccore.account.dto.request.CreateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.entity.AccountEntity;
import br.com.sodexo.new4ccore.account.enums.CardProcessorStrategyName;
import br.com.sodexo.new4ccore.account.exception.AccountException;

public interface CardProcessorStrategy {

    AccountEntity updateAccount(CreateAccountRequestDTO accountDTO, AccountEntity accountEntity) throws AccountException;

    CardProcessorStrategyName getStrategyName();
}
