package br.com.sodexo.new4ccore.account.service.baas;

import br.com.sodexo.mbpcore.dock.client.account.BlockAccountClient;
import br.com.sodexo.mbpcore.dock.client.account.CancelAccountClient;
import br.com.sodexo.mbpcore.dock.client.account.CreateAccountClient;
import br.com.sodexo.mbpcore.dock.client.account.ReactivateAccountClient;
import br.com.sodexo.mbpcore.dock.constants.BaasDockConstants;
import br.com.sodexo.mbpcore.dock.dto.generic.ErrorResponseDTO;
import br.com.sodexo.mbpcore.dock.dto.request.CreateAccountDockRequestDTO;
import br.com.sodexo.mbpcore.dock.dto.response.CreateAccountDockResponseDTO;
import br.com.sodexo.mbpcore.dock.exceptions.BaasException;
import br.com.sodexo.new4ccore.account.dto.request.CreateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BaasIntegrationImpl implements BaasIntegration {

    private final ModelMapper modelMapper;

    private final CreateAccountClient createAccountClient;

    private final BlockAccountClient blockAccountClient;

    private final CancelAccountClient cancelAccountClient;

    private final ReactivateAccountClient reactivateAccountClient;

    @Override
    public CreateAccountDockResponseDTO createAccount(CreateAccountRequestDTO request, Long consumerExternalId) throws AccountException {
        try {
            var account = getCreateAccountDockRequestDTO(consumerExternalId);
            var response = createAccountClient.doRequest(account);

            handleErrorResponseDTO(response);

            return modelMapper.map(response, CreateAccountDockResponseDTO.class);
        } catch (Exception ex) {
            throw parseException(ex);
        }
    }

    @Override
    public Object updateAccount(Long accountId, AccountStatusEnum status) throws AccountException {
        try {
            Object response = null;
            if (status.equals(AccountStatusEnum.BLOCKED)) {
                response = blockAccountClient.doRequest(accountId);
                handleErrorResponseDTO(response);
            } else if (status.equals(AccountStatusEnum.CANCELLED)) {
                response = cancelAccountClient.doRequest(accountId);
                handleErrorResponseDTO(response);
            } else if (status.equals(AccountStatusEnum.ACTIVE)) {
                response = reactivateAccountClient.doRequest(accountId);
                handleErrorResponseDTO(response);
            }
            return response;
        } catch (Exception ex) {
            throw parseException(ex);
        }
    }

    private static CreateAccountDockRequestDTO getCreateAccountDockRequestDTO(Long consumerExternalId) {
        return CreateAccountDockRequestDTO.builder()
                .idPessoa(consumerExternalId)
                .idProduto(BaasDockConstants.BAAS_DOCK_ACCOUNT_ID_PRODUCT)
                .diaVencimento(BaasDockConstants.BAAS_DOCK_ACCOUNT_DUE_DATE)
                .idOrigemComercial(BaasDockConstants.BAAS_DOCK_ACCOUNT_ID_CLIENT_REGISTER)
                .valorPontuacao(BaasDockConstants.BAAS_DOCK_ACCOUNT_SCORING)
                .valorRenda(BaasDockConstants.BAAS_DOCK_ACCOUNT_DEFAULT_INCOME)
                .build();
    }

    private AccountException parseException(Exception ex) {
        log.info("Error response: {}", ex.getMessage());

        if (ex instanceof AccountException ae) {
            return ae;
        } else if (ex instanceof BaasException be) {
            return new AccountException(be.getResponse().getMessage(), be.getResponse().getDetails());
        } else {
            return new AccountException(ex.getMessage());
        }
    }

    private void handleErrorResponseDTO(Object response) throws AccountException {
        if (response instanceof ErrorResponseDTO error) {
            log.info("Error response message: {}", error.getMessage());
            log.info("Error response details: {}", error.getDetails());
            throw new AccountException(error.getMessage(), error.getDetails());
        }
    }
}
