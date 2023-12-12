package br.com.sodexo.new4ccore.account.service.action;

import br.com.sodexo.new4ccore.account.service.integration.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;

import static br.com.sodexo.new4ccore.account.utils.Utils.getCardResponse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class CancelCardsByAccountServiceTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CancelCardsByAccountService service;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(service, "attemptsMax", 2);
    }


    @Test
    public void executeCancelCardWithoutError() {

        when(cardService.getCardsByAccountId(any()))
                .thenReturn(getCardResponse());

        assertDoesNotThrow(() -> service.execute(1l));

        verify(cardService, times(1)).getCardsByAccountId(any());
        verify(cardService, times(1)).cancelCard(any(), any());
    }

    @Test
    public void executeCancelCardWithoutErrorEmptyList() {

        when(cardService.getCardsByAccountId(any()))
                .thenReturn(null);

        assertDoesNotThrow(() -> service.execute(1l));

        verify(cardService, times(1)).getCardsByAccountId(any());
        verify(cardService, times(0)).cancelCard(any(), any());
    }

    @Test
    public void executeCancelCardWithErrorAndRetryCancel() {

        when(cardService.getCardsByAccountId(any()))
                .thenReturn(getCardResponse());
        doThrow(new RuntimeException("teste")).when(cardService).cancelCard(any(), any());

        assertThrows(Exception.class, () -> service.execute(1l));

        verify(cardService, times(1)).getCardsByAccountId(any());
        verify(cardService, times(2)).cancelCard(any(), any());
    }

    @Test
    public void executeCancelCardWithErrorNotThrows() {

        when(cardService.getCardsByAccountId(any()))
                .thenReturn(getCardResponse());
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(cardService).cancelCard(any(), any());

        assertDoesNotThrow(() -> service.execute(1l));

        verify(cardService, times(1)).getCardsByAccountId(any());
        verify(cardService, times(1)).cancelCard(any(), any());
    }

}
