package br.com.sodexo.new4ccore.account.service.integration;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.sodexo.new4ccore.account.dto.response.ConsumerResponseDTO;
import br.com.sodexo.new4ccore.account.enums.ErrorsEnum;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import br.com.sodexo.new4ccore.account.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountWalletService {

    @Value("${multibenefits.accountwallet.base_path}")
    private String basepath;

    @Autowired
    private RestTemplate restTemplate;

	public Boolean createAccountWallet(Long accountId, Long walletId) throws AccountException {

        try {
            var urlBuilder = UriComponentsBuilder.fromUriString(basepath + "/accounts/{accountId}/wallets/{walletId}");
            var map = new HashMap<String, String>();
            map.put("accountId", accountId.toString());
            map.put("walletId", walletId.toString());
            var url = urlBuilder.buildAndExpand(map).toString();
            
			log.info("URL AccountWallet: {} ", url);
            HttpEntity<String> requestEntity = HttpUtils.buildHeader();
            ResponseEntity<ConsumerResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    ConsumerResponseDTO.class
            );
            log.info("AccountWallet Response: {} ", response);
            return (response.getStatusCode()==HttpStatus.CREATED);
        }
        catch (Exception ex) {
            log.error("Error: {}", ex.getMessage());
            throw ErrorsEnum.INTERNAL_SERVER_ERROR.newError();
        }
	}
}
