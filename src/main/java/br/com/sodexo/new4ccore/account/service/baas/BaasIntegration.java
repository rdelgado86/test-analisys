package br.com.sodexo.new4ccore.account.service.baas;

import br.com.sodexo.mbpcore.dock.dto.response.CreateAccountDockResponseDTO;
import br.com.sodexo.new4ccore.account.dto.request.CreateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import br.com.sodexo.new4ccore.account.exception.AccountException;

public interface BaasIntegration {
	CreateAccountDockResponseDTO createAccount(CreateAccountRequestDTO request, Long consumerExternalId) throws AccountException;
	
	Object updateAccount(Long accountId, AccountStatusEnum status) throws AccountException;
	
}
