package br.com.sodexo.new4ccore.account.controller;

import br.com.sodexo.new4ccore.account.controller.resource.AccountResource;
import br.com.sodexo.new4ccore.account.dto.PaginationDTO;
import br.com.sodexo.new4ccore.account.dto.request.UpdateAccountRequestDTO;
import br.com.sodexo.new4ccore.account.dto.response.GeneralResponseDTO;
import br.com.sodexo.new4ccore.account.dto.response.GetAccountByCCPResponseDTO;
import br.com.sodexo.new4ccore.account.dto.response.GetAccountResponseDTO;
import br.com.sodexo.new4ccore.account.dto.response.UpdateAccountResponseDTO;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import br.com.sodexo.new4ccore.account.service.AccountService;
import br.com.sodexo.new4ccore.account.service.action.BlockAccountService;
import br.com.sodexo.new4ccore.account.service.action.UnblockAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController implements AccountResource {

    @Autowired
    private AccountService accountService;

    @Autowired
    private BlockAccountService blockAccountService;

    @Autowired
    private UnblockAccountService unblockAccountService;

    @Override
    public ResponseEntity<GetAccountResponseDTO> getAccountById(Long accountId,Long productId) throws AccountException {
        GetAccountResponseDTO response =null;
        if(productId ==null)
            response = accountService.getAccountById(accountId);
        else
            response = accountService.getAccountByIdAndProduct(accountId, productId);
       
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @Override
    public ResponseEntity<GetAccountByCCPResponseDTO> getAccountByCCP(Long consumerId, Long customerId, Long productId) throws AccountException {
        GetAccountByCCPResponseDTO response = accountService.getAccountByCCP(consumerId, customerId, productId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @Override
    public ResponseEntity<UpdateAccountResponseDTO> updateAccount(Long accountId, UpdateAccountRequestDTO request, UriComponentsBuilder uriBuilder) throws AccountException {
        UpdateAccountResponseDTO response = accountService.updateAccount(accountId, request);
        return ResponseEntity.ok().body(response);
    }

    @Override
    public ResponseEntity<PaginationDTO<GetAccountResponseDTO>> getAccountsByCustomerId(Long customerId, Integer size, Integer page) {
        return ResponseEntity.ok(accountService.getAccountsByCustomerId(customerId, PageRequest.of(page, size)));
    }

    @Override
    public ResponseEntity<GeneralResponseDTO> blockAccount(Long accountId) throws AccountException {
        return ResponseEntity.ok(blockAccountService.execute(accountId));
    }

    @Override
    public ResponseEntity<GeneralResponseDTO> unblockAccount(Long accountId) throws AccountException {
        return ResponseEntity.ok(unblockAccountService.execute(accountId));
    }

}
