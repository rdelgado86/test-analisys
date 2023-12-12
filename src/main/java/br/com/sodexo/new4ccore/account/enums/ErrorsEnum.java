package br.com.sodexo.new4ccore.account.enums;

import org.springframework.http.HttpStatus;

import br.com.sodexo.new4ccore.account.exception.AccountException;
import lombok.Getter;

public enum ErrorsEnum {

	ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Account not found"),
	CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "Customer not found"),
	CONSUMER_NOT_FOUND(HttpStatus.NOT_FOUND, "Consumer not found"),
	WALLET_NOT_FOUND(HttpStatus.NOT_FOUND, "Wallet not found"),
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),
	ACCOUNT_ALREADY_CANCELLED(HttpStatus.CONFLICT, "Account cancelled."),
	ACCOUNT_ALREADY_ACTIVE(HttpStatus.CONFLICT, "Account active."),
	ACCOUNT_DOCUMENT_ALREADY_EXISTS (HttpStatus.CONFLICT, "Account document already exists."),
	ACCOUNT_ALREADY_EXIST(HttpStatus.NOT_FOUND, "Account already exist."),
	BENEFICIARY_ID_IS_MANDATORY(HttpStatus.BAD_REQUEST, "Beneficiary Id is a mandatory field."),
	EXTERNAL_ACCOUNT_NUMBER_IS_MANDATORY(HttpStatus.BAD_REQUEST, "External Account Number is a mandatory field.");
	
	@Getter
	private HttpStatus httpStatus;
	
	@Getter
	private String msg;

	ErrorsEnum(HttpStatus httpStatus, String msg) {
		this.msg = msg;
		this.httpStatus = httpStatus;
	}

	public AccountException newError() {
		return new AccountException(this, null);
	}

	public static void raise(ErrorsEnum error) throws AccountException {
		throw new AccountException(error.getHttpStatus(), error.getMsg());
	}
	
}