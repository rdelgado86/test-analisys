package br.com.sodexo.new4ccore.account.service.action;

import br.com.sodexo.new4ccore.account.dto.request.CancelCardRequestDTO;
import br.com.sodexo.new4ccore.account.service.integration.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

@Service
@Slf4j
public class CancelCardsByAccountService {

    private static final CancelCardRequestDTO CANCEL_CARD_REQUEST_DTO = CancelCardRequestDTO.builder()
            .reason("020")
            .build();
    @Autowired
    private CardService cardService;
    @Value("${retry.attempts-max}")
    private int attemptsMax;

    public void execute(Long accountId) {
        log.info("Canceling cards by account id: {}", accountId);

        var cards = cardService.getCardsByAccountId(accountId);

        if (Objects.nonNull(cards)) {

            for (var card : cards) {
                var attempts = 0;

                while (attempts < attemptsMax) {
                    try {

                        log.info("Canceling card: {}, attempts: {}", card, attempts);

                        cardService.cancelCard(card.getId(), CANCEL_CARD_REQUEST_DTO);

                        attempts = attemptsMax;

                    } catch (Exception ex) {

                        log.error("Error canceling card: {}", ex.getMessage());

                        if (ex instanceof HttpClientErrorException &&
                                ((HttpClientErrorException) ex).getStatusCode().equals(HttpStatus.BAD_REQUEST)) {

                            attempts = attemptsMax;
                        } else {
                            attempts++;

                            if (attempts == attemptsMax) {
                                throw ex;
                            }
                        }
                    }
                }
            }
        }
    }
}
