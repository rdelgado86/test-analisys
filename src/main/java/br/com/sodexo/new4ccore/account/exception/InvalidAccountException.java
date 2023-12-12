package br.com.sodexo.new4ccore.account.exception;

import org.springframework.http.HttpStatus;

import br.com.sodexo.new4ccore.account.dto.response.ErrorResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class InvalidAccountException extends RuntimeException {

    private static final long serialVersionUID = 467832872382247419L;

    private HttpStatus httpStatus;
    private ErrorResponseDTO response;

    public InvalidAccountException(ErrorResponseDTO error) {
        this.response = error;
        this.httpStatus = HttpStatus.CONFLICT;
    }
    
}