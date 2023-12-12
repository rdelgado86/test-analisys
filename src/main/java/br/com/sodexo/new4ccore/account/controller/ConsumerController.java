package br.com.sodexo.new4ccore.account.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.sodexo.new4ccore.account.controller.resource.ConsumerResource;
import br.com.sodexo.new4ccore.account.dto.PaginationDTO;
import br.com.sodexo.new4ccore.account.dto.request.CreateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.dto.response.GetAccountResponseDTO;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import br.com.sodexo.new4ccore.account.service.AccountService;

@RestController
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConsumerController implements ConsumerResource {

	@Autowired
	private AccountService accountService;

	@Override
	public ResponseEntity<PaginationDTO<GetAccountResponseDTO>> getAccountList(Long consumerId,String status,Long productId, Integer size, Integer page) throws AccountException {
		PageRequest pageRequest = PageRequest.of(page, size);
		PaginationDTO<GetAccountResponseDTO> response = accountService.getAccountList(consumerId,status, productId, pageRequest);
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
	}

	@Override
	public ResponseEntity<GetAccountResponseDTO> createAccount(Long consumerId, CreateAccountRequestDTO request, UriComponentsBuilder uriBuilder) throws AccountException, Exception {
		GetAccountResponseDTO response = accountService.createAccount(consumerId, request);
		URI uri = uriBuilder.path("{id}").buildAndExpand(response.getId()).toUri();
		return ResponseEntity.created(uri).contentType(MediaType.APPLICATION_JSON).body(response);
	}

}
