package br.com.sodexo.new4ccore.account.exception;

import br.com.sodexo.new4ccore.account.dto.response.ErrorResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper=false)
public class CardProcessorStrategyNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 5152192661497323067L;

    private HttpStatus httpStatus;
    private ErrorResponseDTO response;

    public CardProcessorStrategyNotFoundException(String message) {
        this.response = ErrorResponseDTO.builder()
                .message(message)
                .build();
        this.httpStatus = HttpStatus.NOT_FOUND;
    }

    public CardProcessorStrategyNotFoundException(ErrorResponseDTO error) {
        this.response = error;
        this.httpStatus = HttpStatus.NOT_FOUND;
    }
}
