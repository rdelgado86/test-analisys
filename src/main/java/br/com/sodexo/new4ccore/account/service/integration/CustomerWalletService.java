package br.com.sodexo.new4ccore.account.service.integration;

import br.com.sodexo.new4ccore.account.dto.response.CustomerWalletListResponseDTO;
import br.com.sodexo.new4ccore.account.enums.ErrorsEnum;
import br.com.sodexo.new4ccore.account.enums.WalletStatusEnum;
import br.com.sodexo.new4ccore.account.exception.AccountException;
import br.com.sodexo.new4ccore.account.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class CustomerWalletService {

    @Value("${multibenefits.customerwallet.base_path}")
    private String basepath;

    @Autowired
    private RestTemplate restTemplate;

	public CustomerWalletListResponseDTO getCustomerWalletList(Long customerId, Long productId) throws AccountException {
        try {
            var urlBuilder = UriComponentsBuilder.fromUriString(basepath + customerId + "/wallets/");
            urlBuilder.queryParam("status", WalletStatusEnum.ACTIVE);
            urlBuilder.queryParam("productId", productId);
			log.info("URL Customer Wallet: " + urlBuilder.toUriString());
			HttpEntity<String> requestEntity = HttpUtils.buildHeader();
            ResponseEntity<CustomerWalletListResponseDTO> response = restTemplate.exchange(
                    urlBuilder.toUriString(),
                    HttpMethod.GET,
                    requestEntity,
                    CustomerWalletListResponseDTO.class
            );
            log.info("Customer Wallet Response: " + response);
            return response.getBody();
        } 
        catch (Exception ex) {
            if (ex instanceof HttpClientErrorException) {
                ErrorsEnum.raise(ErrorsEnum.WALLET_NOT_FOUND);
            }
            log.error("Error: " + ex.getMessage());
            throw ErrorsEnum.INTERNAL_SERVER_ERROR.newError();
        }
	}
}
