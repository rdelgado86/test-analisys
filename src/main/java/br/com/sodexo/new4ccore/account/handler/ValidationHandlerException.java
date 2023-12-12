package br.com.sodexo.new4ccore.account.handler;

import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import com.fasterxml.jackson.core.JsonParseException;

import br.com.sodexo.new4ccore.account.dto.response.ErrorResponseDTO;
import br.com.sodexo.new4ccore.account.exception.BadRequestException;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import br.com.sodexo.new4ccore.account.exception.InvalidAccountException;
import br.com.sodexo.new4ccore.account.exception.ResourceNotFoundException;

@RestControllerAdvice
public class ValidationHandlerException {
	
	@Autowired
	private MessageSource messageSource;
	
	@ExceptionHandler(TypeMismatchException.class)
    protected ResponseEntity<Object> handleTypeMismatchException(TypeMismatchException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO("Invalid Request."));
    }

	@ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(exception.getMessage()));
    }
	
	@ExceptionHandler(WebExchangeBindException.class)
	protected ResponseEntity<Object> handleWebExchangeBindException(WebExchangeBindException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(exception.getMessage()));
    }
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected List<ErrorResponseDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
		List<ErrorResponseDTO> errorList = new ArrayList<>();
		List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
		fieldErrors.forEach(e -> {
			String message = messageSource.getMessage(e, LocaleContextHolder.getLocale());
			ErrorResponseDTO error = new ErrorResponseDTO(e.getField(), message);
			errorList.add(error);
		});
		return errorList;
	}
	
    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> resourceNotFoundException(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getResponse());
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<Object> badRequestException(BadRequestException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getResponse());
    }

    @ExceptionHandler(AccountException.class)
    protected ResponseEntity<Object> handlerException(AccountException exception) {
    	return ResponseEntity.status(exception.getHttpStatus()).body(exception.getResponse());
    }

    @ExceptionHandler(InvalidAccountException.class)
    protected ResponseEntity<Object> handleInvalidConsumerException(InvalidAccountException exception) {
        return ResponseEntity.status(exception.getHttpStatus()).body(exception.getResponse());
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<Object> jsonParseException(JsonParseException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDTO(exception.getMessage()));        
    }

}
