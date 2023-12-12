package br.com.sodexo.new4ccore.account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.sodexo.new4ccore.account.dto.response.ErrorResponseDTO;
import br.com.sodexo.new4ccore.account.enums.ErrorsEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AccountException extends Exception {

    private static final long serialVersionUID = 467832872382247419L;

    private HttpStatus httpStatus;
    private ErrorResponseDTO response;

    public AccountException(String message) {
        this.response = ErrorResponseDTO.builder().message(message).build();
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public AccountException(String message, String details) {
        this.response = ErrorResponseDTO.builder().message(message).details(details).build();
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    
	public AccountException(HttpStatus status, String message) {
		this.response = new ErrorResponseDTO(message);
		this.httpStatus = status;
	}

	public AccountException(ErrorsEnum response, Throwable innerException) {
		this.httpStatus = response.getHttpStatus();
		this.response = new ErrorResponseDTO(response.getMsg());
	}

	public ResponseEntity<?> toResponseError(ErrorResponseDTO response) {
		return ResponseEntity.status(this.httpStatus).body(response);
	}
	
}