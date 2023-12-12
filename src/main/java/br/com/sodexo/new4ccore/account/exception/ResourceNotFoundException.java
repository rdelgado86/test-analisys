package br.com.sodexo.new4ccore.account.exception;

import org.springframework.http.HttpStatus;

import br.com.sodexo.new4ccore.account.dto.response.ErrorResponseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ResourceNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 5272178090459323213L;
	
	private HttpStatus httpStatus;
    private ErrorResponseDTO response;

    public ResourceNotFoundException(String message) {
        this.response = ErrorResponseDTO.builder()
                .message(message)
                .build();
        this.httpStatus = HttpStatus.NOT_FOUND;
    }

    public ResourceNotFoundException(ErrorResponseDTO error) {
        this.response = error;
        this.httpStatus = HttpStatus.NOT_FOUND;
    }
   
}