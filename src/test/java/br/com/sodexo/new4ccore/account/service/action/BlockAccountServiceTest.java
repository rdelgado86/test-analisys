package br.com.sodexo.new4ccore.account.service.action;

import br.com.sodexo.new4ccore.account.enums.AccountStatusEnum;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import br.com.sodexo.new4ccore.account.exception.BadRequestException;
import br.com.sodexo.new4ccore.account.exception.ResourceNotFoundException;
import br.com.sodexo.new4ccore.account.repository.AccountRepository;
import br.com.sodexo.new4ccore.account.service.baas.BaasIntegration;
import br.com.sodexo.new4ccore.accounteventslib.service.PostMessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static br.com.sodexo.new4ccore.account.utils.Utils.getOptionalAccountEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class BlockAccountServiceTest {


    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PostMessageService postMessageService;

    @Mock
    private BaasIntegration baasIntegration;

    @InjectMocks
    private BlockAccountService service;


    @Test
    public void blockAccountOk() throws AccountException {

        var account = getOptionalAccountEntity().get();
        account.setStatus(AccountStatusEnum.ACTIVE);

        when(accountRepository.findById(any())).thenReturn(Optional.of(account));

        var response = assertDoesNotThrow(() -> service.execute(1l));

        verify(baasIntegration, times(1)).updateAccount(any(), eq(AccountStatusEnum.BLOCKED));
        verify(postMessageService, times(1)).executeAsync(any());

        assertEquals("Account has been locked successfully.", response.getMessage());
    }

    @Test
    public void blockAccountNotFoundShouldThrowResourceNotFound() throws AccountException {

        when(accountRepository.findById(any())).thenReturn(Optional.empty());

        var response = assertThrows(ResourceNotFoundException.class, () -> service.execute(1l));

        verify(baasIntegration, times(0)).updateAccount(any(), eq(AccountStatusEnum.BLOCKED));
        verify(postMessageService, times(0)).executeAsync(any());

        assertEquals("Account not found", response.getResponse().getMessage());
    }

    @Test
    public void blockAccountStatusInvalidShouldThrowBadRequestException() throws AccountException {

        var account = getOptionalAccountEntity().get();
        account.setStatus(AccountStatusEnum.BLOCKED);

        when(accountRepository.findById(any())).thenReturn(Optional.of(account));

        var response = assertThrows(BadRequestException.class, () -> service.execute(1l));

        verify(baasIntegration, times(0)).updateAccount(any(), eq(AccountStatusEnum.BLOCKED));
        verify(postMessageService, times(0)).executeAsync(any());

        assertEquals("Status invalid of account", response.getResponse().getMessage());
    }

}
